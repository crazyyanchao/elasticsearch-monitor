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

import casia.isi.elasticsearch.monitor.common.EsUrl;
import casia.isi.elasticsearch.monitor.common.SysConstant;
import casia.isi.elasticsearch.operation.http.HttpPoolSym;
import casia.isi.elasticsearch.operation.http.HttpProxyRegister;
import casia.isi.elasticsearch.operation.http.HttpProxyRequest;
import casia.isi.elasticsearch.operation.search.EsIndexSearch;
import casia.isi.elasticsearch.util.ClientUtils;
import casia.isi.elasticsearch.util.DateUtil;
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
        HttpProxyRegister.register(SysConstant.ELASTICSEARCH_ADDRESS);
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
        statistics.put("explain","最近24小时没有进入新数据的索引[ALARM_CRAWL_EMPTY_24H]");
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

}


