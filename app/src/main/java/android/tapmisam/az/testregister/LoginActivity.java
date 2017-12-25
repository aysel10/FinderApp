package android.tapmisam.az.testregister;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {
    private static final String EMAIL="email";
    private static final String PASSWORD="password";
    private JSONObject jsonObject;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private EditText verifyEditText;
    private ProgressBar progressBar;
    private String url,token;
    private TextInputLayout inputLayoutEmail,inputLayoutPassword;
    private  TokenManager tokenManager;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        url = "http://www.aysel.campuncke.com/public/api/v1/auth/login";
        sharedPreferences = getApplicationContext().getSharedPreferences("mypref",0);
        editor=sharedPreferences.edit();
        tokenManager=TokenManager.getInstance(sharedPreferences);
        emailEditText=findViewById(R.id.input_email);
        passwordEditText=findViewById(R.id.input_password);
        loginButton=findViewById(R.id.loginButton);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        inputLayoutEmail=findViewById(R.id.input_layout_email);
        inputLayoutPassword=findViewById(R.id.input_layout_password);
        emailEditText.addTextChangedListener(new MyTextWatcher(emailEditText));
        passwordEditText.addTextChangedListener(new MyTextWatcher(passwordEditText));
        verifyEditText=findViewById(R.id.verifyEditText);
        verifyEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

//
    }

    private void login(){
        progressBar.setVisibility(View.VISIBLE);
        final String password = passwordEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        Map<String,String> params=new HashMap<String,String>();
        params.put(EMAIL,email);
        params.put(PASSWORD,password);

            jsonObject = new JSONObject(params);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                         token = response.get("access_token").toString();
//                        tokenManager.saveToken(t);
                        editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.commit();
                        startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));

                        finishAffinity();
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.data != null) {
                        String e = new String(networkResponse.data);
                        try {
                            JSONObject json = new JSONObject(e);
                            Toast.makeText(getApplicationContext(), json.get("errors").toString(), Toast.LENGTH_LONG).show();
                        } catch (JSONException exc) {
                            exc.printStackTrace();
                        }
                    }

                }
            });

            ApplicationController.getInstance().addToRequestQueue(request);


    }
    private boolean validatePassword() {
        if (passwordEditText.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(passwordEditText);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private boolean validateEmail() {
        String email = emailEditText.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(emailEditText);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }
    private class MyTextWatcher implements TextWatcher{
        private View view;

        public MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()){
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }


}
