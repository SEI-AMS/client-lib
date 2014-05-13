import edu.cmu.sei.ams.cloudlet.Cloudlet;
import edu.cmu.sei.ams.cloudlet.CloudletFinder;
import edu.cmu.sei.ams.cloudlet.Service;
import edu.cmu.sei.ams.cloudlet.ServiceVM;
import org.junit.Test;

import java.util.List;

/**
 * User: jdroot
 * Date: 5/12/14
 * Time: 3:52 PM
 */
public class CloudletTest
{
    @Test
    public void testServices()
    {
        Cloudlet cloudlet = CloudletFinder.findCloudlets().get(0);
        try
        {
            List<Service> services = cloudlet.getServices();
            for (Service service : services)
            {
                System.out.println(service);
                ServiceVM svm = service.startService();
                System.out.println(svm);
                System.out.println("Test1: " + svm.stopVm());
                System.out.println("Test2: " + svm.stopVm());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
