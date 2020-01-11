package casia.isi.elasticsearch.monitor.model;
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
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.model
 * @Description: TODO(24H数据删除任务统计 - 生成索引的数据统计报告必要指标字段)
 * @author YanchaoMa yanchaoma@foxmail.com
 * @date 2020/1/11 10:51
 *
 *
 */
public class DeleteTaskKpi {
    // 删除任务名称
    private String deleteTaskName;
    // 最近24H运行的次数
    private int count;
    /**
     * 最近状态
     * **/
    // 最近一次运行改任务的状态
    private boolean isCompleted;
    // 最近一次运行改任务的状态
    private boolean isCancellable;
    // 在哪个节点执行任务
    private String locNode;
    // 任务ID
    private String taskID;
    // 最近一次删除的开始时间
    private String start_time;
    // 运行时间
    private long running_time_in_nanos;

    public DeleteTaskKpi() {
    }

    public DeleteTaskKpi(String deleteTaskName, int count, boolean isCompleted, boolean isCancellable, String locNode, String taskID, String start_time, long running_time_in_nanos) {
        this.deleteTaskName = deleteTaskName;
        this.count = count;
        this.isCompleted = isCompleted;
        this.isCancellable = isCancellable;
        this.locNode = locNode;
        this.taskID = taskID;
        this.start_time = start_time;
        this.running_time_in_nanos = running_time_in_nanos;
    }

    public String getDeleteTaskName() {
        return deleteTaskName;
    }

    public void setDeleteTaskName(String deleteTaskName) {
        this.deleteTaskName = deleteTaskName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isCancellable() {
        return isCancellable;
    }

    public void setCancellable(boolean cancellable) {
        isCancellable = cancellable;
    }

    public String getLocNode() {
        return locNode;
    }

    public void setLocNode(String locNode) {
        this.locNode = locNode;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public long getRunning_time_in_nanos() {
        return running_time_in_nanos;
    }

    public void setRunning_time_in_nanos(long running_time_in_nanos) {
        this.running_time_in_nanos = running_time_in_nanos;
    }
}

