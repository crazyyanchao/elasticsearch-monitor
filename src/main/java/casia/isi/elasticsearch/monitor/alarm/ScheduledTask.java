package casia.isi.elasticsearch.monitor.alarm;
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
import casia.isi.elasticsearch.monitor.common.EsUrl;
import casia.isi.elasticsearch.monitor.common.SysConstant;
import casia.isi.elasticsearch.monitor.entity.MailBean;
import casia.isi.elasticsearch.monitor.service.ElasticStatistics;
import casia.isi.elasticsearch.monitor.service.MailService;
import casia.isi.elasticsearch.operation.delete.EsIndexDelete;
import casia.isi.elasticsearch.operation.index.EsIndexCreat;
import casia.isi.elasticsearch.operation.search.EsIndexSearch;
import casia.isi.elasticsearch.util.DateUtil;
import casia.isi.elasticsearch.util.FileUtil;
import casia.isi.elasticsearch.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.alarm
 * @Description: TODO(SCHEDULE TASK)
 * @date 2019/11/15 17:14
 */
@Component
public class ScheduledTask {

//    0 * * * * ?	每分钟触发
//
//    0 0 * * * ? 每小时整触发
//
//    0 0 4 * * ?	每天凌晨4点触发
//
//    0 15 10 * * ?	每天早上10：15触发
//
//    */5 * * * * ?	每隔5秒触发
//
//    0 */5 * * * ?	每隔5分钟触发
//
//    0 0 4 1 * ?	每月1号凌晨4点触发
//
//    0 0 4 L * ?	每月最后一天凌晨3点触发
//
//    0 0 3 ? * L	每周星期六凌晨3点触发
//
//    0 11,22,33 * * * ?	每小时11分、22分、33分触发
//
//    0/10 * * * * ? 每10秒执行一次

    @Autowired
    private MailService mailService;

    @Autowired
    private ElasticStatistics elastic;

    private EsIndexCreat esIndexCreat = new EsIndexCreat(SysConstant.ELASTICSEARCH_ADDRESS, SysConstant._MONITOR_TASK_INDEX_NAME
            , SysConstant._MONITOR_TASK_INDEX_TYPE);
    private EsIndexDelete esIndexDelete = new EsIndexDelete(SysConstant.ELASTICSEARCH_ADDRESS, SysConstant._MONITOR_TASK_INDEX_NAME
            , SysConstant._MONITOR_TASK_INDEX_TYPE);
    private EsIndexSearch esIndexSearch = new EsIndexSearch(SysConstant.ELASTICSEARCH_ADDRESS, ".tasks", "task");

    @Scheduled(cron = "0 00 05 * * ?") // 每天凌晨5点触发
//    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    public void scheduledTaskByCorn() {
        // 1、集群所有索引数据量按日统计；2、集群预警的一些基本信息
        statisticsTask();
    }

//    // TASK RECORD任务
//    @Scheduled(fixedRate = 1000) //1000=1s 每5秒执行一次TASK RECORD
//    public void scheduledTaskByFixedRate() throws Exception {
//        monitorBackstageTask();
//    }

    // TASK RECORD DELETE任务
//    @Scheduled(cron = "0 30 04 * * ?") // 每天凌晨4点30分触发
//    @Scheduled(fixedRate = 5000) //每5秒执行一次
//    public void deleteMonitorBackstageTaskIndex() {
//        deleteMonitorBackstageTask();
//    }

//
//    @Scheduled(fixedDelay = 10000) //每10秒执行一次
//    public void scheduledTaskByFixedDelay() {
//        scheduledTask();
//    }

    /**
     * @param
     * @return
     * @Description: TODO(统计集群索引的数据量 - 按天统计和最近一段时间)
     */
    private void statisticsTask() {
        MailBean mailBean = new MailBean();
        mailBean.setReceiver(SysConstant.EMAIL_RECEIVER);
        mailBean.setSubject("[Daily Report]-CASIA AliYun Elasticsearch Monitor");

        mailBean.setContent(elastic.getReportText());

        try {
            mailService.sendSimpleMail(mailBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(后台运行TASK监控)
     */
    private void monitorBackstageTask() throws Exception {

        // 一、从_tasks API获取任务列表
        // 检查 INDEX:.monitor_task_alarm TYPE:task 索引是否存在
        if (!esIndexCreat.isIndexName()) {
            esIndexCreat.singleMapping("/" + SysConstant._MONITOR_TASK_INDEX_NAME, "mapping" + File.separator + ".monitor_task_alarm.json");
        }
        // 封装数据
        List<JSONObject> dataList = backstageTaskData();
        esIndexCreat.index(dataList, "task_id");

        // 二、从.task索引获取任务列表
        List<JSONObject> dataTaskList = getTaskList();
        esIndexCreat.index(dataTaskList, "task_id");
    }

    private List<JSONObject> getTaskList() {
        List<JSONObject> dataList = new ArrayList<>();
        String time = FileUtil.readAllLineCursor("task_cursor_start_time.cursor");
        esIndexSearch.addRangeTerms("start_time_in_millis", time == null || "".equals(time) ? "0" : time,
                FieldOccurs.MUST, RangeOccurs.GT);
//        esIndexSearch.setRow(SysConstant.BATCH);
        esIndexSearch.execute(new String[]{"_id", "task.node"});
        JSONObject result = esIndexSearch.queryJsonResult;
        String cursorStartTime = "";
        if (result != null && !result.isEmpty()) {
//            JSONArray hits = result.getJSONObject("hits").getJSONArray("hits");
//            task.remove("status");
//            JSONObject taskInfo = new JSONObject();
//            taskInfo.put("task_id", result.getString("_id"));
//            taskInfo.put("start_time", DateUtil.millToTimeStr(taskInfo.getLongValue("start_time_in_millis")));
//            cursorStartTime = taskInfo.getString("start_time_in_millis");
//            dataList.add(taskInfo);
        }
        FileUtil.writeIDSToFile(cursorStartTime, "task_cursor_start_time.cursor");
        return dataList;
    }

    private List<JSONObject> backstageTaskData() {
        List<JSONObject> dataList = new ArrayList<>();
        String response = esIndexCreat.httpRequest.httpGet(EsUrl._tasks.url());
        JSONObject result = JSONObject.parseObject(response);

        if (result != null && !result.isEmpty()) {
            JSONObject nodes = result.getJSONObject("nodes");
            for (Map.Entry entry : nodes.entrySet()) {
                JSONObject value = (JSONObject) entry.getValue();
                JSONObject tasks = value.getJSONObject("tasks");

                for (Map.Entry taskMap : tasks.entrySet()) {
                    String taskId = String.valueOf(taskMap.getKey());
                    JSONObject taskInfo = (JSONObject) taskMap.getValue();
                    taskInfo.put("task_id", taskId);
                    long start_time_in_millis = taskInfo.getLongValue("start_time_in_millis");
                    taskInfo.put("start_time", DateUtil.millToTimeStr(start_time_in_millis));
                    dataList.add(taskInfo);
                }
            }
        }
        return dataList;
    }

    /**
     * @param
     * @return
     * @Description: TODO(监控删除TASK索引的数据)
     */
    private void deleteMonitorBackstageTask() {

        esIndexDelete.setDebug(SysConstant.DEBUG);

        String currentThreadTime = getCurrentThreadTime();

        esIndexDelete.addRangeTerms("start_time", currentThreadTime, FieldOccurs.MUST, RangeOccurs.LTE);
        esIndexDelete.setRefresh("wait_for");
        esIndexDelete.setScrollSize(1000);
        esIndexDelete.conflictsProceed("proceed");
        esIndexDelete.setWaitForCompletion(false);
        esIndexDelete.execute();

        // FORCE MERGE
        esIndexDelete.forceMerge();

        esIndexDelete.reset();
    }

    private String getCurrentThreadTime() {
        long mill = System.currentTimeMillis() - dhmToMill(SysConstant._MONITOR_TASK_INDEX_RETAIN_TIME);
        return DateUtil.millToTimeStr(mill);
    }

    private long dhmToMill(String dhmStr) {
        if (dhmStr != null && !"".equals(dhmStr)) {
            int number = Integer.valueOf(StringUtil.cutNumber(dhmStr));
            if (dhmStr.contains("d")) {
                return number * 86400000L;
            } else if (dhmStr.contains("h")) {
                return number * 3600000L;
            } else if (dhmStr.contains("m")) {
                return number * 60000L;
            } else if (dhmStr.contains("s")) {
                return number * 1000L;
            }
        }
        return 0L;
    }

}

