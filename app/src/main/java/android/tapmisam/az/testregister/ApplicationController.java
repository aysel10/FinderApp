package android.tapmisam.az.testregister;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by ayselkas on 11/24/17.
 */

public class ApplicationController extends Application {
    private static ApplicationController sInstance;
    private RequestQueue mRequestQueue;

    private final List<RequestQueue.RequestFinishedListener> mFinishedListeners =
            new ArrayList<>();
    public ApplicationController() {
    }

    public ApplicationController(RequestQueue mRequestQueue) {
        this.mRequestQueue = mRequestQueue;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
    }
    public static ApplicationController getInstance() {

        return sInstance;
    }

    public RequestQueue getRequestQueue(){
        if(mRequestQueue==null){
            mRequestQueue= Volley.newRequestQueue(getApplicationContext());
        }
        return  mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG :tag);
        VolleyLog.e("Adding request to queue: %s",req.getUrl());
        getRequestQueue().add(req);
    }
    public <T> void addToRequestQueue(Request<T>req){
        req.setTag(TAG);
        getRequestQueue().add(req);
    }
    public void cancelPendingRequests(Object tag){
        if(mRequestQueue!=null){
            mRequestQueue.cancelAll(this);
        }
    }


}
