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
import casia.isi.elasticsearch.monitor.model.DeleteTaskKpi;
import casia.isi.elasticsearch.monitor.model.GlobalIndexKpi;
import casia.isi.elasticsearch.monitor.model.SingleIndexKpi;
import casia.isi.elasticsearch.monitor.model.WholeIndexKpi;
import casia.isi.elasticsearch.monitor.util.HTMLUtil;
import casia.isi.elasticsearch.operation.search.EsIndexSearch;
import casia.isi.elasticsearch.util.ClientUtils;
import casia.isi.elasticsearch.util.DateUtil;
import casia.isi.elasticsearch.util.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.alarm
 * @Description: TODO(Elasticsearch STATUS)
 * @date 2019/11/15 16:32
 */
@Service
public class ElasticStatistics {

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
            EsIndexSearch esIndexSearch = new EsIndexSearch(address, "*", type);
            esIndexSearch.execute(new String[]{"it"});
            long total = esIndexSearch.getTotal();
            esIndexSearch.reset();
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
            EsIndexSearch esIndexSearch = new EsIndexSearch(address, indexName, indexType);

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
            String indexType = packIndexType(map.keySet());
            mapping.put(indexName, indexType);
        }
        return mapping;
    }

    private String packIndexType(Set<String> keySet) {
        StringBuilder builder = new StringBuilder();
        for (String type : keySet) {
            builder.append(type + ",");
        }
        int length = builder.length();
        return length > 0 ? builder.substring(0, length - 1) : "";
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

        JSONObject historyDeleteTasks = recentDoneDeleteTask(address);

        // 获取正在运行的删除任务
        JSONObject tasks = currentDeleteTask(address);

        historyDeleteTasks.putAll(tasks);
        return historyDeleteTasks;
    }

    /**
     * @param
     * @return
     * @Description: TODO(最近24H运行的删除任务)
     */
    public JSONObject recentDoneDeleteTask(String address) {
        EsIndexSearch esIndexSearch = new EsIndexSearch(address, ".tasks", "task");

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

    /**
     * @param
     * @return
     * @Description: TODO(集群访问地址)
     */
    public JSONObject getAbnormalDataDescription(String address, String indexType) {
        String indexNames = getIndicesByType(indexType);

        JSONObject stringMapMap = new JSONObject();
        if ("common_caiji".equals(indexType)) {
            // EARLIEST
            stringMapMap.put("最早一条数据", getAbnormalData(address, indexNames, indexType, SortOrder.ASC, "insert_time"));
            // LATEST
            stringMapMap.put("最近一条数据", getAbnormalData(address, indexNames, indexType, SortOrder.DESC, "insert_time"));
        } else {
            // EARLIEST
            stringMapMap.put("最早一条数据", getAbnormalData(address, indexNames, indexType, SortOrder.ASC, "pubtime"));
            // LATEST
            stringMapMap.put("最近一条数据", getAbnormalData(address, indexNames, indexType, SortOrder.DESC, "pubtime"));
        }
        return stringMapMap;
    }

    /**
     * @param
     * @return
     * @Description: TODO(集群访问地址)
     */
    public Map<String, Map<String, String>> getIndicesTimeInfo(String address) {
        Map<String, Map<String, String>> mapMap = new HashMap<>();
        mapMap.put("monitor_caiji_all", getIndicesTimeMap(address, getIndicesByType("monitor_caiji_all")
                , "monitor_caiji_all", "pubtime"));
        mapMap.put("monitor_caiji_small", getIndicesTimeMap(address, getIndicesByType("monitor_caiji_small")
                , "monitor_caiji_small", "pubtime"));
        mapMap.put("monitor_caiji_preprocess", getIndicesTimeMap(address, getIndicesByType("monitor_caiji_preprocess")
                , "monitor_caiji_preprocess", "pubtime"));
        mapMap.put("event_data", getIndicesTimeMap(address, getIndicesByType("event_data")
                , "event_data", "pubtime"));
        mapMap.put("monitor_data", getIndicesTimeMap(address, getIndicesByType("monitor_data")
                , "monitor_data", "pubtime"));
        mapMap.put("zdr_caiji", getIndicesTimeMap(address, getIndicesByType("zdr_caiji")
                , "zdr_caiji", "pubtime"));
        mapMap.put("zdr_data", getIndicesTimeMap(address, getIndicesByType("zdr_data")
                , "zdr_data", "pubtime"));
        mapMap.put("common_caiji", getIndicesTimeMap(address, getIndicesByType("common_caiji")
                , "common_caiji", "insert_time"));
        return mapMap;
    }

    private Map<String, String> getIndicesTimeMap(String address, String indexName, String indexType, String timeField) {
        Map<String, String> map = new HashMap<>();

        // EARLIEST
        map.put("earliest", getPubtime(address, indexName, indexType, SortOrder.ASC, timeField));
        // LATEST
        map.put("latest", getPubtime(address, indexName, indexType, SortOrder.DESC, timeField));

        return map;
    }

    private String getPubtime(String address, String indexName, String indexType, SortOrder sort, String timeFiled) {
        httpAccessor.removeLastHttpsAddNewAddress(address);
        EsIndexSearch esIndexSearch = new EsIndexSearch(address, indexName, indexType);
        esIndexSearch.addSortField(timeFiled, sort);
        esIndexSearch.setRow(1);
        esIndexSearch.execute(new String[]{timeFiled});
        List<String[]> result = esIndexSearch.getResults();
        String time = null;
        for (String[] info : result) {
            time = info[0];
        }
        esIndexSearch.reset();
        return time;
    }

    private JSONObject getAbnormalData(String address, String indexName, String indexType, SortOrder sort, String timeFiled) {
        httpAccessor.removeLastHttpsAddNewAddress(address);
        EsIndexSearch esIndexSearch = new EsIndexSearch(address, indexName, indexType);
        esIndexSearch.addSortField(timeFiled, sort);
        esIndexSearch.setRow(1);
        esIndexSearch.execute(new String[]{timeFiled, "it", "gid", "data_source", "url", "md5", "user_url", "blogger_id", "blogger"});
        JSONObject jsonObject = JSONObject.parseObject(esIndexSearch.queryJsonResult.toJSONString());
        esIndexSearch.reset();

        JSONObject search = new JSONObject();
        search.put("queryUrl", esIndexSearch.queryUrl);
        search.put("query", esIndexSearch.queryJson);
        jsonObject.put("search", search);
        return jsonObject;
    }

    /**
     * @param
     * @return
     * @Description: TODO(获取HTML的统计数据)
     */
    public String getHTMLInStatistics(String address) {
        httpAccessor.removeLastHttpsAddNewAddress(address);
        // KPI生成HTML
        String globalIndexKpi = HTMLUtil.globalIndexKpi(globalKpi(address));
        String wholeIndexKpi = HTMLUtil.wholeIndexKpi(wholeIndexKpi(address));
        String singleIndexKpi = HTMLUtil.singleIndexKpi(singleIndexKpi(address));
        String deleteTaskKpi = HTMLUtil.deleteTaskKpi(deleteTaskKpi(address));

        String html = HTMLUtil.getStatisticsHtml()
                .replace("★★★globalIndexKpi★★★", globalIndexKpi)
                .replace("★★★wholeIndexKpi★★★", wholeIndexKpi)
                .replace("★★★singleIndexKpi★★★", singleIndexKpi)
                .replace("★★★deleteTaskKpi★★★", deleteTaskKpi);
        return html;
    }

    public List<DeleteTaskKpi> deleteTaskKpi(String address) {
        httpAccessor.removeLastHttpsAddNewAddress(address);
        List<DeleteTaskKpi> deleteTaskKpiList = new ArrayList<>();
        JSONObject object = recentDoneDeleteTask(address);
        for (String key : object.keySet()) {

            String deleteTaskName = key;
            JSONObject obj = object.getJSONObject(key);
            int count = obj.getIntValue("count");
            boolean isCompleted = obj.getBooleanValue("completed");
            boolean isCancellable = obj.getBooleanValue("cancellable");
            String locNode = obj.getString("node");
            String taskID = obj.getString("lastTaskId");
            String start_time = obj.getString("start_time");
            long running_time_in_nanos = obj.getLongValue("running_time_in_nanos");
            deleteTaskKpiList.add(new DeleteTaskKpi(deleteTaskName, count, isCompleted, isCancellable, locNode, taskID, start_time, running_time_in_nanos));
        }
        return deleteTaskKpiList;
    }

    public List<SingleIndexKpi> singleIndexKpi(String address) {
        httpAccessor.removeLastHttpsAddNewAddress(address);
        List<SingleIndexKpi> singleIndexKpiList = new ArrayList<>();
        JSONObject mapping = getAllIndices(address);
        for (Map.Entry entry : mapping.entrySet()) {
            String indexName = String.valueOf(entry.getKey());
            String indexType = String.valueOf(entry.getValue());

            int total = getTotal(address, indexName);

            String time = DateUtil.millToTimeStr(System.currentTimeMillis()).split(" ")[0] + " 00:00:00";
            int yesterdayDataCount = getDataCountMap(address, indexType, indexName, DateUtil.dateSub(time, 86_400_000), time);
            int todayDataCount = getDataCountMap(address, indexType, indexName, time, DateUtil.millToTimeStr(System.currentTimeMillis()));
            String earliestTime;
            String latestTime;
            if ("common_caiji".equals(indexType)) {
                Map<String, String> map = getIndicesTimeMap(address, indexName, indexType, "insert_time");
                // EARLIEST
                earliestTime = map.get("earliest");
                // LATEST
                latestTime = map.get("latest");
            } else {
                Map<String, String> map = getIndicesTimeMap(address, indexName, indexType, "pubtime");
                // EARLIEST
                earliestTime = map.get("earliest");
                // LATEST
                latestTime = map.get("latest");
            }
            singleIndexKpiList.add(new SingleIndexKpi(indexType, indexName, total, yesterdayDataCount, todayDataCount, earliestTime, latestTime));
        }
        return singleIndexKpiList;
    }

    public List<WholeIndexKpi> wholeIndexKpi(String address) {
        httpAccessor.removeLastHttpsAddNewAddress(address);
        List<WholeIndexKpi> wholeIndexKpiList = new ArrayList<>();
        Map<String, Map<String, String>> mapMap = getIndicesTimeInfo(address);
        for (String key : mapMap.keySet()) {
            String indexType = key;
            String alias = getAlias(key);
            Map<String, String> timeMap = mapMap.get(key);
            String earliestTime = timeMap.get("earliest");
            String latestTime = timeMap.get("latest");

            int total = getTotal(address, getIndicesByType(indexType));
            String proportion = calPorportion(address, total);
            String time = DateUtil.millToTimeStr(System.currentTimeMillis()).split(" ")[0] + " 00:00:00";
            int yesterdayDataCount = getDataCountMap(address, indexType, getIndicesByType(indexType), DateUtil.dateSub(time, 86_400_000), time);
            int todayDataCount = getDataCountMap(address, indexType, getIndicesByType(indexType), time, DateUtil.millToTimeStr(System.currentTimeMillis()));

            wholeIndexKpiList.add(new WholeIndexKpi(indexType, alias, total, proportion, yesterdayDataCount, todayDataCount, earliestTime, latestTime));
        }
        return wholeIndexKpiList;
    }

    /**
     * @param
     * @return
     * @Description: TODO(与集群所有索引数据量求比例)
     */
    public String calPorportion(String address, int total) {
        JSONObject object = JSONObject.parseObject(getClusterBasisInfo(address));
        int docs = object.getIntValue("docs");
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(2);
        String result = numberFormat.format((float) total / (float) docs * 100);
        return result + "%";
    }

    /**
     * @param
     * @return
     * @Description: TODO(索引数据量总和)
     */
    public int getTotal(String address, String indices) {
        httpAccessor.removeLastHttpsAddNewAddress(address);
        String info = httpAccessor.catIndicesInfo(indices, "docs.count");
        JSONArray object = JSONArray.parseArray(info);
        int count = 0;
        for (Object obj : object) {
            JSONObject jsonObject = (JSONObject) obj;
            count += jsonObject.getIntValue("docs.count");
        }
        return count;
    }

    public int getDataCountMap(String address, String indexType, String indexName, String startTime, String stopTime) {
        EsIndexSearch esIndexSearch = new EsIndexSearch(address, indexName, indexType);
        httpAccessor.removeLastHttpsAddNewAddress(address);
        esIndexSearch.addRangeTerms(SysConstant.ELASTICSEARCH_TIME_FIELD, startTime, stopTime);
        List<String[]> result = esIndexSearch.facetDate(SysConstant.ELASTICSEARCH_TIME_FIELD, "yyyy-MM-dd", "1d");
        JSONArray array = new JSONArray();
        if (result == null || result.isEmpty()) {
            array.add(packStatistics(indexName, indexType, stopTime.split(" ")[0], 0));
        } else {
            for (String[] infos : result) {
                array.add(packStatistics(indexName, indexType, infos[0], Integer.parseInt(infos[1])));
            }
        }
        esIndexSearch.reset();
        return array.getJSONObject(0).getIntValue("count");
    }

    private GlobalIndexKpi globalKpi(String address) {
        httpAccessor.removeLastHttpsAddNewAddress(address);
        String info = getClusterBasisInfo(address);
        JSONObject object = JSONObject.parseObject(info);
        String clusterName = object.getString("cluster_name_status");
        int total = object.getIntValue("docs");
        String store = object.getString("store");
        int shardsCount = object.getIntValue("shards");
        int nodeCount = object.getIntValue("nodes");
        int indicesCount = object.getIntValue("indices");
        JSONObject deleteObj = recentDoneDeleteTask(address);
        int deleteTaskCount = deleteObj.getIntValue("count");
        return new GlobalIndexKpi(clusterName, total, store, shardsCount, nodeCount, indicesCount, deleteTaskCount);
    }

    private String getAlias(String key) {
        Map<String, String> map = new HashMap<>();
        map.put("monitor_caiji_all", "大索引");
        map.put("monitor_caiji_small", "小索引");
        map.put("monitor_caiji_preprocess", "预处理索引");
        map.put("event_data", "专题索引");
        map.put("monitor_data", "预警索引");
        map.put("zdr_caiji", "ZDR采集索引");
        map.put("zdr_data", "ZDR预处理索引");
        map.put("common_caiji", "用户信息索引");
        map.put("其它索引", "非核心索引类型");
        String alias = map.get(key);
        if (alias == null || "".equals(alias)) return "非核心索引类型";
        return map.get(key);
    }

    private String getIndicesByType(String indexType) {
        Map<String, String> map = new HashMap<>();
        map.put("monitor_caiji_all", "think_tank_all,video_info_all,forum_threads_all,instagram_thread_all,twitter_info_all,youtube_info_all,facebook_info_all,mblog_info_all,wechat_info_all,appdata_all,newspaper_all,news_all,blog_all,platform_info_all");
        map.put("monitor_caiji_small", "think_tank_small,video_info_small,forum_threads_small,instagram_thread_small,twitter_info_small,youtube_info_small,facebook_info_small,mblog_info_small,wechat_info_small,appdata_small,newspaper_small,news_small,blog_small,platform_info_small");
        map.put("monitor_caiji_preprocess", "video_info_preprocess,forum_threads_preprocess,instagram_thread_preprocess,twitter_info_preprocess,youtube_info_preprocess,facebook_info_preprocess,mblog_info_preprocess,wechat_info_preprocess,appdata_preprocess,newspaper_preprocess,news_preprocess,blog_preprocess,platform_info_preprocess");
        map.put("event_data", "event_appdata_ref_event,event_blog_ref_event,event_facebook_info_ref_event,event_forum_threads_ref_event,event_instagram_thread_ref_event,event_mblog_info_ref_event,event_news_ref_event,event_newspaper_ref_event,event_platform_info_ref_event,event_twitter_info_ref_event,event_video_info_ref_event,event_wechat_info_ref_event,event_youtube_info_ref_event");
        map.put("monitor_data", "event_appdata_ref_monitor,event_blog_ref_monitor,event_facebook_info_ref_monitor,event_forum_threads_ref_monitor,event_instagram_thread_ref_monitor,event_mblog_info_ref_monitor,event_news_ref_monitor,event_newspaper_ref_monitor,event_platform_info_ref_monitor,event_twitter_info_ref_monitor,event_video_info_ref_monitor,event_wechat_info_ref_monitor,event_youtube_info_ref_monitor");
        map.put("zdr_caiji", "blog_comment_zdr,facebook_comment_zdr,forum_replys_zdr,instagram_comment_zdr,mblog_comment_zdr,twitter_comment_zdr,wechat_comment_zdr,youtube_comment_zdr");
        map.put("zdr_data", "user_forum_threads_ref_zdr,user_instagram_thread_ref_zdr,user_twitter_info_ref_zdr,user_youtube_info_ref_zdr,user_facebook_info_ref_zdr,user_mblog_info_ref_zdr,user_wechat_info_ref_zdr,user_blog_ref_zdr,user_linkedin_thread_ref_zdr");
        map.put("common_caiji", "bloggers,facebook_users,forum_users,instagram_users,interlocution_users,linkedin_users,mblog_userinfo,platform_users,twitter_users,video_users,wechat_users,youtube_users");
        String indices = map.get(indexType);
        if (indices == null || "".equals(indices)) return "*";
        return map.get(indexType);
    }
}


