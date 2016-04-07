package edu.cmu.sei.ams.cloudlet;

import java.util.HashMap;

/**
 * Created by Sebastian on 2016-04-04.
 */
public interface IDeviceMessageHandler {

    void handleData(HashMap<String, String> data) throws MessageException;
}
