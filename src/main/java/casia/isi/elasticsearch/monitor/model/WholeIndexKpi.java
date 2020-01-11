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
 * @Description: TODO(整体索引 （ 按照索引类型进行统计 ） - 生成索引的数据统计报告必要指标字段)
 * @date 2020/1/11 10:36
 */
public class WholeIndexKpi {
    // 别名
    private String alias;
    // 索引类型名
    private String indexType;
    // 数据总量
    private int total;
    // 占比
    private String proportion;

    // 昨日数据量
    private int yesterdayDataCount;
    // 今日数据量
    private int todayDataCount;
    // 最早一条数据时间
    private String earliestTime;
    // 最近一条数据时间
    private String latestTime;

    public WholeIndexKpi() {
    }

    public WholeIndexKpi(String indexType, String alias, int total, String proportion, int yesterdayDataCount, int todayDataCount, String earliestTime, String latestTime) {
        this.indexType = indexType;
        this.alias = alias;
        this.total = total;
        this.proportion = proportion;
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getProportion() {
        return proportion;
    }

    public void setProportion(String proportion) {
        this.proportion = proportion;
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

