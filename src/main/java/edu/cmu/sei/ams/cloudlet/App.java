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
