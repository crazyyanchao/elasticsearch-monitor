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
 * @Description: TODO(单个索引 - 生成索引的数据统计报告必要指标字段)
 * @date 2020/1/11 10:07
 */
public class SingleIndexKpi {
    // 索引类型
    private String indexType;
    // 索引名
    private String indexName;
    // 数据总量
    private int total;
    // 昨日数据量
    private int yesterdayDataCount;
    // 今日数据量
    private int todayDataCount;
    // 最早一条数据时间
    private String earliestTime;
    // 最近一条数据时间
    private String latestTime;

    public SingleIndexKpi() {
    }

    public SingleIndexKpi(String indexType, String indexName, int total, int yesterdayDataCount, int todayDataCount, String earliestTime, String latestTime) {
        this.indexType = indexType;
        this.indexName = indexName;
        this.total = total;
        this.yesterdayDataCount = yesterdayDataCount;
        this.todayDataCount = todayDataCount;
        this.earliestTime = earliestTime;
        this.latestTime = latestTime;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getYesterdayDataCount() {
        return yesterdayDataCount;
    }

    public void setYesterdayDataCount(int yesterdayDataCount) {
        this.yesterdayDataCount = yesterdayDataCount;
    }

    public int getTodayDataCount() {
        return todayDataCount;
    }

    public void setTodayDataCount(int todayDataCount) {
        this.todayDataCount = todayDataCount;
    }

    public String getEarliestTime() {
        return earliestTime;
    }

    public void setEarliestTime(String earliestTime) {
        this.earliestTime = earliestTime;
    }

    public String getLatestTime() {
        return latestTime;
    }

    public void setLatestTime(String latestTime) {
        this.latestTime = latestTime;
    }
}

