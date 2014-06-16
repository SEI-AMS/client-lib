package edu.cmu.sei.ams.cloudlet.impl;

import edu.cmu.sei.ams.cloudlet.CloudletException;
import edu.cmu.sei.ams.cloudlet.impl.cmds.CloudletCommand;

import java.net.InetAddress;

/**
 * User: jdroot
 * Date: 3/24/14
 * Time: 3:18 PM
 * Internal interface for an object that is able to execute Cloudlet commands. The only implementation is CloudletImpl.
 * This interface allows other objects to be independent of the CloudletImpl should it ever change.
 */
public interface CloudletCommandExecutor
{
    public String executeCommand(CloudletCommand cmd) throws CloudletException;

    public InetAddress getInetAddress();
}
