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

import casia.isi.elasticsearch.monitor.service.ElasticStatistics;
import casia.isi.elasticsearch.monitor.common.Message;
import casia.isi.elasticsearch.monitor.common.SysConstant;
import casia.isi.elasticsearch.monitor.entity.MailBean;
import casia.isi.elasticsearch.monitor.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
     * @Description: TODO(Monitor Instrument Index) http://localhost:7100/monitor/
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        modelMap.put("msg", "SpringBoot Elasticsearch Monitor");
        return "index";
    }

    /**
     * @param
     * @return
     * @Description: TODO(SEND EMAIL)
     */
    @RequestMapping(value = "/send-email", method = RequestMethod.GET)
    @ResponseBody
    public String sendEmail(ModelMap modelMap) {
        modelMap.put("msg", "Send Email");

        MailBean mailBean = new MailBean();
        mailBean.setReceiver(SysConstant.EMAIL_RECEIVER);
        mailBean.setContent(elasticStatistics.getReportText());
        mailBean.setSubject("[Daily Report]-CASIA AliYun Elasticsearch Monitor");
        try {
            mailService.sendSimpleMail(mailBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Message().send(true).toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(ALARM STATISTICS)
     */
    @RequestMapping(value = "/alarm-statistics", method = RequestMethod.GET)
    @ResponseBody
    public String alarmStatistics(ModelMap modelMap) {
        modelMap.put("msg", "Alarm Statistics");
        return new Message().send(true)
                .putResult(elasticStatistics.getReportText())
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(GET ALL INDICES)
     */
    @RequestMapping(value = "/get-all-indices", method = RequestMethod.GET)
    @ResponseBody
    public String getAllIndices(ModelMap modelMap) {
        modelMap.put("msg", "Get all indices");
        return new Message().send(true)
                .putResult(elasticStatistics.getAllIndices())
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(24H STATISTICS)
     */
    @RequestMapping(value = "/24h-statistics", method = RequestMethod.GET)
    @ResponseBody
    public String get24HStatistics(ModelMap modelMap) {
        modelMap.put("msg", "Get all indices");
        return new Message().send(true)
                .putResult(elasticStatistics.recentOneDaysIndexStatus())
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(7D STATISTICS)
     */
    @RequestMapping(value = "/7d-statistics", method = RequestMethod.GET)
    @ResponseBody
    public String get7DStatistics(ModelMap modelMap) {
        modelMap.put("msg", "Get all indices");
        return new Message().send(true)
                .putResult(elasticStatistics.recentSeveralDaysIndexStatus())
                .toJSONString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(DELETE STATISTICS)
     */
    @RequestMapping(value = "/delete-statistics", method = RequestMethod.GET)
    @ResponseBody
    public String getDeleteStatistics(ModelMap modelMap) {
        modelMap.put("msg", "Get all indices");
        return new Message().send(true)
                .putResult(elasticStatistics.detectDeleter())
                .toJSONString();
    }
}


