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

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: jdroot
 * Date: 3/21/14
 * Time: 10:04 AM
 * A collection of utilities for parsing JSON strings without throwing exceptions
 */
public class CloudletUtilities
{
    static String getSafeString(String name, JSONObject json)
    {
        try
        {
            if (json.has(name))
            {
                // We do a null check here because the server will return "null" instead of null
                String val = json.getString(name);
                if (val != null && val.equalsIgnoreCase("null"))
                    val = null;
                return val;
            }
        }
        catch (Exception e)
        {

        }
        return null;
    }

    static int getSafeInt(String name, JSONObject json)
    {
        try
        {
            if (json.has(name))
                return json.getInt(name);
        }
        catch (Exception e)
        {

        }
        return 0;
    }

    static double getSafeDouble(String name, JSONObject json)
    {
        try
        {
            if (json.has(name))
                return json.getDouble(name);
        }
        catch (Exception e)
        {

        }
        return 0.0;
    }

    static long getSafeLong(String name, JSONObject json)
    {
        try
        {
            if (json.has(name))
                return json.getLong(name);
        }
        catch (Exception e)
        {

        }
        return 0l;
    }

    static InetAddress getSafeInetAddress(String name, JSONObject json)
    {
        String value = getSafeString(name, json);
        if (value == null)
            return null;
        try
        {
            return InetAddress.getByName(value);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    static JSONObject getSafeJsonObject(String name, JSONObject json)
    {
        try
        {
            if (json.has(name))
                return json.getJSONObject(name);
        }
        catch (Exception e)
        {

        }
        return null;
    }

    /**
     * Creates an immutable list based on a comma seperated json string
     * @param name
     * @param json
     * @return
     */
    static List<String> getSafeStringArray(String name, JSONObject json)
    {
        try
        {
            if (json.has(name))
            {
                JSONArray array = json.getJSONArray(name);
                List<String> ret = new ArrayList<String>();
                for (int x = 0; x < array.length(); x++)
                    ret.add(array.getString(x));
                return Collections.unmodifiableList(ret);
            }
        }
        catch (Exception e)
        {

        }
        return new ArrayList<String>();
    }
}
