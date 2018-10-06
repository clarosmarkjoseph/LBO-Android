package com.system.mobile.lay_bare;

import com.system.mobile.lay_bare.Sockets.SocketApplication;

/**
 * Created by paolohilario on 3/2/18.
 */

public class SingletonGlobal {

    private static SingletonGlobal instance;
    SocketApplication mySocket   = null;
    boolean ifSocketIsAlreadySet = false;

    public static SingletonGlobal Instance() {
        if (instance == null) {
            instance = new SingletonGlobal();
        }
        return instance;
    }

    public SocketApplication getMySocket(){
        return this.mySocket;
    }

    public void setMySocket(SocketApplication mySocket){
        this.mySocket = mySocket;
    }

    public void setValue(boolean value) {
        ifSocketIsAlreadySet = value;
    }

    public boolean checkIfSocketSet() {
        return ifSocketIsAlreadySet;
    }
}
