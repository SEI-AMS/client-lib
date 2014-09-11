package edu.cmu.sei.ams.cloudlet;

import java.net.InetAddress;
import java.util.List;

/**
 * User: jdroot
 * Date: 3/19/14
 * Time: 1:28 PM
 *
 * This interface is an immutable representation of an existing Cloudlet server
 */
public interface Cloudlet
{
    /**
     * Gets the name of this Cloudlet
     * @return the name of this Cloudlet
     */
    public String getName();

    /**
     * Gets the address of this Cloudlet
     * @return address of this Cloudlet
     */
    public InetAddress getAddress();

    /**
     * Gets the port this cloudlet is listening on
     * @return port
     */
    public int getPort();

    /**
     * Gets a list of services available on this Cloudlet
     * @return list of services
     * @throws CloudletException can throw exceptions when server is unreachable or bad data is returned
     */
    public List<Service> getServices() throws CloudletException;

    /**
     * Gets a list of services available on this Cloudlet
     * @param useCache flag to specify if we should used our cached list
     * @return list of services
     * @throws CloudletException can throw exceptions when server is unreachable or bad data is returned
     */
    public List<Service> getServices(boolean useCache) throws CloudletException;

    /**
     * Will locate a service based on the ID of the service
     * @param id The service ID to look for
     * @return The service object or null if it is not found
     * @throws CloudletException
     */
    public Service getServiceById(String id) throws CloudletException;

    /**
     * Gets the current CPU and Memory state of the Cloudlet for evaluating Cloudlet selection
     * @return
     * @throws CloudletException can throw exceptions when the server is unreachable or bad data is returned
     */
    public CloudletSystemInfo getSystemInfo() throws CloudletException;

    /**
     * Gets a list of apps available for installation from this Cloudlet
     * @return list of apps
     * @throws CloudletException
     */
    public List<App> getApps() throws CloudletException;

    /**
     * Gets a list of apps available for installation from this Cloudlet
     * @param useCache flag to specify if we should use the cached list
     * @return list of apps
     * @throws CloudletException
     */
    public List<App> getApps(boolean useCache) throws CloudletException;

    /**
     *
     * @param filter Filters to apply to this request
     * @return A list of apps matching the correct tags
     * @throws CloudletException
     */
    public List<App> getApps(AppFilter filter) throws CloudletException;
}
