package casia.isi.elasticsearch.monitor.controller;
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

import casia.isi.elasticsearch.monitor.common.HttpAccessor;
import casia.isi.elasticsearch.monitor.entity.UserJson;
import casia.isi.elasticsearch.monitor.service.ElasticStatistics;
import casia.isi.elasticsearch.monitor.common.Message;
import casia.isi.elasticsearch.monitor.common.SysConstant;
import casia.isi.elasticsearch.monitor.entity.MailBean;
import casia.isi.elasticsearch.monitor.service.MailService;
import casia.isi.elasticsearch.operation.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.File;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.controller
 * @Description: TODO(REMOTE INTERFACE)
 * @date 2019/11/15 14:50
 */
@org.springframework.stereotype.Controller
@CrossOrigin(origins = "*", maxAge = 3600) // 为了支持跨源请求添加注解
public class Controller {

    @Autowired
    private MailService mailService;

    @Autowired
    private ElasticStatistics elasticStatistics;

    /**
     * @param
     * @return
     * @Description: TODO(Monitor Instrument Index) http://localhost:7100/toolkit/index
     */
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        modelMap.put("msg", "SpringBoot Elasticsearch Monitor");
        return "elasticsearch_monitor/index";
    }

    /**
     * @param
     * @return
     * @Description: TODO(Monitor Instrument Index) http://localhost:7100/toolkit/dash_board
     */
    @RequestMapping(value = "/dash_board", method = RequestMethod.GET)
    public String dash_board(ModelMap modelMap) {
        modelMap.put("msg", "SpringBoot Elasticsearch Monitor");
        return "elasticsearch_monitor/dash_board";
    }

    /**
     * @param
     * @return
     * @Description: TODO(Monitor Instrument Index) http://localhost:7100/toolkit/login
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(ModelMap modelMap) {
        modelMap.put("msg", "SpringBoot Elasticsearch Monitor Login");
        return "elasticsearch_monitor/login";
    }

    /**
     * @param
     * @return
     * @Description: TODO(Monitor Instrument Index) http://localhost:7100/toolkit/
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root(ModelMap modelMap) {
        modelMap.put("msg", "SpringBoot Elasticsearch Monitor Root");
        return "home";
    }

    /**
     * @param
     * @return
     * @Description: TODO(README) http://localhost:7100/toolkit/readme
     */
    @RequestMapping(value = "/readme", method = RequestMethod.GET)
    public String readme(ModelMap modelMap) {
        modelMap.put("msg", "SpringBoot Elasticsearch Monitor Readme");
        return "readme/readme";
    }

    /**
     * @param
     * @return
     * @Description: TODO(README) http://localhost:7100/toolkit/json
     */
    @RequestMapping(value = "/json", method = RequestMethod.GET)
    public String json(ModelMap modelMap) {
        modelMap.put("msg", "SpringBoot Elasticsearch Monitor Json");
        return "elasticsearch_monitor/json";
    }

    /**
     * @param
     * @return
     * @Description: TODO(Monitor Instrument Bigdesk Index) http://localhost:7100/toolkit/bigdesk-index
     */
    @RequestMapping(value = "/bigdesk-index", method = RequestMethod.GET)
    public String bigdeskIndex(ModelMap modelMap) {
        modelMap.put("msg", "SpringBoot Elasticsearch Monitor Bigdesk Index");
        return "bigdesk" + File.separator + "index";
    }

    /**
     * @param
     * @return
     * @Description: TODO(集群地址健康检查)
     */
    @RequestMapping(value = "/healthCheck", method = RequestMethod.POST)
    @ResponseBody
    public String healthCheck(@RequestBody UserJson userJson) {
        String address = userJson.getAddress();

        // 先重置集群地址
        new HttpAccessor().removeLastHttpsAddNewAddress(userJson.getAddress());

        HttpRequest httpRequest = new HttpRequest();
        boolean status = httpRequest.checkHttpGet(address.trim());
        return new Message().setStatus(status).toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(重置集群地址 - 将集群地址赋值给动态常量)
     */
    @RequestMapping(value = "/removeLastHttpsAddNewAddress", method = RequestMethod.POST)
    @ResponseBody
    public String removeLastHttpsAddNewAddress(@RequestBody UserJson userJson) {
        new HttpAccessor().removeLastHttpsAddNewAddress(userJson.getAddress());
        return JSONObject.parseObject(JSON.toJSONString(userJson)).toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(SEND EMAIL)
     */
    @RequestMapping(value = "/send-email", method = RequestMethod.POST)
    @ResponseBody
    public String sendEmail(@RequestBody UserJson userJson) {
        MailBean mailBean = new MailBean();
        mailBean.setReceiver(SysConstant.EMAIL_RECEIVER);
        mailBean.setContent(elasticStatistics.getReportText(userJson.getAddress()));
        mailBean.setSubject("[Daily Report]-CASIA AliYun Elasticsearch Monitor");
        try {
            mailService.sendSimpleMail(mailBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message().setStatus(true).toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(ALARM STATISTICS)
     */
    @RequestMapping(value = "/alarm-statistics", method = RequestMethod.POST)
    @ResponseBody
    public String alarmStatistics(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.getReportText(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(GET ALL INDICES)
     */
    @RequestMapping(value = "/get-all-indices", method = RequestMethod.POST)
    @ResponseBody
    public String getAllIndices(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.getAllIndices(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(24H STATISTICS)
     */
    @RequestMapping(value = "/24h-statistics", method = RequestMethod.POST)
    @ResponseBody
    public String get24HStatistics(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.recentOneDaysIndexStatus(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(7D STATISTICS)
     */
    @RequestMapping(value = "/7d-statistics", method = RequestMethod.POST)
    @ResponseBody
    public String get7DStatistics(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.recentSeveralDaysIndexStatus(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(DELETE STATISTICS)
     */
    @RequestMapping(value = "/delete-statistics", method = RequestMethod.POST)
    @ResponseBody
    public String getDeleteStatistics(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.detectDeleter(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(DELETE STATISTICS 01)
     */
    @RequestMapping(value = "/delete-statistics_view_all_delete_01", method = RequestMethod.POST)
    @ResponseBody
    public String getDeleteStatistics01(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.clusterDeleteTask(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(DELETE STATISTICS 02)
     */
    @RequestMapping(value = "/delete-statistics_view_all_delete_02", method = RequestMethod.POST)
    @ResponseBody
    public String getDeleteStatistics02(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.detectDeleter(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(CLUSTER DELETE TASKS)
     */
    @RequestMapping(value = "/cluster-delete-task", method = RequestMethod.POST)
    @ResponseBody
    public String getClusterDeleteTask(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.clusterDeleteTask(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(MONITOR DELETE TASK BY ID)
     */
    @RequestMapping(value = "/monitor-delete-task-id", method = RequestMethod.POST)
    @ResponseBody
    public String getMonitorDeleteTaskId(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.getTaskDetailById(userJson.getAddress(), userJson.getTaskId()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(CLUSTER STATISTICS BASIS INFORMATION)
     */
    @RequestMapping(value = "/cluster-basis-info", method = RequestMethod.POST)
    @ResponseBody
    public String getClusterBasisInfo(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.getClusterBasisInfo(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(CLUSTER STATISTICS INFORMATION)
     */
    @RequestMapping(value = "/cluster-average-basis-info", method = RequestMethod.POST)
    @ResponseBody
    public String getClusterAverageBasisInfo(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.getClusterAverageBasisInfo(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(CLUSTER STATISTICS INFORMATION)
     */
    @RequestMapping(value = "/cluster-pie-statistics-info", method = RequestMethod.POST)
    @ResponseBody
    public String getClusterPieStatisticsInfo(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.getClusterPieStatisticsInfo(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(INDICES TIME INFORMATION)
     */
    @RequestMapping(value = "/indices-time-info", method = RequestMethod.POST)
    @ResponseBody
    public String getIndicesTimeInfo(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.getIndicesTimeInfo(userJson.getAddress()))
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(ABNORMAL DATA DESCRIPTION)
     */
    @RequestMapping(value = "/abnormal-data-description", method = RequestMethod.POST)
    @ResponseBody
    public String getAbnormalDataDescription(@RequestBody UserJson userJson) {
        return new Message().setStatus(true)
                .putResult(elasticStatistics.getAbnormalDataDescription(userJson.getAddress(),userJson.getIndexType()))
                .toJSONString();
    }
}


