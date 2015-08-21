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
import edu.cmu.sei.ams.cloudlet.impl.cmds.CloudletCommand;
import edu.cmu.sei.ams.cloudlet.impl.cmds.GetAppListCommand;
import edu.cmu.sei.ams.cloudlet.impl.cmds.GetMetadataCommand;
import edu.cmu.sei.ams.cloudlet.impl.cmds.GetServicesCommand;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.MessageDigest;
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

    public CloudletImpl(String name, InetAddress addr, int port, CloudletCommandExecutor commandExecutor)
    {
        this.name = name;
        this.addr = addr;
        this.port = port;
        this.commandExecutor = commandExecutor;
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
    public CloudletSystemInfo getSystemInfo() throws CloudletException
    {
        String ret = this.commandExecutor.executeCommand(new GetMetadataCommand(), this.getAddress().getHostAddress(), this.getPort());
        return new CloudletSystemInfoImpl(new JSONObject(ret));
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
                _ret.add(new ServiceImpl(this.commandExecutor, this, service));
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
                ret.add(new AppImpl(this.commandExecutor, this, app));
            }
        }
        catch (Exception e)
        {
            log.error("Error getting apps array from response!", e);
        }

        log.exit(ret);
        return ret;
    }

    public String toString()
    {
        return name + "[" + addr + ":" + port + "]";
    }
}
