package android.tapmisam.az.testregister;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoadingActivity extends AppCompatActivity {
String url,token;
SharedPreferences sharedPreferences;
ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.rgb(153,32,87)));
        sharedPreferences = getApplicationContext().getSharedPreferences("mypref",0);
        progressBar.setMax(100);



        checkUser();
    }
    private void checkUser() {
        progressBar.setVisibility(View.VISIBLE);
        url="http://www.aysel.campuncke.com/public/api/v1/auth/getAuthUser";

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Token:",sharedPreferences.getString("token",token));
                if(response.has("user")){
                    Log.e("MYOBJECT",response.toString());
                }
                startActivity(new Intent(LoadingActivity.this,WelcomeActivity.class));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    String e=new String(networkResponse.data);
                    try {
                        JSONObject json = new JSONObject(e);
                        Toast.makeText(getApplicationContext(),json.get("errors").toString(),Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoadingActivity.this,LoginActivity.class));

                    }catch(JSONException exc){
                        exc.printStackTrace();
                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders()  {
                Map<String,String>params=new HashMap<String,String>();
                params.put("Authorization","Bearer "+sharedPreferences.getString("token",token));
                return params;
            }
        };
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
