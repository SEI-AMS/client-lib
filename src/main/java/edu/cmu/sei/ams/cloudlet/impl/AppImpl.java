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

import edu.cmu.sei.ams.cloudlet.App;
import edu.cmu.sei.ams.cloudlet.CloudletException;
import edu.cmu.sei.ams.cloudlet.impl.cmds.GetAppCommand;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.io.File;
import java.util.List;

import static edu.cmu.sei.ams.cloudlet.impl.CloudletUtilities.*;

/**
 * User: jdroot
 * Date: 5/21/14
 * Time: 2:48 PM
 */
public class AppImpl implements App
{
    private static final XLogger log = XLoggerFactory.getXLogger(AppImpl.class);
    private final CloudletCommandExecutor mExecutor;
    private final JSONObject json;

    private final String id;
    private final String name;
    private final String packageName;
    private final List<String> tags;
    private final String md5sum;
    private final String minAndroidVersion;
    private final String appVersion;
    private final String serviceId;
    private final String description;

    AppImpl(CloudletCommandExecutor mExecutor, JSONObject json)
    {
        this.mExecutor = mExecutor;
        this.json = json;

        this.id = getSafeString("_id", json);
        this.name = getSafeString("name", json);
        this.packageName = getSafeString("package_name", json);
        this.tags = getSafeStringArray("tags", json);
        this.md5sum = getSafeString("md5sum", json);
        this.minAndroidVersion = getSafeString("min_android_version", json);
        this.serviceId = getSafeString("service_id", json);
        this.appVersion = getSafeString("app_version", json);
        this.description = getSafeString("description", json);

        log.debug("Created app: " + this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackageName()
    {
        return this.packageName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getTags()
    {
        return this.tags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMD5Sum()
    {
        return this.md5sum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMinAndroidVersion()
    {
        return this.minAndroidVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAppVersion()
    {
        return this.appVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceId()
    {
        return this.serviceId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription()
    {
        return this.description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File downloadApp() throws CloudletException
    {
        return downloadApp(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File downloadApp(File outputDirectory) throws CloudletException
    {
        log.entry(outputDirectory);
        //Verify the directory before we use it, default to java.io.tmpdir
        File _outDirectory = outputDirectory;
        if (outputDirectory == null || !outputDirectory.exists())
            _outDirectory = new File(System.getProperty("java.io.tmpdir"));

        File outFile = new File(_outDirectory, this.getName() + ".apk");

        File ret = null;

        // If the file already exists, we will return it
        if (outFile.exists())
        {
            ret = outFile;
        } else
        {
            GetAppCommand cmd = new GetAppCommand(this.getId(), outFile);
            //Response will contain the MD5 sum for validation
            String md5 = mExecutor.executeCommand(cmd);

            //If we dont have an MD5 sum, let it through
            if (this.getMD5Sum() == null)
                ret = outFile;
                //If the md5 sum matches, let it through
            else if (md5 != null && md5.equalsIgnoreCase(this.getMD5Sum()))
                ret = outFile;
            else
            {
                CloudletException e = new CloudletException(
                        String.format("MD5 mismatch. Expecting '%s' but got '%s'", this.getMD5Sum(), md5)
                );
                log.exit(e);
                throw e;
            }
        }
        log.exit(ret);
        return ret;

    }

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public String toString()
    {
        return json.toString();
    }
}
