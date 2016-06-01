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

import java.util.HashMap;
import java.util.List;

import edu.cmu.sei.ams.cloudlet.Cloudlet;
import edu.cmu.sei.ams.cloudlet.CloudletException;
import edu.cmu.sei.ams.cloudlet.DeviceMessage;
import edu.cmu.sei.ams.cloudlet.impl.AppImpl;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Created by Sebastian on 2016-03-28.
 */
public class DeviceMessageManager implements ICurrentCloudlerHolder {
    private static final XLogger log = XLoggerFactory.getXLogger(AppImpl.class);
    private HashMap<String, IDeviceMessageHandler> handlers = new HashMap<String, IDeviceMessageHandler>();

    private boolean stopped = false;

    public void stop(){
        stopped = true;
    }

    private Cloudlet currentCloudlet;

    public void registerHandler(String message, IDeviceMessageHandler newHandler) {
        handlers.put(message, newHandler);
    }

    @Override
    public void setCurrentCloudlet(Cloudlet cloudlet) {
        currentCloudlet = cloudlet;
    }

    public void execute(Cloudlet cloudlet, String serviceId) {
        setCurrentCloudlet(cloudlet);

        while(!stopped) {
            try {
                List<DeviceMessage> messages = currentCloudlet.getMessages(serviceId);

                for (DeviceMessage message : messages) {
                    String messageText = message.getMessage();
                    log.debug("Got message: " + messageText);
                    if (handlers.containsKey(messageText)) {
                        try {
                            log.debug("Found handler for message: " + messageText);
                            HashMap<String, String> params = message.getParams();
                            handlers.get(messageText).handleData(params, this);
                        } catch (MessageException e) {
                            e.printStackTrace();
                        }
                    } else {
                        //Log.w("DeviceMessageManager", "Unknown message type: " + messageText);
                    }
                }

                int pollTimeInMs = 10 * 1000;
                Thread.sleep(pollTimeInMs);
            } catch (InterruptedException e) {
                // This means the thread should stop.
                stopped = true;
            } catch (CloudletException e) {
                // TODO: improve error handling.
                e.printStackTrace();
            } catch (Exception e) {
                // TODO: improve error handling.
                e.printStackTrace();
            }
        }
    }
}
