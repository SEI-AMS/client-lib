package edu.cmu.sei.ams.cloudlet.impl;

import edu.cmu.sei.ams.cloudlet.CloudletException;
import edu.cmu.sei.ams.cloudlet.Service;
import edu.cmu.sei.ams.cloudlet.ServiceVM;
import edu.cmu.sei.ams.cloudlet.impl.cmds.StartServiceCommand;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;


import java.util.ArrayList;
import java.util.List;

import static edu.cmu.sei.ams.cloudlet.impl.CloudletUtilities.*;

/**
 * User: jdroot
 * Date: 3/20/14
 * Time: 3:44 PM
 */
public class ServiceImpl implements Service
{
    private static final XLogger log = XLoggerFactory.getXLogger(ServiceImpl.class);

    private String serviceId;
    private String description;
    private String version;
    private JSONObject json;
    private List<String> tags;

    private final CloudletCommandExecutor mCloudlet;

    private ServiceVM serviceVM;

    /**
     * Creates an instance a service based on a specific cloudlet
     * @param mCloudlet The cloudlet this service lives on
     * @param json The json data describing this service
     */
    ServiceImpl(CloudletCommandExecutor mCloudlet, JSONObject json)
    {
        this.serviceId = getSafeString("service_id", json);
        this.description = getSafeString("description", json);
        this.version = getSafeString("version", json);
        this.tags = getSafeStringArray("tags", json);
        this.mCloudlet = mCloudlet;
        this.json = json;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getServiceId()
    {
        return serviceId;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getDescription()
    {
        return description;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getVersion()
    {
        return this.version;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public List<String> getTags()
    {
        return this.tags;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public ServiceVM startService()
    {
        return startService(true);
    }

    @Override
    public ServiceVM startService(boolean join)
    {
        ServiceVM ret = null;
        StartServiceCommand cmd = new StartServiceCommand(this);
        cmd.setJoin(join);
        try
        {
            String jsonStr = mCloudlet.executeCommand(cmd);
            JSONObject obj = new JSONObject(jsonStr);
            ret = new ServiceVMImpl(mCloudlet, this, obj);
            this.serviceVM = ret;
        }
        catch (CloudletException e)
        {
            log.error("Error starting service", e);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public boolean stopService()
    {
        boolean ret = false;
        if (serviceVM != null)
        {
            ret = serviceVM.stopVm();
            serviceVM = null;
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public ServiceVM getServiceVM()
    {
        return serviceVM;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String toString()
    {
        return json.toString();
    }
}
