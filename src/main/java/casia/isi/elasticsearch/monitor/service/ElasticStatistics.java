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
import casia.isi.elasticsearch.monitor.common.HttpAccessor;
import casia.isi.elasticsearch.monitor.common.SysConstant;
import casia.isi.elasticsearch.monitor.common.TaskAction;
import casia.isi.elasticsearch.operation.search.EsIndexSearch;
import casia.isi.elasticsearch.util.ClientUtils;
import casia.isi.elasticsearch.util.DateUtil;
import casia.isi.elasticsearch.util.FileUtil;
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

    private EsIndexSearch esIndexSearch;

    private HttpAccessor httpAccessor;

    public ElasticStatistics() {
        this.httpAccessor = new HttpAccessor();
    }

    /**
     * @param
     * @return
     * @Description: TODO(集群整体情况)
     */
    public String getClusterBasisInfo(String address) {
        httpAccessor.removeLastHttpsAddNewAddress(address);
        String queryResult = httpAccessor.request.httpGet(ClientUtils.referenceUrl(EsUrl._cluster_stats.url()));
        JSONObject result = JSONObject.parseObject(queryResult);
        // 集群名称/状态 节点数量(nodes) 索引数量(indices) 分片总数(shards) 总体数据量(docs) 占用存储大小GB
        JSONObject stats = new JSONObject();
        stats.put("status", result.getString("status"));
        stats.put("cluster_name", result.getString("cluster_name"));
        stats.put("cluster_name_status", result.getString("cluster_name") + "(" + result.getString("status") + ")");
        stats.put("nodes", result.getJSONObject("_nodes").getIntValue("total"));
        JSONObject indices = result.getJSONObject("indices");
        stats.put("indices", indices.getIntValue("count"));
        stats.put("shards", indices.getJSONObject("shards").getIntValue("total"));
        stats.put("docs", indices.getJSONObject("docs").getIntValue("count"));
        stats.put("store", FileUtil.convertFileSizeDescription(indices.getJSONObject("store").getLongValue("size_in_bytes")));
        return stats.toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(集群整体情况)
     */
    public String getClusterAverageBasisInfo(String address) {
        httpAccessor.removeLastHttpsAddNewAddress(address);
        String queryResult = httpAccessor.request.httpGet(ClientUtils.referenceUrl(EsUrl._cluster_stats.url()));
        JSONObject result = JSONObject.parseObject(queryResult);
        // 集群名称/状态 节点数量(nodes) 索引数量(indices) 分片总数(shards) 总体数据量(docs) 占用存储大小GB
        JSONObject stats = new JSONObject();
        stats.put("status", result.getString("status"));
        stats.put("cluster_name", result.getString("cluster_name"));
        stats.put("cluster_name_status", result.getString("cluster_name") + "(" + result.getString("status") + ")");
        stats.put("nodes", result.getJSONObject("_nodes").getIntValue("total"));
        JSONObject indices = result.getJSONObject("indices");
        stats.put("cpu", 0.23);
        stats.put("heap", 0.40);
        stats.put("disk", 0.56);
        return stats.toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(各类型数据量统计)
     */
    public String getClusterPieStatisticsInfo(String address) {
        httpAccessor.removeLastHttpsAddNewAddress(address);
        String[] indexType = new String[]{"monitor_caiji_all", "monitor_caiji_small", "common_caiji", "event_data",
                "monitor_data", "zdr_caiji", "zdr_data", "monitor_caiji_preprocess"};
        JSONObject stats = new JSONObject();
        long count = 0;
        long zdrCount = 0;
        for (String type : indexType) {
            esIndexSearch = new EsIndexSearch(address, "*", type);
            esIndexSearch.execute(new String[]{"it"});
            long total = esIndexSearch.getTotal();
            if ("monitor_caiji_all".equals(type)) {
                stats.put("all", total);
            } else if ("monitor_caiji_small".equals(type)) {
                stats.put("small", total);
            } else if ("common_caiji".equals(type)) {
                stats.put("user", total);
            } else if ("event_data".equals(type)) {
                stats.put("event", total);
            } else if ("monitor_data".equals(type)) {
                stats.put("monitor", total);
            } else if ("monitor_caiji_preprocess".equals(type)) {
                stats.put("preprocess", total);
            } else {
                zdrCount += total;
            }
            count += total;
        }
        JSONObject statistics = JSONObject.parseObject(getClusterBasisInfo(address));
        long docs = statistics.getLongValue("docs") - count;
        stats.put("other", docs);
        stats.put("zdr", zdrCount);
        return stats.toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(最近七天的索引数据量统计)
     */
    public JSONArray recentSeveralDaysIndexStatus(String address) {
        String stopTime = DateUtil.millToTimeStr(System.currentTimeMillis());
        String startTime = DateUtil.dateSub(stopTime, 7 * 86_400_000); // 7*24H
        return daysIndexStatus(address, startTime, stopTime);
    }

    private JSONObject packStatistics(String indexName, String indexType, String date, int count) {
        JSONObject object = new JSONObject();
        object.put("INDEX-NAME", indexName);
        object.put("INDEX-TYPE", indexType);
        object.put("date", date);
        object.put("count", count);
        return object;
    }

    public JSONArray daysIndexStatus(String address, String startTime, String stopTime) {

        // 重置HTTP模块 - 将上一次注册的地址移除 ， 并加入新的集群地址
        httpAccessor.removeLastHttpsAddNewAddress(address);

        JSONArray array = new JSONArray();
        JSONObject mapping = getAllIndices(address);
        for (Map.Entry entry : mapping.entrySet()) {
            String indexName = String.valueOf(entry.getKey());
            String indexType = String.valueOf(entry.getValue());
            esIndexSearch = new EsIndexSearch(address, indexName, indexType);

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
    public JSONArray recentOneDaysIndexStatus(String address) {
        String stopTime = DateUtil.millToTimeStr(System.currentTimeMillis());
        String startTime = DateUtil.dateSub(stopTime, 86_400_000); // 24H
        return daysIndexStatus(address, startTime, stopTime);
    }

    /**
     * @param
     * @return
     * @Description: TODO(所有索引 - 索引类型和索引名)
     */
    public JSONObject getAllIndices(String address) {
        // 先重置集群地址

        httpAccessor.removeLastHttpsAddNewAddress(address);

        String queryResult = httpAccessor.request.httpGet(ClientUtils.referenceUrl(EsUrl._mapping.url()));
        JSONObject object = JSONObject.parseObject(queryResult);
        JSONObject mapping = new JSONObject();
        for (String indexName : object.keySet()) {
            JSONObject map = object.getJSONObject(indexName).getJSONObject("mappings");
            String indexType = map.keySet().stream().findFirst().get();
            mapping.put(indexName, indexType);
        }
        return mapping;
    }

    public JSONObject statisticsToAlarm(String address) {
        // 最近七天的数据量
        JSONArray severalDays = recentSeveralDaysIndexStatus(address);

        // 最近24小时的数据量
        JSONArray oneDay = recentOneDaysIndexStatus(address);

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
    public JSONObject detectDeleter(String address) {

        esIndexSearch = new EsIndexSearch(address, ".tasks", "task");

        // 重置HTTP模块 - 将上一次注册的地址移除 ， 并加入新的集群地址
        esIndexSearch.removeLastHttpsAddNewAddress(address); // 重置集群HTTP池

        // 获取最近24H的任务
        long millTime = System.currentTimeMillis() - 86_400_000L;
        esIndexSearch.addRangeTerms("task.start_time_in_millis", String.valueOf(millTime), FieldOccurs.MUST, RangeOccurs.GTE);
        esIndexSearch.addSortField("task.start_time_in_millis", SortOrder.ASC);
        esIndexSearch.setRow(10000);
        esIndexSearch.execute(new String[]{"task.description", "task.start_time_in_millis", "task.node", "task.id", "task.type",
                "task.action", "task.running_time_in_nanos", "task.cancellable", "task.parent_task_id", "task.status", "response", "completed"});
        JSONObject result = esIndexSearch.queryJsonResult;
        JSONArray hits = result.getJSONObject("hits").getJSONArray("hits");
        esIndexSearch.reset();
        JSONObject historyDeleteTasks = countDeleteQueryTask(hits);

        // 获取正在运行的删除任务
        JSONObject tasks = currentDeleteTask(address);

        historyDeleteTasks.putAll(tasks);
        return historyDeleteTasks;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取正在运行的删除任务)
     */
    private JSONObject currentDeleteTask(String address) {
        JSONObject result = clusterDeleteTask(address).getJSONObject("nodes");

        JSONObject md5Task = new JSONObject();
        for (Map.Entry entry : result.entrySet()) {
            JSONObject object = (JSONObject) entry.getValue();
            JSONObject tasks = object.getJSONObject("tasks");
            for (Map.Entry entry1 : tasks.entrySet()) {
                String currentTaskId = String.valueOf(entry1.getKey());
                JSONObject taskObj = (JSONObject) entry1.getValue();

                String action = taskObj.getString("action");
                if (TaskAction._delete_by_query.getSymbol().contains(action)) {
                    JSONObject taskDetail = getTaskDetailById(address, currentTaskId);
                    taskObj.put("completed", taskDetail.getBooleanValue("taskDetail"));
                    JSONObject task = taskDetail.getJSONObject("task");

//                    String md5 = MD5Digest.MD5(task.getString("description"));
                    taskObj.put("lastTaskId", currentTaskId);
                    String md5 = task.getString("description");
                    taskObj.putAll(task);
                    if (md5Task.containsKey(md5)) {
                        JSONObject taskCur = md5Task.getJSONObject(md5);
                        taskObj.put("count", taskCur.getInteger("count") + 1);
                        md5Task.put(md5, taskObj);
                    } else {
                        taskObj.put("count", 1);
                        md5Task.put(md5, taskObj);
                    }
                }
            }
        }
        return md5Task;
    }

    /**
     * @param
     * @return
     * @Description: TODO(根据任务ID获取任务详情)
     */
    public JSONObject getTaskDetailById(String address, String taskId) {

        // 重置HTTP模块 - 将上一次注册的地址移除 ， 并加入新的集群地址
        httpAccessor.removeLastHttpsAddNewAddress(address); // 重置集群HTTP池
        String queryResult = httpAccessor.request.httpGet(EsUrl._tasks.url() + "/" + taskId);
        return JSONObject.parseObject(queryResult);
    }

    /**
     * @param
     * @return
     * @Description: TODO(查看集群上正在运行的删除任务)
     */
    public JSONObject clusterDeleteTask(String address) {

        // 重置HTTP模块 - 将上一次注册的地址移除 ， 并加入新的集群地址
        httpAccessor.removeLastHttpsAddNewAddress(address); // 重置集群HTTP池
        String queryResult = httpAccessor.request.httpGet(EsUrl._tasks.url());
        return JSONObject.parseObject(queryResult);
    }

    private JSONObject countDeleteQueryTask(JSONArray hits) {
        JSONObject md5Task = new JSONObject();
        for (Object object : hits) {
            JSONObject task = (JSONObject) object;
            String taskId = task.getString("_id");
            JSONObject taskObj = task.getJSONObject("_source").getJSONObject("task");
            JSONObject responseObj = task.getJSONObject("_source").getJSONObject("response");
            taskObj.put("response", responseObj);
//            String md5 = MD5Digest.MD5(taskObj.getString("description"));
            String md5 = taskObj.getString("description");

            taskObj.put("lastTaskId", taskId);
            taskObj.put("completed", task.getJSONObject("_source").getBooleanValue("completed"));
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

    public String getReportText(String address) {
        JSONObject deleterObj = detectDeleter(address);
        JSONObject statisticsObj = statisticsToAlarm(address);
        return new StringBuilder()
                .append("❤集群索引数量：" + getAllIndices(address).size() + "\r\n")
                .append("❤24H运行的数据删除任务数量：" + deleterObj.size() + "\r\n")
                .append("❤24H数据删除任务详细信息：" + deleterObj.toJSONString() + "\r\n")
                .append("❤集群所有索引数据量统计：" + statisticsObj.toJSONString() + "\r\n")
                .toString();
    }

}


