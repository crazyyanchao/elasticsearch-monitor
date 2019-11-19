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

import casia.isi.elasticsearch.monitor.common.SysConstant;
import casia.isi.elasticsearch.monitor.entity.MailBean;
import casia.isi.elasticsearch.monitor.service.ElasticStatistics;
import casia.isi.elasticsearch.monitor.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    @Scheduled(cron = "0 00 09 * * ?") // 每天早上九点触发
    public void scheduledTaskByCorn() {
        scheduledTask();
    }

//    @Scheduled(fixedRate = 10000) //每10秒执行一次
//    public void scheduledTaskByFixedRate() {
//        scheduledTask();
//    }
//
//    @Scheduled(fixedDelay = 10000) //每10秒执行一次
//    public void scheduledTaskByFixedDelay() {
//        scheduledTask();
//    }

    private void scheduledTask() {
        MailBean mailBean = new MailBean();
        mailBean.setReceiver(SysConstant.EMAIL_RECEIVER);
        mailBean.setSubject("[Daily Report]-CASIA AliYun Elasticsearch Monitor");
        mailBean.setContent(elastic.statisticsToAlarm().toJSONString());

        try {
            mailService.sendSimpleMail(mailBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

