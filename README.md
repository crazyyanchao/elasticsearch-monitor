# Elasticsearch监控插件

## 一、可配置
1、集群地址
2、被监控的索引【可全部监控或者定制化监控】
3、发送报警的时间
4、发送报警的类型【发送邮件/生成报告】

## 二、报警内容
1、集群整体信息统计【整体资源情况，每个节点的资源情况】
2、索引信息分类统计【每个索引的信息统计】
3、按天统计索引数据量【按天统计】
4、24小时内数据删除任务的运行情况统计【删除任务运行状态监控】【24小时内无运行删除任务的索引马上报警】

## 三、WEB仪表盘

## 备注
1、暂时只支持HTTP访问
2、每日凌晨5点生成报告并发送邮件
3、.monitor_task_alarm只保留最近一段时间的TASK
  【每5S执行一次TASK RECORD，定期删除过时的TASK】【.monitor.task.index.retain.time选项进行配置】



