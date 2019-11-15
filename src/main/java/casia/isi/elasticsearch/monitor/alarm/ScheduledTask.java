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

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.alarm
 * @Description: TODO(Describe the role of this JAVA class)
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


    @Scheduled(cron = "0/10 * * * * ?") //每10秒执行一次
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
        try {
            System.out.println("Timer...");
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
