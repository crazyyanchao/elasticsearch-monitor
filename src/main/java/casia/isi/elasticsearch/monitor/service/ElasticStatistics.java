package casia.isi.elasticsearch.monitor.service;
/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ +
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 */

import casia.isi.elasticsearch.common.FieldOccurs;
import casia.isi.elasticsearch.common.RangeOccurs;
import casia.isi.elasticsearch.common.SortOrder;
import casia.isi.elasticsearch.monitor.common.EsUrl;
import casia.isi.elasticsearch.monitor.common.SysConstant;
import casia.isi.elasticsearch.operation.http.HttpDiscoverRegister;
import casia.isi.elasticsearch.operation.http.HttpPoolSym;
import casia.isi.elasticsearch.operation.http.HttpProxyRegister;
import casia.isi.elasticsearch.operation.http.HttpProxyRequest;
import casia.isi.elasticsearch.operation.search.EsIndexSearch;
import casia.isi.elasticsearch.util.ClientUtils;
import casia.isi.elasticsearch.util.DateUtil;
import casia.isi.elasticsearch.util.MD5Digest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.alarm
 * @Description: TODO(Elasticsearch STATUS)
 * @date 2019/11/15 16:32
 */
@Service
public class ElasticStatistics {

    public HttpProxyRequest request = new HttpProxyRequest(HttpPoolSym.DEFAULT.getSymbolValue());

    private EsIndexSearch esIndexSearch;

    public ElasticStatistics() {
    }

    /**
     * @param
     * @return
     * @Description: TODO(集群整体情况)
     */
    public String statistics() {
        String queryResult = request.httpGet(ClientUtils.referenceUrl(EsUrl._cluster_state_all.url()));
        return queryResult;
    }

    /**
     * @param
     * @return
     * @Description: TODO(最近七天的索引数据量统计)
     */
    public JSONArray recentSeveralDaysIndexStatus() {
        String stopTime = DateUtil.millToTimeStr(System.currentTimeMillis());
        String startTime = DateUtil.dateSub(stopTime, 7 * 86_400_000); // 7*24H
        return daysIndexStatus(startTime, stopTime);
    }

    private JSONObject packStatistics(String indexName, String indexType, String date, int count) {
        JSONObject object = new JSONObject();
        object.put("INDEX-NAME", indexName);
        object.put("INDEX-TYPE", indexType);
        object.put("date", date);
        object.put("count", count);
        return object;
    }

    public JSONArray daysIndexStatus(String startTime, String stopTime) {
        JSONArray array = new JSONArray();
        JSONObject mapping = getAllIndices();
        for (Map.Entry entry : mapping.entrySet()) {
            String indexName = String.valueOf(entry.getKey());
            String indexType = String.valueOf(entry.getValue());
            esIndexSearch = new EsIndexSearch(SysConstant.ELASTICSEARCH_ADDRESS, indexName, indexType);

            esIndexSearch.addRangeTerms(SysConstant.ELASTICSEARCH_TIME_FIELD, startTime, stopTime);
            List<String[]> result = esIndexSearch.facetDate(SysConstant.ELASTICSEARCH_TIME_FIELD, "yyyy-MM-dd", "1d");

            if (result == null || result.isEmpty()) {
                array.add(packStatistics(indexName, indexType, stopTime.split(" ")[0], 0));
            } else {
                for (String[] infos : result) {
                    array.add(packStatistics(indexName, indexType, infos[0], Integer.parseInt(infos[1])));
                }
            }
            esIndexSearch.reset();

        }
        return array;
    }

    /**
     * @param
     * @return
     * @Description: TODO(最近一天的索引数据量统计)
     */
    public JSONArray recentOneDaysIndexStatus() {
        String stopTime = DateUtil.millToTimeStr(System.currentTimeMillis());
        String startTime = DateUtil.dateSub(stopTime, 86_400_000); // 24H
        return daysIndexStatus(startTime, stopTime);
    }

    /**
     * @param
     * @return
     * @Description: TODO(所有索引 - 索引类型和索引名)
     */
    public JSONObject getAllIndices() {
        String queryResult = request.httpGet(ClientUtils.referenceUrl(EsUrl._mapping.url()));
        JSONObject object = JSONObject.parseObject(queryResult);
        JSONObject mapping = new JSONObject();
        for (String indexName : object.keySet()) {
            JSONObject map = object.getJSONObject(indexName).getJSONObject("mappings");
            String indexType = map.keySet().stream().findFirst().get();
            mapping.put(indexName, indexType);
        }
        return mapping;
    }

    public JSONObject statisticsToAlarm() {
        // 最近七天的数据量
        JSONArray severalDays = recentSeveralDaysIndexStatus();

        // 最近24小时的数据量
        JSONArray oneDay = recentOneDaysIndexStatus();

        JSONObject statistics = new JSONObject();
        statistics.put("SEVERAL_DAYS_STATISTICS", severalDays);
        statistics.put("CRAWL_STATISTICS_24H", oneDay);
        statistics.put("ALARM_CRAWL_EMPTY_24H", filterEmptyCountIndex(oneDay));
        statistics.put("explain", "最近24小时没有进入新数据的索引[ALARM_CRAWL_EMPTY_24H]");
        return statistics;
    }

    private JSONArray filterEmptyCountIndex(JSONArray oneDay) {
        return oneDay.stream().filter(v -> {
            JSONObject obj = (JSONObject) v;
            int count = obj.getIntValue("count");
            return count == 0;
        }).map(v -> JSONObject.parseObject(JSON.toJSONString(v)))
                .collect(Collectors.toCollection(JSONArray::new));
    }

    /**
     * @param
     * @return
     * @Description: TODO(删除任务的执行状态)
     */
    public JSONObject detectDeleter() {
        esIndexSearch = new EsIndexSearch(SysConstant.ELASTICSEARCH_ADDRESS, ".tasks", "task");

        // 获取最近24H的任务
        long millTime = System.currentTimeMillis() - 86_400_000L;
        esIndexSearch.addRangeTerms("task.start_time_in_millis", String.valueOf(millTime), FieldOccurs.MUST, RangeOccurs.GTE);
        esIndexSearch.addSortField("task.start_time_in_millis", SortOrder.ASC);
        esIndexSearch.setRow(10000);
        esIndexSearch.execute(new String[]{"task.description", "task.start_time_in_millis", "task.node", "task.id", "task.type",
                "task.action", "task.running_time_in_nanos", "task.cancellable", "task.parent_task_id", "task.status", "response"});
        JSONObject result = esIndexSearch.queryJsonResult;
        JSONArray hits = result.getJSONObject("hits").getJSONArray("hits");
        esIndexSearch.reset();
        return countDeleteQueryTask(hits);
    }

    private JSONObject countDeleteQueryTask(JSONArray hits) {
        JSONObject md5Task = new JSONObject();
        for (Object object : hits) {
            JSONObject task = (JSONObject) object;
            String taskId = task.getString("_id");
            JSONObject taskObj = task.getJSONObject("_source").getJSONObject("task");
            JSONObject responseObj = task.getJSONObject("_source").getJSONObject("response");
            taskObj.put("response", responseObj);
            String md5 = MD5Digest.MD5(taskObj.getString("description"));
            taskObj.put("task_id", taskId);
            // TRANSFER TIME
            taskObj.put("start_time", DateUtil.millToTimeStr(taskObj.getLongValue("start_time_in_millis")));
            if (md5Task.containsKey(md5)) {
                JSONObject taskCur = md5Task.getJSONObject(md5);
                taskObj.put("count", taskCur.getInteger("count") + 1);
                md5Task.put(md5, taskObj);
            } else {
                taskObj.put("count", 1);
                md5Task.put(md5, taskObj);
            }
        }
        return md5Task;
    }

    /**
     * @param
     * @return
     * @Description: TODO(重置HTTP模块 - 将上一次注册的地址移除 ， 并加入新的集群地址)
     */
    public void removeLastHttpsAddNewAddress(String ipPorts) {
        boolean status;
        do {
            status = HttpDiscoverRegister.discover(ipPorts);
        } while (!status);
    }

    public String getReportText() {
        JSONObject deleterObj = detectDeleter();
        JSONObject statisticsObj = statisticsToAlarm();
        return new StringBuilder()
                .append("❤集群索引数量：" + getAllIndices().size() + "\r\n")
                .append("❤24H运行的数据删除任务数量：" + deleterObj.size() + "\r\n")
                .append("❤24H数据删除任务详细信息：" + deleterObj.toJSONString() + "\r\n")
                .append("❤集群所有索引数据量统计：" + statisticsObj.toJSONString() + "\r\n")
                .toString();
    }

}


