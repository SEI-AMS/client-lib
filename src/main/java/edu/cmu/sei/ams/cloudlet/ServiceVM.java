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

import java.net.InetAddress;

/**
 * User: jdroot
 * Date: 3/24/14
 * Time: 4:11 PM
 * Immutable representation of a running service on a Cloudlet
 */
public interface ServiceVM
{
    /**
     * The ID of the service this instance was created from
     */
    public String getServiceId();

    /**
     * The specific ID of this running service
     * @return
     */
    public String getInstanceId();

    /**
     * Returns a FQDN that can be resolved to the IP this VM is running in.
     * @return
     */
    public String getDomainName();

    /**
     * Returns the port that this service is running on
     * @return
     */
    public int getPort();

    /**
     * Will stop this instance of the service
     * @return
     */
    public boolean stopVm() throws CloudletException;

    /**
     * Returns the address that this service is running on
     * If Domain Name is set, this will return null.
     * @return
     */
    public InetAddress getAddress();
}
