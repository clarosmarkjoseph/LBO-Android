package com.system.mobile.lay_bare.Sockets;
import android.content.Context;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by paolohilario on 1/22/18.
 */

public class SocketApplication {

    Context context;

    private Socket mSocket;{
        IO.Options opts = new IO.Options();
        opts.reconnection       = true;
//        opts.reconnectionDelay = 0;
//        opts.reconnectionAttempts = 1000;
//        opts.path = "/relay.bus/socket.io";
        try {
            mSocket = IO.socket("https://socket.lay-bare.com",opts);
//            https://socket.lay-bare.com
//            https://lbo-express.azurewebsites.net
        }
        catch (URISyntaxException e) {
            throw new RuntimeException("Socket connection failed.", e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

}
