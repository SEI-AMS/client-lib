package edu.cmu.sei.ams.cloudlet.impl.cmds;

import java.io.File;

/**
 * User: jdroot
 * Date: 6/12/14
 * Time: 4:13 PM
 */
public class GetAppCommand extends CloudletCommand
{
    private static final String PATH = "/app/getApp";
    private final File outFile;

    public GetAppCommand(String appId, File outFile)
    {
        this.getArgs().put("app_id", appId);
        this.outFile = outFile;
    }

    @Override
    public String getPath()
    {
        return PATH;
    }

    @Override
    public File getFile()
    {
        return outFile;
    }
}
