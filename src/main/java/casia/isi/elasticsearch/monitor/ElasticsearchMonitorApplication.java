package casia.isi.elasticsearch.monitor;

import casia.isi.elasticsearch.monitor.common.SysConstant;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isi.elasticsearch.monitor.ElasticsearchMonitorApplication
 * @Description: TODO(Spring Boot Starter)
 * @date 2019/11/15 14:47
 */
@SpringBootApplication
@EnableScheduling
public class ElasticsearchMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchMonitorApplication.class, args);
    }

    // 支持https访问请求
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(createHTTPConnector());
        return tomcat;
    }

    private Connector createHTTPConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        //同时启用http/https两个端口
        connector.setScheme("http");
        connector.setSecure(false);
        connector.setPort(SysConstant.HTTP_PORT);
//        connector.setRedirectPort(SysConstant.HTTPS_PORT);
        return connector;
    }

}

