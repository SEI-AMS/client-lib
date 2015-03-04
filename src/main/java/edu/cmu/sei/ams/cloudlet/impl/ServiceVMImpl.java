package edu.cmu.sei.ams.cloudlet.impl;

import edu.cmu.sei.ams.cloudlet.Service;
import edu.cmu.sei.ams.cloudlet.ServiceVM;
import edu.cmu.sei.ams.cloudlet.impl.cmds.StopVMInstanceCommand;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.net.InetAddress;

import static edu.cmu.sei.ams.cloudlet.impl.CloudletUtilities.*;

/**
 * User: jdroot
 * Date: 3/24/14
 * Time: 4:34 PM
 */
public class ServiceVMImpl implements ServiceVM
{
    private static final XLogger log = XLoggerFactory.getXLogger(ServiceVMImpl.class);
    private String instanceId;
    private InetAddress address;
    private int port;
    private JSONObject json;

    private Service mService;
    private final CloudletCommandExecutor mCloudlet;

    ServiceVMImpl(CloudletCommandExecutor mCloudlet, Service mService, JSONObject obj)
    {
        log.entry(mCloudlet, mService, obj);
        this.mCloudlet = mCloudlet;
        this.instanceId = getSafeString("_id", obj);
        this.address = getSafeInetAddress("ip_address", obj);
        this.port = getSafeInt("port", obj);
        this.mService = mService;
        this.json = obj;
        log.exit();
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public boolean stopVm()
    {
        try
        {
            StopVMInstanceCommand cmd = new StopVMInstanceCommand(this);
            String result = mCloudlet.executeCommand(cmd);
            //Result is ignored, it will be a blank json object on success
            return true;
        }
        catch (Exception e)
        {
            log.error("Error stopping VM!", e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getServiceId()
    {
        return mService.getServiceId();
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String getInstanceId()
    {
        return instanceId;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public InetAddress getAddress()
    {
        return address;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public int getPort()
    {
        return port;
    }

    @Override
    public String toString()
    {
        return json.toString();
    }
}
