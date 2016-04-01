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
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.cmu.sei.ams.cloudlet.DeviceMessage;
import static edu.cmu.sei.ams.cloudlet.impl.CloudletUtilities.*;

/**
 * Created by Sebastian on 2016-03-24.
 */
public class DeviceMessageImpl implements DeviceMessage
{
    private static final XLogger log = XLoggerFactory.getXLogger(DeviceMessageImpl.class);

    private final String message;
    private final HashMap<String, String> params;

    /**
     * Creates a list of messages from a JSON object.
     * @param jsonData
     * @return
     */
    public static List<DeviceMessage> createFromJson(JSONObject jsonData)
    {
        log.debug("Raw message data: " + jsonData);
        List<DeviceMessage> messages = new ArrayList<DeviceMessage>();

        JSONArray jsonMessages = getSafeJsonArray("messages", jsonData);
        if(jsonMessages != null)
        {
            for (int i = 0; i < jsonMessages.length(); i++)
            {
                JSONObject jsonMessage = jsonMessages.getJSONObject(i);
                DeviceMessage message = new DeviceMessageImpl(jsonMessage);
                messages.add(message);
            }
        }

        else
        {
            log.debug("No messages found!");
        }

        return messages;
    }

    /**
     * Creates the device message from a JSON object.
     * @param jsonObject
     */
    public DeviceMessageImpl(JSONObject jsonObject)
    {
        log.debug("Creating device message from JSON object: " + jsonObject);

        message = getSafeString("message", jsonObject);

        params = new HashMap<String, String>();
        JSONObject jsonParams = getSafeJsonObject("params", jsonObject);
        if(jsonParams != null)
        {
            Iterator<String> keys = jsonParams.keys();
            while(keys.hasNext())
            {
                String key = keys.next();
                String value = getSafeString(key, jsonParams);
                getParams().put(key, value);
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, String> getParams() {
        return params;
    }
}
