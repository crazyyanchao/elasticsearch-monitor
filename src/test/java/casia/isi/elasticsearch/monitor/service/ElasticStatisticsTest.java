package casia.isi.elasticsearch.monitor.service;

import casia.isi.elasticsearch.monitor.common.SysConstant;
import casia.isi.elasticsearch.monitor.entity.MailBean;
import org.apache.log4j.PropertyConfigurator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
 * @date 2020/1/11 11:23
 */
class ElasticStatisticsTest {

    private ElasticStatistics elastic;
    private MailService mailService;

    @BeforeEach
    void setUp() {
        PropertyConfigurator.configureAndWatch("config/log4j.properties");
        elastic = new ElasticStatistics();
        mailService = new MailService();
    }

    @Test
    void getHTMLInStatistics() {
        MailBean mailBean = new MailBean();
        mailBean.setReceiver(SysConstant.EMAIL_RECEIVER);
        mailBean.setSubject("[Elasticsearch Daily Report]-DX AliYun");
        mailBean.setContent(elastic.getHTMLInStatistics(SysConstant.ELASTICSEARCH_ADDRESS)); // 后台定时任务监控预警配置的集群
        try {
            mailService.sendMailHtml(mailBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}