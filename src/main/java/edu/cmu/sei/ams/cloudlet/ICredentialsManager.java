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

/**
 * Created by Sebastian on 2015-11-13.
 */
public interface ICredentialsManager {
    /**
     * Creates the full path given a file name/id.
     * @param fileName The name of the file.
     * @return the full path.
     */
    String getFullPath(String cloudletName, String fileName);

    /**
     * Returns the encryption password.
     * @return the encryption password string.
     */
    String getEncryptionPassword(String cloudletName);

    /**
     * Stores an IBC related file.
     * @param fileContents the data in the file as bytes
     * @param fileId the file name
     */
    void storeFile(String cloudletName, byte[] fileContents, String fileId);

    /**
     * Loads data from a file
     * @param fileId the file name
     * @return the data in the file as a string
     */
    String loadDataFromFile(String cloudletName, String fileId);

    /**
     * Removes all credentials.
     * @return true if they were removed, false otherwise.
     */
    boolean clearCredentials();
}
