package edu.cmu.sei.ams.cloudlet.impl;

import edu.cmu.sei.ams.cloudlet.App;
import edu.cmu.sei.ams.cloudlet.CloudletException;
import edu.cmu.sei.ams.cloudlet.impl.cmds.GetAppCommand;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static edu.cmu.sei.ams.cloudlet.impl.CloudletUtilities.*;

/**
 * User: jdroot
 * Date: 5/21/14
 * Time: 2:48 PM
 */
public class AppImpl implements App
{
    private final CloudletCommandExecutor mExecutor;
    private final JSONObject json;

    private final String id;
    private final String name;
    private final String packageName;
    private final List<String> tags;
    private final String md5sum;
    private final String minAndroidVersion;
    private final String appVersion;
    private final String serviceId;
    private final String description;

    AppImpl(CloudletCommandExecutor mExecutor, JSONObject json)
    {
        this.mExecutor = mExecutor;
        this.json = json;

        this.id = getSafeString("_id", json);
        this.name = getSafeString("name", json);
        this.packageName = getSafeString("package_name", json);
        this.tags = getSafeStringArray("tags", json);
        this.md5sum = getSafeString("md5sum", json);
        this.minAndroidVersion = getSafeString("min_android_version", json);
        this.serviceId = getSafeString("service_id", json);
        this.appVersion = getSafeString("app_version", json);
        this.description = getSafeString("description", json);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId()
    {
        return this.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPackageName()
    {
        return this.packageName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getTags()
    {
        return this.tags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMD5Sum()
    {
        return this.md5sum;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMinAndroidVersion()
    {
        return this.minAndroidVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAppVersion()
    {
        return this.appVersion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getServiceId()
    {
        return this.serviceId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription()
    {
        return this.description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File downloadApp() throws CloudletException
    {
        return downloadApp(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File downloadApp(File outputDirectory)  throws CloudletException
    {
        //Verify the directory before we use it, default to java.io.tmpdir
        File _outDirectory = outputDirectory;
        if (outputDirectory == null || !outputDirectory.exists())
            _outDirectory = new File(System.getProperty("java.io.tmpdir"));

        File outFile = new File(_outDirectory, this.getName() + ".apk");

        //If the file already exists, not really a reason to download it again
        if (outFile.exists())
            return outFile;

        GetAppCommand cmd = new GetAppCommand(this.getId(), outFile);
        //Response will contain the MD5 sum for validation
        String md5 = mExecutor.executeCommand(cmd);

        //If we dont have an MD5 sum, just let it through
        if (this.getMD5Sum() == null || this.getMD5Sum().trim().length() == 0)
            return outFile;
        //If the md5 sum matches, let it through
        if (md5 != null && md5.equalsIgnoreCase(this.getMD5Sum()))
            return outFile;

        throw new CloudletException(String.format("MD5 mismatch. Expecting '%s' but got '%s'", this.getMD5Sum(), md5));
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public String toString()
    {
        return json.toString();
    }
}
