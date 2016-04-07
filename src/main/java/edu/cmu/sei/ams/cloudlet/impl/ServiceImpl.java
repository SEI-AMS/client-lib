/*
KVM-based Discoverable Cloudlet (KD-Cloudlet) 
Copyright (c) 2015 Carnegie Mellon University.
All Rights Reserved.

THIS SOFTWARE IS PROVIDED "AS IS," WITH NO WARRANTIES WHATSOEVER. CARNEGIE MELLON UNIVERSITY EXPRESSLY DISCLAIMS TO THE FULLEST EXTENT PERMITTEDBY LAW ALL EXPRESS, IMPLIED, AND STATUTORY WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT OF PROPRIETARY RIGHTS.

Released under a modified BSD license, please see license.txt for full terms.
DM-0002138

KD-Cloudlet includes and/or makes use of the following Third-Party Software subject to their own licenses:
MiniMongo
Copyright (c) 2010-2014, Steve Lacy 
All rights reserved. Released under BSD license.
https://github.com/MiniMongo/minimongo/blob/master/LICENSE

Bootstrap
Copyright (c) 2011-2015 Twitter, Inc.
Released under the MIT License
https://github.com/twbs/bootstrap/blob/master/LICENSE

jQuery JavaScript Library v1.11.0
http://jquery.com/
Includes Sizzle.js
http://sizzlejs.com/
Copyright 2005, 2014 jQuery Foundation, Inc. and other contributors
Released under the MIT license
http://jquery.org/license
*/
package edu.cmu.sei.ams.cloudlet.impl;

import edu.cmu.sei.ams.cloudlet.Cloudlet;
import edu.cmu.sei.ams.cloudlet.CloudletException;
import edu.cmu.sei.ams.cloudlet.DeviceMessageManager;
import edu.cmu.sei.ams.cloudlet.IDeviceMessageHandler;
import edu.cmu.sei.ams.cloudlet.Service;
import edu.cmu.sei.ams.cloudlet.ServiceVM;
import edu.cmu.sei.ams.cloudlet.impl.cmds.StartServiceCommand;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.List;
import java.util.Map;

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

    private final CloudletCommandExecutor commandExecutor;
    private final Cloudlet cloudlet;

    private ServiceVM serviceVM;
    private Thread messageCheckerThread;

    /**
     * Creates an instance a service based on a specific cloudlet
     * @param commandExecutor The cloudlet this service lives on
     * @param json The json data describing this service
     */
    ServiceImpl(CloudletCommandExecutor commandExecutor, Cloudlet cloudlet, JSONObject json)
    {
        this.serviceId = getSafeString("service_id", json);
        this.description = getSafeString("description", json);
        this.version = getSafeString("version", json);
        this.tags = getSafeStringArray("tags", json);
        this.commandExecutor = commandExecutor;
        this.cloudlet = cloudlet;
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
    public ServiceVM startService() throws CloudletException
    {
        return startService(true);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public ServiceVM startService(boolean join) throws CloudletException
    {
        return startService(join);
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public ServiceVM startService(final Map<String, IDeviceMessageHandler> handlers) throws CloudletException
    {
        return startService(true, handlers);
    }

    @Override
    public ServiceVM startService(boolean join, final Map<String, IDeviceMessageHandler> handlers) throws CloudletException
    {
        ServiceVM ret = null;
        StartServiceCommand cmd = new StartServiceCommand(this);
        cmd.setJoin(join);
        String jsonStr = "";
        try
        {
            jsonStr = commandExecutor.executeCommand(cmd, cloudlet.getAddress().getHostAddress(), cloudlet.getPort());
            JSONObject obj = new JSONObject(jsonStr);
            ret = new ServiceVMImpl(this.commandExecutor, this.cloudlet, this, obj);
            this.serviceVM = ret;

            // Create a thread that will check for messages associated to this service and device.
            messageCheckerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if(handlers.size() > 0) {
                        DeviceMessageManager manager = new DeviceMessageManager();
                        for (String message: handlers.keySet()) {
                            manager.registerHandler(message, handlers.get(message));
                        }

                        // TODO: what happens after migration, if we are passing the current cloudlet here?
                        manager.execute(getCloudlet(), serviceId);
                    }
                }
            });
            messageCheckerThread.start();
        }
        catch (CloudletException e)
        {
            // We want the user to see this problem.
            throw e;
        }
        catch (JSONException e)
        {
            log.error("Error parsing reply: " + jsonStr + ";", e);
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public boolean stopService() throws CloudletException
    {
        boolean ret = false;
        if (serviceVM != null)
        {
            ret = serviceVM.stopVm();
            serviceVM = null;

            if(messageCheckerThread != null)
            {
                messageCheckerThread.interrupt();
                messageCheckerThread = null;
            }
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

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public Cloudlet getCloudlet() {
        return cloudlet;
    }
}
