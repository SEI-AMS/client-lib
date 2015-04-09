package edu.cmu.sei.ams.cloudlet.impl;

import edu.cmu.sei.ams.cloudlet.CpuInfo;
import org.json.JSONObject;
import static edu.cmu.sei.ams.cloudlet.impl.CloudletUtilities.*;

/**
 * User: jdroot
 * Date: 4/8/14
 * Time: 11:47 AM
 */
public class CpuInfoImpl implements CpuInfo
{

    private final int totalCores;
    private final double usage;

    private final double speed;
    private final int cache;

    CpuInfoImpl(JSONObject obj)
    {
        totalCores = getSafeInt("max_cores", obj);
        usage = getSafeDouble("usage", obj);
        speed = getSafeDouble("speed", obj);
        cache = getSafeInt("cache", obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalCores()
    {
        return totalCores;
    }

    @Override
    public double getUsage()
    {
        return usage;
    }

    @Override
    public double getSpeed()
    {
        return speed;
    }

    @Override
    public int getCache()
    {
        return cache;
    }


    @Override
    public String toString()
    {
        return "{max_cores:" + getTotalCores() + ",usage:" + getUsage()  +
                ",speed:" + getSpeed()  + ",cache:" + getCache() + "}";
    }
}
