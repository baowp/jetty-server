package org.eclipse.jetty.diy.launch;

import org.apache.log4j.Logger;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.diy.utility.LaunchUtil;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.*;

import static org.apache.log4j.Logger.getLogger;

/**
 * Created with IntelliJ IDEA.
 * User: baowp
 * Date: 11/7/13
 * Time: 9:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class JettyWebContainer implements Container {

    private static final Logger logger = getLogger(JettyWebContainer.class);

    public static final String JETTY_PORT = "jetty.port";
    public static final int DEFAULT_JETTY_PORT = 8080;

    public static final String RESOURCE_BASE = "resource.base";

    public void start() {
        String serverPort = LaunchUtil.getProperty(JETTY_PORT);
        int port;
        if (serverPort == null || serverPort.length() == 0) {
            port = DEFAULT_JETTY_PORT;
        } else {
            port = Integer.parseInt(serverPort);
        }
        String resourceBase = LaunchUtil.getProperty(RESOURCE_BASE);

        Server server = new Server(port);

        WebAppContext context = new WebAppContext();
        context.setResourceBase(resourceBase);
        context.setDescriptor(resourceBase + "/WEB-INF/web.xml");
        context.setConfigurations(new Configuration[]{
                new AnnotationConfiguration(), new WebXmlConfiguration(),
                new WebInfConfiguration(), new TagLibConfiguration(),
                new PlusConfiguration(), new MetaInfConfiguration(),
                new FragmentConfiguration(), new EnvConfiguration()});

        context.setContextPath("/");
        context.setParentLoaderPriority(true);
        server.setHandler(context);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to start jetty server on " + "NetUtils.getLocalHost()" + ":" + port + ", cause: " + e.getMessage(), e);
        }
    }

    public void stop() {
    }
}
