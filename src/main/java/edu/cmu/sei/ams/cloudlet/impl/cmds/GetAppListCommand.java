package edu.cmu.sei.ams.cloudlet.impl.cmds;

import edu.cmu.sei.ams.cloudlet.AppFilter;

import java.util.Map;

/**
 * User: jdroot
 * Date: 6/12/14
 * Time: 2:05 PM
 */
public class GetAppListCommand extends CloudletCommand
{
    private static final String CMD = "/app/getList";

    /**
     * Default constructor will generate all apps
     */
    public GetAppListCommand()
    {

    }

    public GetAppListCommand(AppFilter filter)
    {
        Map<String,String> args = getArgs();
        if (filter.osName != null)
        {
            args.put("os_name", filter.osName);
            if (filter.osVersion != null)
            {
                args.put("os_version", filter.osVersion);
            }
        }

        if (filter.tags != null && filter.tags.size() > 0)
        {
            String tags = "";
            for (String tag : filter.tags)
            {
                if (tags.length() != 0)
                    tags += ",";
                tags += tag;
            }
            args.put("tags", tags);
        }
    }

    @Override
    public String getPath()
    {
        return CMD;
    }
}