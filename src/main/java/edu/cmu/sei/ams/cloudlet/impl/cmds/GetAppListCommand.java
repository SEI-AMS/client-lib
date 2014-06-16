package edu.cmu.sei.ams.cloudlet.impl.cmds;

/**
 * User: jdroot
 * Date: 6/12/14
 * Time: 2:05 PM
 */
public class GetAppListCommand extends CloudletCommand
{
    private static final String CMD = "/app/getList";

    @Override
    public String getPath()
    {
        return CMD;
    }
}