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
package edu.cmu.sei.ams.cloudlet.impl.cmds;

import edu.cmu.sei.ams.cloudlet.AppFilter;

import java.util.Map;

/**
 * User: jdroot
 * Date: 6/12/14
 * Time: 2:05 PM
 */
public class GetAppListCommand extends CloudletCommand
{
    private static final String CMD = "/app/getList";

    /**
     * Default constructor will generate all apps
     */
    public GetAppListCommand()
    {

    }

    public GetAppListCommand(AppFilter filter)
    {
        Map<String,String> args = getArgs();
        if (filter.osName != null)
        {
            args.put("os_name", filter.osName);
            if (filter.osVersion != null)
            {
                args.put("os_version", filter.osVersion);
            }
        }

        if (filter.tags != null && filter.tags.size() > 0)
        {
            String tags = "";
            for (String tag : filter.tags)
            {
                if (tags.length() != 0)
                    tags += ",";
                tags += tag;
            }
            args.put("tags", tags);
        }
    }

    @Override
    public String getPath()
    {
        return CMD;
    }
}