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
    private final CloudletCommandExecutor commandExecutor;
    private final Cloudlet cloudlet;

    ServiceVMImpl(CloudletCommandExecutor commandExecutor, Cloudlet cloudlet, Service mService, JSONObject obj)
    {
        log.entry(commandExecutor, mService, obj);
        this.commandExecutor = commandExecutor;
        this.cloudlet = cloudlet;
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
    public boolean stopVm() throws CloudletException
    {
        try
        {
            StopVMInstanceCommand cmd = new StopVMInstanceCommand(this);
            String result = commandExecutor.executeCommand(cmd, this.cloudlet.getAddress().getHostAddress(), this.cloudlet.getPort());
            //Result is ignored, it will be a blank json object on success
            return true;
        }
        catch (CloudletException e)
        {
            // We want this type of exception to be rethrown, so that it can be shown to the user.
            throw e;
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
