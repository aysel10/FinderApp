package android.tapmisam.az.testregister;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String USERNAME="name";
    public static final String SURNAME="surname";
    public static final String EMAIL="email";
    public static final String PASSWORD="password";
    public static final String NUMBER="phone_number";
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText surnameEditText;
    private EditText passwordEditText,phoneNumberEditText;
    private TextInputLayout inputLayoutEmail,inputLayoutPassword,inputLayoutName,inputLayoutSurname,inputLayoutPhone;

    private Button signup;

    private SharedPreferences.Editor editor;
    private  JSONObject jsonObject;
    private  String url;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        url = "http://www.aysel.campuncke.com/public/api/v1/auth/register";
        inputLayoutEmail=findViewById(R.id.input_layout_email);
        inputLayoutPassword=findViewById(R.id.input_layout_password);
        inputLayoutPhone=findViewById(R.id.input_layout_phone);
        inputLayoutName=findViewById(R.id.input_layout_name);
        inputLayoutSurname=findViewById(R.id.input_layout_surname);
        emailEditText = findViewById(R.id.input_email);
        passwordEditText = findViewById(R.id.input_password);
        phoneNumberEditText=findViewById(R.id.phoneNumberEditText);
        nameEditText = findViewById(R.id.nameEditText);
        surnameEditText=findViewById(R.id.surnameEditText);
        emailEditText.addTextChangedListener(new MyTextWatcher(emailEditText));
        passwordEditText.addTextChangedListener(new MyTextWatcher(passwordEditText));
        nameEditText.addTextChangedListener(new MyTextWatcher(nameEditText));
        surnameEditText.addTextChangedListener(new MyTextWatcher(surnameEditText));
        phoneNumberEditText.addTextChangedListener(new MyTextWatcher(phoneNumberEditText));
        sharedPreferences = getApplicationContext().getSharedPreferences("mypref",0);
        editor=sharedPreferences.edit();
        signup = findViewById(R.id.signupButton);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeJsonArrayRequest();

            }
        });
    }
    private void makeJsonArrayRequest(){
        final String name = nameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();
        final String email = emailEditText.getText().toString().trim();
        final String surname=surnameEditText.getText().toString().trim();
        final String phone_number=phoneNumberEditText.getText().toString().trim();
        if(validateName()&&validateEmail()&&validatePassword()&&validatePhone()&&validateSurname()) {
            Map<String, String> params = new HashMap<String, String>();
            params.put(USERNAME, name);
            params.put(SURNAME, surname);
            params.put(NUMBER, phone_number);
            params.put(EMAIL, email);
            params.put(PASSWORD, password);

            jsonObject = new JSONObject(params);
        }else{
            Toast.makeText(getApplicationContext(),"sdfsdfsd",Toast.LENGTH_LONG).show();
        }
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e("adaresponses", "dfasd");
                    try {
                        if (response.has("message")) {
                            Toast.makeText(getApplicationContext(), response.get("message").toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(), "Not correct", Toast.LENGTH_SHORT).show();
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null) {
                        String str = new String(networkResponse.data);
                        try {

                            JSONObject json = new JSONObject(str);
                            Toast.makeText(getApplicationContext(), json.get("error").toString(), Toast.LENGTH_SHORT).show();
                            Log.e("in if", json.get("error").toString());
                        } catch (JSONException exc) {
                            exc.printStackTrace();
                        }
                    }
                }
            });
            ApplicationController.getInstance().addToRequestQueue(req);

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
    private boolean validateName() {
        if (nameEditText.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(nameEditText);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateSurname() {
        if (surnameEditText.getText().toString().trim().isEmpty()) {
            inputLayoutSurname.setError(getString(R.string.err_msg_surname));
            requestFocus(surnameEditText);
            return false;
        } else {
            inputLayoutSurname.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validatePhone() {
        if (phoneNumberEditText.getText().toString().trim().isEmpty()) {
            inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(phoneNumberEditText);
            return false;
        } else {
            inputLayoutPhone.setErrorEnabled(false);
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
    private class MyTextWatcher implements TextWatcher {
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
                case R.id.nameEditText:
                    validateName();
                    break;
                case R.id.surnameEditText:
                    validateSurname();
                    break;
                case R.id.phoneNumberEditText:
                    validatePhone();
                    break;
            }
        }
    }

}

