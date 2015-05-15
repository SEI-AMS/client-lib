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

import edu.cmu.sei.ams.cloudlet.impl.CloudletImpl;
import edu.cmu.sei.ams.cloudlet.rank.CloudletRanker;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static edu.cmu.sei.ams.cloudlet.Constants.*;

/**
 * User: jdroot
 * Date: 3/19/14
 * Time: 1:35 PM
 * CloudletFinder is the entry point to the Cloudlet library.
 * It is used to locate Cloudlets on the current network
 */
public class CloudletFinder
{
    private static final XLogger log = XLoggerFactory.getXLogger(CloudletFinder.class);

    /**
     * findCloudlets will locate all Cloudlets on the current network and return them, unfiltered.
     * @return
     */
    public static List<Cloudlet> findCloudlets()
    {
        log.entry();
        List<Cloudlet> ret = new ArrayList<Cloudlet>();
        try
        {
            //JmDNS will find all of the Cloudlets, but it will block for a few seconds
            JmDNS jmdns = JmDNS.create(InetAddress.getByName("255.255.255.255"));
            ServiceInfo[] info = jmdns.list(CLOUDLET_SERVER_DNS);

            //For each cloudlet that is returned, create a new CloudletImpl for it
            for (ServiceInfo i : info)
            {
                String name = i.getName();
                InetAddress addr = i.getInetAddresses()[0];
                int port = i.getPort();
                ret.add(new CloudletImpl(name, addr, port));
            }
        }
        catch (UnknownHostException e)
        {
            log.error("Unknown Host Exception", e);
        }
        catch (IOException e)
        {
            log.error("IO Exception", e);
        }
        log.exit(ret);
        return ret;
    }

    /**
     * findCloudletForService will locate Cloudlets that can proved a specific service and
     * then rank them based on the supplied ranker. The highest ranked Cloudlet is returned.
     * @param serviceId
     * @param ranker
     * @return
     */
    public static Cloudlet findCloudletForService(String serviceId, CloudletRanker ranker)
    {
        log.entry();
        if (ranker == null)
            return null;
        if (serviceId == null)
            return null;

        Cloudlet ret = null;

        List<Cloudlet> cloudlets = findCloudlets(); //Blocks while looking for Cloudlets

        double max_rank = 0.0;

        for (Cloudlet c: cloudlets)
        {
            try
            {
                List<Service> services = c.getServices();
                for (Service s : services)
                {
                    //Only call the ranker if this Cloudlet can offer this service
                    if (serviceId.equalsIgnoreCase(s.getServiceId()))
                    {
                        try
                        {
                            double val = ranker.rankCloudlet(s, c);
                            log.info("Ranking for " + c.getName() + ": " + val);
                            if (val > max_rank)
                            {
                                max_rank = val;
                                ret = c;
                            }
                        }
                        catch (CloudletException e)
                        {
                            log.error("Error running ranker: " + ranker + " on " + c + " with service " + s);
                        }

                        break; //For now, we only care about one service
                    }
                }
            }
            catch (CloudletException e)
            {
                log.error("Error getting services for " + c);
            }
        }

        log.exit(ret);
        return ret;
    }

    /**
     * Will locate all nearby services for a Cloudlet.
     * @return List of Service Ids
     */
    public static List<String> findAllNearbyServices()
    {
        log.entry();
        List<Cloudlet> cloudlets = findCloudlets();
        List<String> ret = new ArrayList<String>();

        for (Cloudlet cloudlet : cloudlets)
        {
            try
            {
                List<Service> services = cloudlet.getServices();
                for (Service service : services)
                {
                    if (!ret.contains(service.getServiceId()))
                        ret.add(service.getServiceId());
                }
            }
            catch (CloudletException e)
            {
                log.error("Error getting services for cloudlet: " + cloudlet.getName(), e);
            }
        }

        log.exit(ret);
        return ret;
    }
}
