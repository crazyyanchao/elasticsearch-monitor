package casia.isi.elasticsearch.monitor.util;/**
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

import casia.isi.elasticsearch.monitor.model.DeleteTaskKpi;
import casia.isi.elasticsearch.monitor.model.GlobalIndexKpi;
import casia.isi.elasticsearch.monitor.model.SingleIndexKpi;
import casia.isi.elasticsearch.monitor.model.WholeIndexKpi;
import casia.isi.elasticsearch.util.FileUtil;

import java.util.List;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.util
 * @Description: TODO(HTML生成工具)
 * @date 2020/1/11 15:52
 */
public class HTMLUtil {

    private final static String STATISTICS_HTML = FileUtil.readAllLine("config/loding_statistics.html", "UTF-8");

    public static String getStatisticsHtml() {
        return STATISTICS_HTML;
    }

    public static String globalIndexKpi(GlobalIndexKpi globalKpi) {
        return "<tr class=\"bj_blue\">\n" +
                "        <td>" + globalKpi.getClusterName() + "</td>\n" +
                "        <td>" + globalKpi.getTotal() + "</td>\n" +
                "        <td>" + globalKpi.getStore() + "</td>\n" +
                "        <td>" + globalKpi.getShardsCount() + "</td>\n" +
                "        <td>" + globalKpi.getNodeCount() + "</td>\n" +
                "        <td>" + globalKpi.getIndicesCount() + "</td>\n" +
                "        <td>" + globalKpi.getDeleteTaskCount() + "</td>\n" +
                "    </tr>";
    }

    public static String wholeIndexKpi(List<WholeIndexKpi> wholeIndexKpiList) {
        StringBuilder builder = new StringBuilder();
        for (WholeIndexKpi wholeIndexKpi : wholeIndexKpiList) {
            builder.append("<tr class=\"bj_orange\">\n" +
                    "        <td>" + wholeIndexKpi.getAlias() + "</td>\n" +
                    "        <td>" + wholeIndexKpi.getIndexType() + "</td>\n" +
                    "        <td>" + wholeIndexKpi.getTotal() + "</td>\n" +
                    "        <td>" + wholeIndexKpi.getProportion() + "</td>\n" +
                    "        <td>" + wholeIndexKpi.getYesterdayDataCount() + "</td>\n" +
                    "        <td>" + wholeIndexKpi.getTodayDataCount() + "</td>\n" +
                    "        <td>" + wholeIndexKpi.getEarliestTime() + "</td>\n" +
                    "        <td>" + wholeIndexKpi.getLatestTime() + "</td>\n" +
                    "    </tr>");
        }
        return builder.toString();
    }

    public static String singleIndexKpi(List<SingleIndexKpi> singleIndexKpiList) {
        StringBuilder builder = new StringBuilder();
        for (SingleIndexKpi singleIndexKpi : singleIndexKpiList) {
            builder.append(" <tr class=\"bj_red\">\n" +
                    "        <td>" + singleIndexKpi.getIndexType() + "</td>\n" +
                    "        <td>" + singleIndexKpi.getIndexName() + "</td>\n" +
                    "        <td>" + singleIndexKpi.getTotal() + "</td>\n" +
                    "        <td>" + singleIndexKpi.getYesterdayDataCount() + "</td>\n" +
                    "        <td>" + singleIndexKpi.getTodayDataCount() + "</td>\n" +
                    "        <td>" + singleIndexKpi.getEarliestTime() + "</td>\n" +
                    "        <td>" + singleIndexKpi.getLatestTime() + "</td>\n" +
                    "    </tr>");
        }
        return builder.toString();
    }

    public static String deleteTaskKpi(List<DeleteTaskKpi> deleteTaskKpiList) {
        StringBuilder builder = new StringBuilder();
        for (DeleteTaskKpi deleteTaskKpi : deleteTaskKpiList) {
            builder.append("<tr class=\"bj_blue\">\n" +
                    "        <td>" + deleteTaskKpi.getDeleteTaskName() + "</td>\n" +
                    "        <td>" + deleteTaskKpi.getCount() + "</td>\n" +
                    "        <td>" + deleteTaskKpi.getTaskID() + "</td>\n" +
                    "        <td>" + deleteTaskKpi.getStart_time() + "</td>\n" +
                    "        <td>" + deleteTaskKpi.getRunning_time_in_nanos() + "</td>\n" +
                    "        <td>completed(" + deleteTaskKpi.isCompleted() + ");cancellable(" + deleteTaskKpi.isCancellable() + ")</td>\n" +
                    "    </tr>");
        }
        return builder.toString();
    }
}

