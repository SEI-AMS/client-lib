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

import java.io.File;
import java.util.List;

/**
 * User: jdroot
 * Date: 5/21/14
 * Time: 2:42 PM
 * Immutable representation of an app that the cloudle has
 */
public interface App
{
    /**
     * Gets the ID of the app
     * @return
     */
    public String getId();

    /**
     * Gets the name of this application
     * @return
     */
    public String getName();

    /**
     * Gets the package name for this app
     * @return
     */
    public String getPackageName();

    /**
     * Get the search tags for this app
     * @return
     */
    public List<String> getTags();

    /**
     * Gets the md5sum of the apk file
     * @return
     */
    public String getMD5Sum();

    /**
     * Gets the minimum android version required for this app
     * @return
     */
    public String getMinAndroidVersion();

    /**
     * Gets the version of this app
     * @return
     */
    public String getAppVersion();

    /**
     * Gets the service ID of the service this app may use
     * @return
     */
    public String getServiceId();

    /**
     * Gets the written description of the app
     * @return
     */
    public String getDescription();

    /**
     * Will download the file to the temp directory and return it
     * @return
     */
    public File downloadApp() throws CloudletException;

    /**
     * Will download the file to the specified output directory and return it
     * @param outputDirectory
     * @return
     */
    public File downloadApp(File outputDirectory) throws CloudletException;
}
