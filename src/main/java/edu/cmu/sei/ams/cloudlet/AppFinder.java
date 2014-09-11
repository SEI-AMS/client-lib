package edu.cmu.sei.ams.cloudlet;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jdroot
 * Date: 9/9/14
 * Time: 3:40 PM
 */
public class AppFinder
{
    private static final XLogger log = XLoggerFactory.getXLogger(AppFinder.class);
    /**
     * Private constructor to make sure this class is entirely static
     */
    private AppFinder()
    {

    }

    /**
     * Find all apps from nearby cloudlets
     * @param filter Filter's the apps that will be returned
     * @return
     */
    public static List<App> findAppsByTag(AppFilter filter)
    {
        log.entry(filter);
        List<App> ret = new ArrayList<App>();
        for (Cloudlet cloudlet: CloudletFinder.findCloudlets())
        {
            try
            {
                for (App app : cloudlet.getApps(filter))
                    if (!contains(ret, app))
                        ret.add(app);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        log.exit(ret);
        return ret;
    }

    private static boolean contains(List<App> apps, App app)
    {
        for (App a : apps)
            if (a.getName().equals(app.getName()))
                return true;
        return false;
    }
}
