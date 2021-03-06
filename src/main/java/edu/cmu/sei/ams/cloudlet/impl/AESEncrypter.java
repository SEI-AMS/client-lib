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

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by Sebastian on 2015-08-14.
 */
public class AESEncrypter {
    private static final XLogger log = XLoggerFactory.getXLogger(AESEncrypter.class);

    private static final int blockSize = 16;

    protected String password;
    protected SecretKeySpec skeySpec;

    public AESEncrypter(String password)
    {
        this.password = password;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            this.skeySpec = new SecretKeySpec(digest.digest(this.password.getBytes("UTF-8")), "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static byte[] ivCipherConcat(byte[] iv, byte[] cipherText) {
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
        return combined;
    }

    /**
     * Encrypts data and returns the encrypted string.
     * @param clear A string to encrypt.
     * @return An encrypted string.
     * @throws EncryptionException
     */
    public String encryptFromString(String clear) throws EncryptionException {
        return encrypt(clear.getBytes());
    }

    /**
     * Encrypts data and returns the encrypted string.
     * @param clear A byte array to encrypt.
     * @return An encrypted string.
     * @throws EncryptionException
     */
    public String encrypt(byte[] clear) throws EncryptionException {
        try
        {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            byte[] b = new byte[blockSize];
            random.nextBytes(b);
            byte[] iv = b;
            //log.info("IV: " + String.valueOf(Hex.encodeHex(iv)));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, this.skeySpec, new IvParameterSpec(iv));

            byte[] encrypted = cipher.doFinal(clear);
            //log.info("Cipher Text: " + String.valueOf(Hex.encodeHex(encrypted)));
            String encryptedString = new String(Base64.encodeBase64(ivCipherConcat(iv, encrypted)));
            return encryptedString;
        }
        catch(Exception e)
        {
            throw new EncryptionException("Error encrypting information", e);
        }
    }

    public String decryptToString(String encrypted) throws EncryptionException {
        byte[] decrypted = decrypt(encrypted);
        String decryptedString = new String(decrypted);
        return decryptedString;
    }

    public byte[] decrypt(String encrypted) throws EncryptionException {
        try {
            //log.info("Encrypted string (" + encrypted.length() + "): " + encrypted);
            byte[] cryptedBytes = Base64.decodeBase64(encrypted.getBytes());
            log.info("Crypted bytes length: " + cryptedBytes.length);

            byte[] iv = new byte[blockSize];
            System.arraycopy(cryptedBytes, 0, iv, 0, blockSize);
            //log.info("IV: " + String.valueOf(Hex.encodeHex(iv)));
            byte[] crypted = new byte[cryptedBytes.length - blockSize];
            System.arraycopy(cryptedBytes, blockSize, crypted, 0, crypted.length);
            //log.info("Cipher Text: " + String.valueOf(Hex.encodeHex(crypted)));

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, this.skeySpec, new IvParameterSpec(iv));

            byte[] decrypted = cipher.doFinal(crypted);
            log.info("Data decrypted.");
            return decrypted;
        }
        catch(Exception e)
        {
            log.error("Error decrypting string (" + encrypted.length() + "): " + encrypted);
            throw new EncryptionException("Error decrypting information", e);
        }
    }
}
