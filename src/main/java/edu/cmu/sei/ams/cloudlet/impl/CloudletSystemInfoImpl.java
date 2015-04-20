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

import edu.cmu.sei.ams.cloudlet.CloudletSystemInfo;
import edu.cmu.sei.ams.cloudlet.CpuInfo;
import edu.cmu.sei.ams.cloudlet.MemoryInfo;
import org.json.JSONObject;
import static edu.cmu.sei.ams.cloudlet.impl.CloudletUtilities.*;

/**
 * User: jdroot
 * Date: 4/8/14
 * Time: 11:47 AM
 */
public class CloudletSystemInfoImpl implements CloudletSystemInfo
{

    private final CpuInfo cpuInfo;
    private final MemoryInfo memoryInfo;

    CloudletSystemInfoImpl(JSONObject obj)
    {
        this.cpuInfo = new CpuInfoImpl(getSafeJsonObject("cpu_info", obj));
        this.memoryInfo = new MemoryInfoImpl(getSafeJsonObject("memory_info", obj));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CpuInfo getCPUInfo()
    {
        return cpuInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MemoryInfo getMemoryInfo()
    {
        return memoryInfo;
    }

    @Override
    public String toString()
    {
        return "{cpu_info:" + getCPUInfo() + ",memory_info:" + getMemoryInfo() + "}";
    }
}
