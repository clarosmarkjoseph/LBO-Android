package com.system.mobile.lay_bare;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ITDevJr1 on 4/12/2017.
 */

public class ServerHandler {

    private static ServerHandler mInstance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private ServerHandler(Context context)
    {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public com.android.volley.RequestQueue getRequestQueue()
    {
        if(requestQueue==null)
        {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized ServerHandler getInstance(Context context)
    {
        if(mInstance==null)
        {
            mInstance = new ServerHandler(context);
        }
        return mInstance;
    }

    public void addToRequest(Request request)
    {
        requestQueue.add(request);
    }

}
