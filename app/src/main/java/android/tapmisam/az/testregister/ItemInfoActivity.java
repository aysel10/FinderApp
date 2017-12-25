package android.tapmisam.az.testregister;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ItemInfoActivity extends AppCompatActivity  {

    private ImageView itemImageView;
    private  Toolbar toolbar;
    private ProgressBar progressBar;
    private String image,name,surname,url,phoneNumber,email;
    private TextView phoneNumberEditText,emailEditText;
    private ImageButton emailImageButton,phoneNumberImageButton;
    private TextView descriptionTextView,placeTextView;
    private ImageButton showPlaceButton;
    private String lat,lng;
    JsonObjectRequest jsonObjectRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        int id=bundle.getInt("id");
        Log.e("ID",String .valueOf(id));
        itemImageView=findViewById(R.id.itemImageView);
        progressBar=findViewById(R.id.progressBar);
        phoneNumberEditText=findViewById(R.id.phoneNumberEditText);
        emailEditText=findViewById(R.id.emailEditText);
        emailEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "some@email.address" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                startActivity(Intent.createChooser(intent, ""));
            }
        });
        phoneNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(Intent.createChooser(intent,""));
            }
        });
        emailImageButton=findViewById(R.id.emailImageButton);
        phoneNumberImageButton=findViewById(R.id.phoneImageButton);
        placeTextView=findViewById(R.id.placeTextView);
        showPlaceButton=findViewById(R.id.showPlaceButton);
        showPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String coordinates = "http://maps.google.com/maps?daddr=" + lat + "," + lng;

                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(coordinates) );
                startActivity( Intent.createChooser(intent,"") );
            }
        });
        descriptionTextView=findViewById(R.id.descriptionTextView);
        phoneNumberImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(Intent.createChooser(intent,""));
            }
        });
        emailImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "some@email.address" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                intent.putExtra(Intent.EXTRA_TEXT, "mail body");
                startActivity(Intent.createChooser(intent, ""));
            }
        });


        getFullInfo(id);

    }



    private void getFullInfo(long id){
        progressBar.setVisibility(View.VISIBLE);

        url="http://aysel.campuncke.com/public/api/v1/items/get/"+id;
         jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String title=response.getString("title");
                    String description=response.getString("description");
                    String created_at=response.getString("created_at");
                    JSONObject user=response.getJSONObject("user");
                    name=user.getString("name");
                    surname=user.getString("surname");
                    phoneNumber=user.getString("phone_number");
                    email=user.getString("email");
                    JSONArray jsonArray=response.getJSONArray("images");
                    for (int x=0; x<jsonArray.length(); x++) {
                        JSONObject im = jsonArray.getJSONObject(x);
                        image = "http://aysel.campuncke.com"+im.getString("image");
                    }

                    JSONObject place=response.getJSONObject("marker");

                    String address=place.getString("address");
                    lng=place.getString("lng");
                    lat=place.getString("lat");
                    phoneNumberEditText.setText(phoneNumber);
                    emailEditText.setText(email);
                    descriptionTextView.setText(description);
                    placeTextView.setText(address);
                    toolbar.setTitle(title);
                    Glide.with(getApplicationContext()).load(image).listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    }).override(800,800).into(itemImageView);

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);

            }

        }
         );

        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);




    }

}
