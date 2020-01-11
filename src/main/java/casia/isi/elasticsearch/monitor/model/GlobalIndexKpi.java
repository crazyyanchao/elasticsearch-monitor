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
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.model
 * @Description: TODO(索引集群基本信息)
 * @date 2020/1/11 10:42
 */
public class GlobalIndexKpi {
    // 集群名称
    private String clusterName;
    // 数据总量
    private int total;
    // 存储占用
    private String store;
    // 分片数量
    private int shardsCount;
    // 节点数量
    private int nodeCount;
    // 索引数量
    private int indicesCount;
    // 删除任务总数
    private int deleteTaskCount;

    public GlobalIndexKpi() {
    }

    public GlobalIndexKpi(String clusterName, int total, String store, int shardsCount, int nodeCount, int indicesCount, int deleteTaskCount) {
        this.clusterName = clusterName;
        this.total = total;
        this.store = store;
        this.shardsCount = shardsCount;
        this.nodeCount = nodeCount;
        this.indicesCount = indicesCount;
        this.deleteTaskCount = deleteTaskCount;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public int getDeleteTaskCount() {
        return deleteTaskCount;
    }

    public void setDeleteTaskCount(int deleteTaskCount) {
        this.deleteTaskCount = deleteTaskCount;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public int getShardsCount() {
        return shardsCount;
    }

    public void setShardsCount(int shardsCount) {
        this.shardsCount = shardsCount;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount) {
        this.nodeCount = nodeCount;
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public void setIndicesCount(int indicesCount) {
        this.indicesCount = indicesCount;
    }
}
