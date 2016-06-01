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

import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

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

    private final Cloudlet cloudlet;

    private ServiceVM serviceVM;

    /**
     * Creates an instance a service based on a specific cloudlet
     * @param json The json data describing this service
     */
    ServiceImpl(Cloudlet cloudlet, JSONObject json)
    {
        this.serviceId = getSafeString("service_id", json);
        this.description = getSafeString("description", json);
        this.version = getSafeString("version", json);
        this.tags = getSafeStringArray("tags", json);
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

    @Override
    public ServiceVM startService(boolean join) throws CloudletException
    {
        this.serviceVM = cloudlet.startService(serviceId, join);
        return this.serviceVM;
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
