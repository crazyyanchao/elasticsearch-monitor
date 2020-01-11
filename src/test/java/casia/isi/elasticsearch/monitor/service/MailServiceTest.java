package casia.isi.elasticsearch.monitor.service;

import casia.isi.elasticsearch.monitor.common.SysConstant;
import casia.isi.elasticsearch.monitor.entity.MailBean;
import casia.isi.elasticsearch.util.FileUtil;
import org.apache.log4j.PropertyConfigurator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
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

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.service
 * @Description: TODO(TEST)
 * @date 2020/1/11 11:36
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class MailServiceTest {

    @Autowired
    private MailService mailService;

    @Autowired
    private ElasticStatistics elastic;

    @BeforeEach
    void setUp() {
        PropertyConfigurator.configureAndWatch("config/log4j.properties");
    }

    @Test
    void sendSimpleMail() {
        MailBean mailBean = new MailBean();
        mailBean.setReceiver(SysConstant.EMAIL_RECEIVER);
        mailBean.setSubject("[Daily Report]-CASIA AliYun Elasticsearch Monitor");
        mailBean.setContent(elastic.getReportText(SysConstant.ELASTICSEARCH_ADDRESS)); // 后台定时任务监控预警配置的集群
        try {
            mailService.sendSimpleMail(mailBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    void sendMailHtml() {
        MailBean mailBean = new MailBean();
        mailBean.setReceiver(SysConstant.EMAIL_RECEIVER);
        mailBean.setSubject("[Daily Report]-CASIA AliYun Elasticsearch Monitor");
        String html= FileUtil.readAllLine("config/statistics.html","UTF-8");
        mailBean.setContent(html); // 后台定时任务监控预警配置的集群
        try {
            mailService.sendMailHtml(mailBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
