package edu.cmu.sei.ams.cloudlet;

/**
 * Created by Sebastian on 2016-04-04.
 */
public class MessageException extends Exception {
    public MessageException(String message) {
        super(message);
    }

    public MessageException(Exception parentException) {
        super(parentException);
    }
}
