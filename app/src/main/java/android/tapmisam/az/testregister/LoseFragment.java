package android.tapmisam.az.testregister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import static android.app.Activity.RESULT_OK;
public class LoseFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    private FloatingActionButton buttonChoose;
    private Button buttonUpload;
    private ImageView imageView;
    private final String TITLE="title";
    private final String DESCRIPTION="description";
    private final String PLACE_NAME="place_name";
    private final String LONGITUDE="lng";
    private final String LATITUDE="lat";
    private final String ADDRESS="address";
    private EditText titleEditText;
    private EditText descriptionEditText;
    private JSONObject jsonObject;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private String KEY_IMAGE = "image";
    private String KEY_NAME = "name";
    private String UPLOAD_URL = "http://aysel.campuncke.com/public/api/v1/items/post";
    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;
    private TextView tvPlaceDetails;
    private FloatingActionButton fabPickPlace;
    private String place_name,address,lng,lat;

    // TODO: Rename parameter arguments, choose names that match

    public LoseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_lose, container, false);
        buttonChoose = view.findViewById(R.id.buttonChoose);
        sharedPreferences = getContext().getSharedPreferences("mypref",0);
        editor=sharedPreferences.edit();
        titleEditText=view.findViewById(R.id.titleEditText);
        descriptionEditText= view.findViewById(R.id.descriptionEdittext);
        buttonUpload = (Button) view.findViewById(R.id.buttonUpload);
        imageView = (ImageView) view.findViewById(R.id.myImageView);
        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();

            }
        });
        fabPickPlace=view.findViewById(R.id.locationFab);

        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(),this)
                .build();
        fabPickPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                 startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        return  view;
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
       startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data,getContext());
                StringBuilder stBuilder = new StringBuilder();
                place_name = String.format("%s", place.getName());
                lat = String.valueOf(place.getLatLng().latitude);
                lng = String.valueOf(place.getLatLng().longitude);
                address = String.format("%s", place.getAddress());
                stBuilder.append("Name: ");
                stBuilder.append(place_name);
                stBuilder.append("\n");
                stBuilder.append("Latitude: ");
                stBuilder.append(lat);
                stBuilder.append("\n");
                stBuilder.append("Logitude: ");
                stBuilder.append(lng);
                stBuilder.append("\n");
                stBuilder.append("Address: ");
                stBuilder.append(address);
                Log.e(stBuilder.toString(),"dff");
                Log.e(lng,lat);

            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());

        mGoogleApiClient.disconnect();

    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Uploading...", "Please wait...", false, false);

        final String title=titleEditText.getText().toString().trim();
        final String description=descriptionEditText.getText().toString().trim();

        Log.e(lat,lng+" "+place_name+" "+address);

        Map<String,String> params=new HashMap<>();

        String image = getStringImage(bitmap);

        //Getting Image Name
        String name = UUID.randomUUID().toString();

        //Creating parameters
        //Adding parameters
        params.put(TITLE,title);
        params.put(KEY_IMAGE, image);
        params.put(KEY_NAME, name);
        params.put(DESCRIPTION,description);
        params.put(PLACE_NAME,place_name);
        params.put(ADDRESS,address);
        params.put(LONGITUDE,lng);
        params.put(LATITUDE,lat);

        jsonObject=new JSONObject(params);

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, UPLOAD_URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading.dismiss();
                //Showing toast message of the response
                Toast.makeText(getActivity(), KEY_IMAGE, Toast.LENGTH_LONG).show();
                try {
                    String message=response.get("message").toString();
                    Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
                    Log.i("Good Work,",message);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Log.e("ERROR",error.toString());
//                NetworkResponse networkResponse = error.networkResponse;
//                if (networkResponse != null && networkResponse.data != null) {
//                    String e=new String(networkResponse.data);
//                    try {
//                        JSONObject json = new JSONObject(e);
//                        Toast.makeText(getContext(),json.get("errors").toString(),Toast.LENGTH_LONG).show();
//                    }catch(JSONException exc){
//                        exc.printStackTrace();
//                    }
//                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String>params=new HashMap<String,String>();
                params.put("Authorization","Bearer "+sharedPreferences.getString("token","mytoken"));
                return params;
            }


        };

        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
//        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
//            @Override
//            public int getCurrentTimeout() {
//                return 50000;
//            }
//
//            @Override
//            public int getCurrentRetryCount() {
//                return 50000;
//            }
//
//            @Override
//            public void retry(VolleyError error) throws VolleyError {
//
//            }
//        });
//
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(fabPickPlace, connectionResult.getErrorMessage() + "", Snackbar.LENGTH_LONG).show();

    }
}
