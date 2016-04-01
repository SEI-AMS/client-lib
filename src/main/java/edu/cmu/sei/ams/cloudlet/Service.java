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

import java.util.List;

/**
 * User: jdroot
 * Date: 3/20/14
 * Time: 3:44 PM
 * Immutable representation of a service a Cloudlet is capable of providing
 */
public interface Service
{
    /**
     * Gets the Service ID for a specific service
     * @return
     */
    public String getServiceId();

    /**
     * Gets the textual description of this service
     * @return
     */
    public String getDescription();

    /**
     * Gets the version of this service (may return null)
     * @return
     */
    public String getVersion();

    /**
     * Gets the tags associated with this service
     * @return
     */
    public List<String> getTags();

    /**
     * Asks the server to start the service. Blocks until the service is started.
     * This action will attempt to join an existing VM by default.
     * @return Immutable instance of the information regarding the started service
     */
    public ServiceVM startService() throws CloudletException;

    /**
     * Asks the server to start the service. Blocks until the service is started.
     * @param join Specifies if we should join an existing VM or not
     * @return Immutable instance of the information regarding the started service
     */
    public ServiceVM startService(boolean join) throws CloudletException;

    /**
     * Stops an instance of this service<br/>
     * Should eventually be removed
     * @return wether or not the service was stopped
     */
    public boolean stopService() throws CloudletException;

    /**
     * Gets the current instance of the running vm, if one exists<br/>
     * Should eventually be removed
     * @return Null if the running instance doesnt exist, ServiceVM if it does
     */
    public ServiceVM getServiceVM();

    /**
     * Returns the cloudlet being used.
     * @return
     */
    Cloudlet getCloudlet();
}
