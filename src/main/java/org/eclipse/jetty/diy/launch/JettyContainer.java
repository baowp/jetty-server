package org.eclipse.jetty.diy.launch;

import org.eclipse.jetty.diy.utility.LaunchUtil;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.DispatcherServlet;


/**
 * Created with IntelliJ IDEA.
 * User: baowp
 * Date: 11/7/13
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class JettyContainer implements Container {

    private static final Logger logger = LoggerFactory.getLogger(JettyContainer.class);

    public static final String JETTY_PORT = "jetty.port";
    public static final int DEFAULT_JETTY_PORT = 8080;

    public static final String SPRING_SERVLET = "spring.servlet";
    public static final String DEFAULT_SPRING_SERVLET = "classpath*:spring-servlet.xml";

    ServerConnector connector;

    public void start() {
        String springConfig = LaunchUtil.getProperty(SpringContainer.SPRING_CONFIG);
        if (springConfig == null || springConfig.length() == 0) {
            springConfig = SpringContainer.DEFAULT_SPRING_CONFIG;
        }
        String springServlet = LaunchUtil.getProperty(SPRING_SERVLET);
        if (springServlet == null || springServlet.isEmpty()) {
            springServlet = DEFAULT_SPRING_SERVLET;
        }
        String serverPort = LaunchUtil.getProperty(JETTY_PORT);
        int port;
        if (serverPort == null || serverPort.length() == 0) {
            port = DEFAULT_JETTY_PORT;
        } else {
            port = Integer.parseInt(serverPort);
        }

        Server server = new Server();

        connector = new ServerConnector(server);
        // connector.setMaxIdleTime(1000);
        // connector.setAcceptors(10);
        connector.setPort(port);
        // connector.setConfidentialPort(8443);

        server.setConnectors(new Connector[]{connector});

        ServletContextHandler handler = new ServletContextHandler(server, "/");

        handler.setInitParameter("contextConfigLocation", springConfig);
        handler.addEventListener(new org.springframework.web.context.ContextLoaderListener());

        ServletHolder servletHolder = new ServletHolder(DispatcherServlet.class);
        servletHolder.setInitParameter("contextConfigLocation", springServlet);
        servletHolder.setInitOrder(2);

        handler.addServlet(servletHolder, "/");
        //server.setHandler(handler);

        try {
            server.start();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start jetty server on " + "NetUtils.getLocalHost()" + ":" + port + ", cause: " + e.getMessage(), e);
        }
    }

    public void stop() {
        try {
            if (connector != null) {
                connector.close();
                connector = null;
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

}
