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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;

import edu.cmu.sei.ams.cloudlet.CloudletException;
import edu.cmu.sei.ams.cloudlet.impl.cmds.CloudletCommand;

/**
 * Created by Sebastian on 2015-08-19.
 * Handles sending commands to a cloudlet.
 */
public class CloudletCommandExecutorImpl implements CloudletCommandExecutor
{
    private static final XLogger log = XLoggerFactory.getXLogger(CloudletCommandExecutorImpl.class);

    private String deviceId = "";
    private boolean encryptionEnabled = false;
    private AESEncrypter encrypter;

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableEncryption(String deviceId, String password)
    {
        this.encryptionEnabled = true;
        this.encrypter = new AESEncrypter(password);
        this.deviceId = deviceId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disableEncryption()
    {
        this.encryptionEnabled = false;
        this.encrypter = null;
    }

    /**
     * Compiles a command into an unencrypted API call.
     * @param cmd The object with the command details.
     * @return A HttpRequestBase request containing an URL to call the API for the given command.
     */
    @SuppressWarnings("deprecation")
    protected HttpRequestBase compileOpenRequest(CloudletCommand cmd, String ipAddress, int port)
            throws URISyntaxException {

        String apiCommandUrl = String.format("http://%s:%d/api%s", ipAddress, port, cmd.getPath());

        // Create the URL string for the params.
        String args = null;
        for (String key : cmd.getArgs().keySet())
        {
            if (args == null)
                args = "?";
            else
                args += "&";
            args += key + "=" + URLEncoder.encode(cmd.getArgs().get(key));
        }

        // Merge the URL plus the params string.
        if (args != null)
            apiCommandUrl += args;
        log.info("Compiled command: " + apiCommandUrl);

        // Set up the request based on the comand and the selected method.
        HttpRequestBase request;
        switch (cmd.getMethod()) {
            case GET:
                request = new HttpGet();
                break;
            case PUT:
                request = new HttpPut();
                break;
            case POST:
                request = new HttpPost();
                break;
            default:
                request = new HttpGet();
                break;
        }
        request.setURI(new URI(apiCommandUrl));

        return request;
    }

    /**
     * Compiles a command into an encrypted API call.
     * @param cmd The object with the command details.
     * @return A HttpRequestBase request containing an encrypted request to call the API for the given command.
     */
    protected HttpRequestBase compileEncryptedRequest(CloudletCommand cmd, String ipAddress, int port)
            throws URISyntaxException, UnsupportedEncodingException, EncryptionException {

        String apiCommandUrl = String.format("http://%s:%d/api/command", ipAddress, port);

        // Encryption requires everything to be sent by POST.
        HttpPost request = new HttpPost();

        // Put all parameters together with the command.
        String postCommandString = cmd.getPath();
        for (String key : cmd.getArgs().keySet())
        {
            postCommandString += "&" + key + "=" + cmd.getArgs().get(key);
        }

        // Encrypt the command string.
        String encryptedCommandString = this.encrypter.encrypt(postCommandString);

        // Add the encrypted command and the device id.
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("command", encryptedCommandString));

        // Set the URI and params of the request.
        request.setURI(new URI(apiCommandUrl));
        request.setHeader("X-Device-ID", this.deviceId);
        request.setEntity(new UrlEncodedFormEntity(postParameters));

        return request;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("deprecation")
    public String executeCommand(CloudletCommand cmd, String ipAddress, int port) throws CloudletException
    {
        log.entry(cmd.getMethod(), cmd.getPath());

        HttpClient client = null;
        try
        {
            HttpRequestBase request;
            if(encryptionEnabled)
            {
                request = compileEncryptedRequest(cmd, ipAddress, port);
            }
            else
            {
                request = compileOpenRequest(cmd, ipAddress, port);
            }

            client = new DefaultHttpClient();
            HttpResponse response = client.execute(request);

            int code = response.getStatusLine().getStatusCode();
            log.info("Response object: " + response.getStatusLine().getReasonPhrase());

            // Fail if we didn't get a 200 OK
            if (code != 200)
            {
                //Get the error text
                String responseText = getResponseText(response, false);
                throw new CloudletException(response.getStatusLine() + (responseText == null ? "" : ":\n" + responseText));
            }

            String responseText = null;
            //Change how we handle response based on if we are expecting a file or not
            if (cmd.hasFile())
            {
                HttpEntity entity = response.getEntity();
                if (entity != null && entity.getContentLength() > 0)
                {
                    final InputStream is = entity.getContent();
                    final OutputStream os = new FileOutputStream(cmd.getFile());
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    //Compute the md5 sum
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    while ((len = is.read(buffer)) > 0)
                    {
                        md.update(buffer, 0, len);
                        os.write(buffer, 0, len);
                    }
                    os.flush();
                    os.close();
                    is.close();
                    responseText = bytesToHex(md.digest());
                }
                else
                {
                    //Server didnt return a file for some reason
                    throw new CloudletException("Server did not return a file");
                }
            }
            else
            {
                responseText = getResponseText(response, true);
            }

            log.exit(responseText);
            return responseText;
        }
        catch (CloudletException e)
        {
            throw e; //Just pass it on
        }
        catch (Exception e)
        {
            log.error("Error connecting to " + ipAddress + ": " + e.getMessage());
            throw new CloudletException("Error sending command to server!", e);
        }
        finally
        {
            if (client != null)
            {
                try
                {
                    client.getConnectionManager().shutdown();
                }
                catch (Exception e)
                {
                    log.error("Error shutting down http client");
                }
            }
        }
    }

    /**
     * Gets the response text from an HTTP response as String.
     * @param response The HTTP response to get the text from.
     * @return The text in the response as a string.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private String getResponseText(final HttpResponse response, boolean decryptResponse)
    {
        String responseText = "";

        // Return empty in this case.
        if (response == null)
        {
            return responseText;
        }

        try
        {
            final InputStream responseContentInputStream = response.getEntity().getContent();
            if (responseContentInputStream != null)
            {
                Header[] headers = response.getAllHeaders();
                for (Header header : headers) {
                    System.out.println(header.toString());
                }

                // Load the response from the input stream into a byte buffer.
                int size = (int) response.getEntity().getContentLength();
                if (size <= 0)
                    return null;
                byte[] resByteBuf = new byte[size];
                responseContentInputStream.read(resByteBuf);
                responseContentInputStream.close();

                log.info("Response size: " + size);

                // Turn the buffer into a string, which should be straightforward since HTTP uses strings to communicate.
                responseText = new String(resByteBuf);

                // Decrypt if needed.
                if(this.encryptionEnabled && decryptResponse)
                {
                    responseText = this.encrypter.decrypt(responseText);
                }
            }
        }
        catch (IllegalStateException e)
        {
            log.error("Illegal State Exception in the response!", e);
        }
        catch (IOException e)
        {
            log.error("IOException in the response!", e);
        }
        catch (EncryptionException e)
        {
            log.error("EncryptionException in the response!", e);
        }

        return responseText;
    }

    private final static char[] hexArray = "0123456789abcdef".toCharArray();
    private final static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}