package com.system.mobile.lay_bare;

import org.json.JSONArray;

import java.util.Observable;

/**
 * Created by paolohilario on 9/3/18.
 */

public class ObservableChat extends Observable {

    private static ObservableChat instance = new ObservableChat();

    public static ObservableChat getInstance(){
        return instance;
    }

    private ObservableChat(){

    }

    public void reloadValue(){
        synchronized (this) {
            setChanged();
            notifyObservers();
        }
    }


}
