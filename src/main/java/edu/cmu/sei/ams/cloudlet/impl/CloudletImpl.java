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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: jdroot
 * Date: 3/19/14
 * Time: 4:05 PM
 * CloudletImpl handles both the cloudlet metadata issuing commands to the cloudlet
 */
public class CloudletImpl implements Cloudlet, CloudletCommandExecutor
{
    private static final XLogger log = XLoggerFactory.getXLogger(CloudletImpl.class);

    private final String name;
    private final InetAddress addr;
    private final int port;

    private List<Service> servicesCache;

    public CloudletImpl(String name, InetAddress addr, int port)
    {
        this.name = name;
        this.addr = addr;
        this.port = port;
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
    @SuppressWarnings("deprecation")
    public String executeCommand(CloudletCommand cmd) throws CloudletException
    {
        log.entry(cmd.getMethod(), cmd.getPath());

        HttpClient client = null;

        String command = String.format("http://%s:%d/api%s",
                getAddress().getHostAddress(),
                getPort(),
                cmd.getPath());

        String args = null;
        for (String key : cmd.getArgs().keySet())
        {
            if (args == null)
                args = "?";
            else
                args += "&";
            args += key + "=" + URLEncoder.encode(cmd.getArgs().get(key));
        }

        if (args != null)
            command += args;

        log.info("Compiled command: " + command);

        HttpRequestBase request;
        switch (cmd.getMethod())
        {
            case GET:
                request = new HttpGet();
                break;
            case PUT:
                request = new HttpPut();
                break;
            case POST:
                request = new HttpPost();
                break;
            default:
                log.exit("");
                return "";
        }

        try
        {

            client = new DefaultHttpClient();

            request.setURI(new URI(command));

            HttpResponse response = client.execute(request);

            int code = response.getStatusLine().getStatusCode();
            log.info("Response object: " + response.getStatusLine().getReasonPhrase());

            //Fail if we didnt get a 200 OK
            if (code != 200)
            {
                //Get the error text
                String responseText = getResponseText(response);
                throw new CloudletException(response.getStatusLine() + (responseText == null ? "" : ":\n" + responseText));
            }


            String responseText = null;
            //Change how we handle response based on if we are expecting a file or not
            if (cmd.hasFile())
            {
                HttpEntity entity = response.getEntity();
                if (entity != null && entity.getContentLength() > 0)
                {
                    final InputStream is = entity.getContent();
                    final OutputStream os = new FileOutputStream(cmd.getFile());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    //Compute the md5 sum
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    while ((len = is.read(buffer)) > 0)
                    {
                        md.update(buffer, 0, len);
                        os.write(buffer, 0, len);
                    }
                    os.flush();
                    os.close();
                    is.close();
                    responseText = bytesToHex(md.digest());
                }
                else
                {
                    //Server didnt return a file for some reason
                    throw new CloudletException("Server did not return a file");
                }
            }
            else
            {
                responseText = getResponseText(response);
            }

            log.exit(responseText);
            return responseText;
        }
        catch (CloudletException e)
        {
            throw e; //Just pass it on
        }
        catch (Exception e)
        {
            log.error("Error connecting to " + getAddress() + ": " + e.getMessage());
            throw new CloudletException("Error sending command to server!", e);
        }
        finally
        {
            if (client != null)
            {
                try
                {
                    client.getConnectionManager().shutdown();
                }
                catch (Exception e)
                {
                    log.error("Error shutting down http client");
                }
            }
        }
    }

    @Override
    public InetAddress getInetAddress()
    {
        return addr;
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
        String ret = this.executeCommand(new GetMetadataCommand());
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

        String result = executeCommand(new GetServicesCommand()); //Get the services from the server

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

        String result = executeCommand(new GetAppListCommand(filter));

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
     * Gets the response text from an HTTP response as String.
     * @param response The HTTP response to get the text from.
     * @return The text in the response as a string.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String getResponseText(final HttpResponse response)
    {
        String responseText = "";

        // Return empty in this case.
        if (response == null)
        {
            return responseText;
        }

        try
        {
            final InputStream responseContentInputStream = response.getEntity().getContent();
            if (responseContentInputStream != null)
            {
                // Load the response from the input stream into a byte buffer.
                int size = (int) response.getEntity().getContentLength();
                if (size <= 0)
                    return null;
                byte[] resByteBuf = new byte[size];
                responseContentInputStream.read(resByteBuf);
                responseContentInputStream.close();

                // Turn the buffer into a string, which should be straightforward since HTTP uses strings to communicate.
                responseText = new String(resByteBuf);
            }
        }
        catch (IllegalStateException e)
        {
            log.error("Illegal State Exception in the response!", e);
        }
        catch (IOException e)
        {
            log.error("IOException in the response!", e);
        }

        return responseText;
    }

    public String toString()
    {
        return name + "[" + addr + ":" + port + "]";
    }

    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    private final static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
