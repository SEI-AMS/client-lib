package edu.cmu.sei.ams.cloudlet.impl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.ArrayList;
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
                return json.getString(name);
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
                return ret;
            }
        }
        catch (Exception e)
        {

        }
        return null;
    }
}
