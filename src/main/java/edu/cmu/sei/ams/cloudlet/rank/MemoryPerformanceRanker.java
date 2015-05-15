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

package edu.cmu.sei.ams.cloudlet.rank;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import edu.cmu.sei.ams.cloudlet.Cloudlet;
import edu.cmu.sei.ams.cloudlet.CloudletException;
import edu.cmu.sei.ams.cloudlet.CpuInfo;
import edu.cmu.sei.ams.cloudlet.MemoryInfo;
import edu.cmu.sei.ams.cloudlet.Service;

/**
 * Created by Sebastian on 2015-05-14.
 */
public class MemoryPerformanceRanker  implements CloudletRanker
{
    private static final XLogger log = XLoggerFactory.getXLogger(CpuPerformanceRanker.class);

    @Override
    public double rankCloudlet(Service service, Cloudlet cloudlet) throws CloudletException
    {
        log.entry(service, cloudlet);
        CpuInfo cpuInfo = cloudlet.getSystemInfo().getCPUInfo();
        MemoryInfo memInfo = cloudlet.getSystemInfo().getMemoryInfo();

        double cacheWeight = 0;
        double ramWeight = 1.0/1024.0/1024.0;
        double ranking = cacheWeight * (cpuInfo.getCache() * (double)(cpuInfo.getTotalCores())) +
                ramWeight * memInfo.getFreeMemory();

        log.exit(ranking);
        return ranking;
    }
}