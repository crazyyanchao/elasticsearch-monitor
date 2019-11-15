package casia.isi.elasticsearch.monitor.common;/**
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

import java.io.File;
import java.io.FileInputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.common
 * @Description: TODO(CONSTANT LOAD)
 * @date 2019/11/15 14:53
 */
public class SysConstant {

    public static final int HTTP_PORT = Integer.parseInt(loadProperty("server.http.port"));
//    public static final int HTTPS_PORT = Integer.parseInt(loadProperty("server.https.port"));

    public static final String ELASTICSEARCH_ADDRESS = loadProperty("elasticsearch.address");
    public static final String MONITOR_INDEX_TYPE = loadProperty("monitor.index.type");
    public static final String ALARM_STATUS_TIME = loadProperty("alarm.status.time");
    public static final String EMAIL_RECEIVER = loadProperty("email.receiver");

    /**
     * @param
     * @return
     * @Description: TODO(加载配置文件)
     */
    private static Properties getIndexProperties() {
        try {
            //获取服务器ip和端口
            FileInputStream inStream = new FileInputStream(new File("config" + File.separator + "server.properties"));
            Properties properties = new Properties();
            properties.load(inStream);
            return properties;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param mysqlTableIndexName:大小索引的统一索引名配置
     * @return
     * @Description: TODO(通过配置名称获取配置)
     */
    private static String loadProperty(String mysqlTableIndexName) {
        Properties properties = getIndexProperties();
        return Objects.requireNonNull(properties).getProperty(mysqlTableIndexName);
    }
}

