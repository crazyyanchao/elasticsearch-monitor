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

import casia.isi.elasticsearch.monitor.alarm.ElasticStatistics;
import casia.isi.elasticsearch.monitor.common.Message;
import casia.isi.elasticsearch.monitor.entity.MailBean;
import casia.isi.elasticsearch.monitor.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    /**
     * @param
     * @return
     * @Description: TODO(Monitor Instrument Index)http://localhost:7100/monitor/
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
    public String sendEmail(ModelMap modelMap) {
        modelMap.put("msg", "Send Email");

        ElasticStatistics elastic=new ElasticStatistics();

        MailBean mailBean = new MailBean();
        mailBean.setReceiver("yanchaoma@foxmail.com");
        mailBean.setContent(elastic.recentThreeDaysIndexStatus().toJSONString());
        try {
            mailService.sendSimpleMail(mailBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Message.send(true).toJSONString();
    }

}
