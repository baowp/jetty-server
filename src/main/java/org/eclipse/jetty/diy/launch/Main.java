package org.eclipse.jetty.diy.launch;

import org.apache.log4j.Logger;
import org.eclipse.jetty.diy.utility.LaunchUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.apache.log4j.Logger.getLogger;

/**
 * Created with IntelliJ IDEA.
 * User: baowp
 * Date: 11/7/13
 * Time: 10:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    public static final String CONTAINER_KEY = "container";

    public static final String SHUTDOWN_HOOK_KEY = "dubbo.shutdown.hook";

    private static final Logger logger = getLogger(Main.class);

    //private static final ExtensionLoader<Container> loader = ExtensionLoader.getExtensionLoader(Container.class);

    private static volatile boolean running = true;

    public static void main(String[] args) {
        try {
            if (args == null || args.length == 0) {
                String config = LaunchUtil.getProperty(CONTAINER_KEY);
                args = config.split("[,\\s]+");
            }

            final List<Container> containers = new ArrayList<Container>();
            List<String> argList = Arrays.asList(args);
            if (argList.contains("spring"))
                containers.add(new SpringContainer());
            if (argList.contains("jetty"))
                containers.add(new JettyContainer());
            else if(argList.contains("jettyWeb"))
                containers.add(new JettyWebContainer());
           /* for (int i = 0; i < args.length; i ++) {
                containers.add(loader.getExtension(args[i]));
            }
            System.out.println("Use container type(" + Arrays.toString(args) + ") to run dubbo serivce.");

            if ("true".equals(System.getProperty(SHUTDOWN_HOOK_KEY))) {
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        for (Container container : containers) {
                            try {
                                container.stop();
                                System.out.println("Dubbo " + container.getClass().getSimpleName() + " stopped!");
                            } catch (Throwable t) {
                                logger.error(t.getMessage(), t);
                            }
                            synchronized (Main.class) {
                                running = false;
                                Main.class.notify();
                            }
                        }
                    }
                });
            }
*/
            for (Container container : containers) {
                container.start();
                logger.info(container.getClass().getSimpleName() + " started!");
            }
            System.out.println(new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date()) + " service server started!");
        } catch (RuntimeException e) {
            e.printStackTrace();
            //logger.error(e.getMessage(), e);
            System.exit(1);
        }
        synchronized (Main.class) {
            while (running) {
                try {
                    Main.class.wait();
                } catch (Throwable e) {
                }
            }
        }
    }
}
