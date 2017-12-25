package android.tapmisam.az.testregister;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity implements SearchView.OnQueryTextListener,GeneralFragment.OnFragmentInteractionListener{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SharedPreferences.Editor editor;
    private String token;
    private JSONObject jsonObject;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private String url;
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sharedPreferences = getApplicationContext().getSharedPreferences("mypref",0);
        editor=sharedPreferences.edit();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        progressBar=findViewById(R.id.welcomProgressBar);
        viewPager=findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        tabLayout=findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        progressBar.setVisibility(View.GONE);

    }

    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GeneralFragment(),"General");
        adapter.addFragment(new LoseFragment(),"Lose");
        adapter.addFragment(new FindFragment(),"Find");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList=new ArrayList<>();
        private final List<String>mFragmentTitleList=new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
        public void addFragment(Fragment fragment,String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logoutMenu:
                logout();
                sharedPreferences.getAll().clear();
                Log.e("ewef","wefwe");


        }
        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        progressBar.setVisibility(View.VISIBLE);
        url="http://aysel.campuncke.com/public/api/v1/auth/logout";
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String m=response.getString("message");
                    Toast.makeText(getApplicationContext(),m,Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    finishAffinity();
                    progressBar.setVisibility(View.GONE);
                }catch(JSONException e){
                    e.printStackTrace();
                }
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
                    }catch(JSONException exc){
                        exc.printStackTrace();
                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String>params=new HashMap<String,String>();
                params.put("Authorization","Bearer "+sharedPreferences.getString("token",token));
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);

    }

}