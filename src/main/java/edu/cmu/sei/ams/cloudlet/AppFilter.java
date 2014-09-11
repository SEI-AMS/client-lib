package edu.cmu.sei.ams.cloudlet;

import java.util.ArrayList;
import java.util.List;

/**
 * User: jdroot
 * Date: 9/10/14
 * Time: 1:33 PM
 */
public class AppFilter
{
    public static final String CURRENT_OS_NAME = System.getProperty("os.name");
    public static final String CURRENT_OS_VERSION = System.getProperty("os.version");

    public String osName;
    public String osVersion;
    public List<String> tags = new ArrayList<String>();
}
