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

import edu.cmu.sei.ams.cloudlet.*;
import edu.cmu.sei.ams.cloudlet.impl.cmds.GetAppCommand;
import edu.cmu.sei.ams.cloudlet.impl.cmds.GetAppListCommand;
import edu.cmu.sei.ams.cloudlet.impl.cmds.GetDeviceMessagesCommand;
import edu.cmu.sei.ams.cloudlet.impl.cmds.GetMetadataCommand;
import edu.cmu.sei.ams.cloudlet.impl.cmds.GetServicesCommand;
import edu.cmu.sei.ams.cloudlet.impl.cmds.StartServiceCommand;
import edu.cmu.sei.ams.cloudlet.impl.cmds.StopVMInstanceCommand;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: jdroot
 * Date: 3/19/14
 * Time: 4:05 PM
 * CloudletImpl handles both the cloudlet metadata and issuing commands to the cloudlet
 */
public class CloudletImpl implements Cloudlet
{
    private static final XLogger log = XLoggerFactory.getXLogger(CloudletImpl.class);

    private final String name;
    private final InetAddress addr;
    private final int port;

    private List<Service> servicesCache;
    private final CloudletCommandExecutor commandExecutor;

    public CloudletImpl(String name, InetAddress addr, int port, boolean encryptionEnabled,
                        String deviceId, ICredentialsManager credentialsManager) throws IOException {
        this.name = name;
        this.addr = addr;
        this.port = port;

        // Create a command executor for this cloudlet.
        commandExecutor = new CloudletCommandExecutorImpl();
        commandExecutor.setDeviceId(deviceId);
        if(encryptionEnabled) {
            // Get the stored password for this cloudlet and enable encryption for it.
            String password = credentialsManager.getEncryptionPassword(name);
            if(password.equals(""))
            {
                throw new IOException("Password is empty.");
            }

            commandExecutor.enableEncryption(password);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetAddress getAddress()
    {
        return addr;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPort()
    {
        return port;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEncryptionEnabled() {
        return this.commandExecutor.isEncryptionEnabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Service> getServices() throws CloudletException
    {
        return getServices(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Service getServiceById(String id) throws CloudletException
    {
        log.entry(id);
        if (id == null)
        {
            log.exit(null);
            return null;
        }

        if (servicesCache == null)
            getServices();

        for (Service service : servicesCache)
        {
            if (id.equalsIgnoreCase(service.getServiceId()))
            {
                log.exit(service);
                return service;
            }
        }
        log.exit(null);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceVM startService(String serviceId, boolean join) throws CloudletException {
        ServiceVM serviceVM = null;
        StartServiceCommand cmd = new StartServiceCommand(serviceId, join);
        String jsonStr = "";
        try
        {
            jsonStr = commandExecutor.executeCommand(cmd, this.getAddress().getHostAddress(), this.getPort());
            JSONObject jsonSvmInfo = new JSONObject(jsonStr);
            serviceVM = new ServiceVMImpl(this, serviceId, jsonSvmInfo);
        }
        catch (JSONException e)
        {
            log.error("Error parsing reply: " + jsonStr + ";", e);
            e.printStackTrace();
        }
        return serviceVM;

    }

    @Override
    public boolean stopServiceVM(String serviceVMId) throws CloudletException {
        StopVMInstanceCommand cmd = new StopVMInstanceCommand(serviceVMId);
        String result = commandExecutor.executeCommand(cmd, this.getAddress().getHostAddress(), this.getPort());

        // Result is ignored, it will be a blank json object on success.
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CloudletSystemInfo getSystemInfo() throws CloudletException
    {
        String ret = this.commandExecutor.executeCommand(new GetMetadataCommand(), this.getAddress().getHostAddress(), this.getPort());
        return new CloudletSystemInfoImpl(new JSONObject(ret));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<DeviceMessage> getMessages(String serviceId) throws CloudletException
    {
        String ret = this.commandExecutor.executeCommand(new GetDeviceMessagesCommand(serviceId), this.getAddress().getHostAddress(), this.getPort());
        return DeviceMessageImpl.createFromJson(new JSONObject(ret));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Service> getServices(boolean useCache) throws CloudletException
    {
        log.entry(useCache);

        //If the caller wants us to use the cache, and the cache exists, return that
        //We do not return the actual cache because we do not want external parties modifying it
        if (useCache && servicesCache != null)
            return new ArrayList<Service>(servicesCache);

        String result = this.commandExecutor.executeCommand(new GetServicesCommand(),this.getAddress().getHostAddress(), this.getPort()); //Get the services from the server

        List<Service> _ret = new ArrayList<Service>();

        try
        {
            JSONObject obj = new JSONObject(result);
            JSONArray services = obj.getJSONArray("services");
            for (int x = 0; x < services.length(); x++)
            {
                JSONObject service = services.getJSONObject(x);
                _ret.add(new ServiceImpl(this, service));
            }
        }
        catch (Exception e)
        {
            log.error("Error getting services array from response!", e);
        }

        servicesCache = Collections.unmodifiableList(_ret);

        log.exit(servicesCache);
        return servicesCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<App> getApps() throws CloudletException
    {
        return getApps(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<App> getApps(boolean useCache) throws CloudletException
    {
        log.entry(useCache);

        List<App> ret = getApps(new AppFilter());

        log.exit(ret);
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<App> getApps(AppFilter filter) throws CloudletException
    {
        log.entry(filter);
        List<App> ret = new ArrayList<App>();

        String result = this.commandExecutor.executeCommand(new GetAppListCommand(filter), this.getAddress().getHostAddress(), this.getPort());

        try {
            JSONObject obj = new JSONObject(result);
            JSONArray apps = obj.getJSONArray("apps");
            for (int x = 0; x < apps.length(); x++)
            {
                JSONObject app = apps.getJSONObject(x);
                ret.add(new AppImpl(this, app));
            }
        }
        catch (Exception e)
        {
            log.error("Error getting apps array from response!", e);
        }

        log.exit(ret);
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getApp(String appId, File outFile) throws CloudletException
    {
        GetAppCommand cmd = new GetAppCommand(appId, outFile);

        // Response will contain the MD5 sum for validation.
        String md5 = commandExecutor.executeCommand(cmd, this.getAddress().getHostAddress(), this.getPort());
        return md5;
    }

    public String toString()
    {
        return name + "[" + addr + ":" + port + "]";
    }
}
