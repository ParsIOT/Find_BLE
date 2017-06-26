package com.find.wifitool.Controller;

/**
 * Created by root on 4/6/17.
 */

public interface Observable {
    //methods to register and unregister observers
    void register(Observer obj);
    void unregister(Observer obj);

    //method to notify observers of change
    void notifyObservers();

    //method to get updates from subject
    Object getUpdate(Observer obj);

}
