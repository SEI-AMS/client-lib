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
package edu.cmu.sei.ams.cloudlet;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jdroot
 * Date: 9/9/14
 * Time: 3:40 PM
 */
public class AppFinder
{
    private static final XLogger log = XLoggerFactory.getXLogger(AppFinder.class);

    private final CloudletFinder cloudletFinder;

    public AppFinder(CloudletFinder cloudletFinder)
    {
        this.cloudletFinder = cloudletFinder;
    }

    /**
     * Find all apps from nearby cloudlets
     * @param filter Filter's the apps that will be returned
     * @return
     */
    public List<App> findApps(AppFilter filter) throws CloudletException {
        log.entry(filter);
        List<App> ret = new ArrayList<App>();
        for (Cloudlet cloudlet: this.cloudletFinder.findCloudlets())
        {
            for (App app : cloudlet.getApps(filter))
                if (!contains(ret, app))
                    ret.add(app);
        }
        log.exit(ret);
        return ret;
    }

    private boolean contains(List<App> apps, App app)
    {
        for (App a : apps)
            if (a.getName().equals(app.getName()))
                return true;
        return false;
    }
}
