package pebsecret.mitra3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pebsecret.mitra3.Config.Config;
import pebsecret.mitra3.SSL.HttpsTrustManager;
import pebsecret.mitra3.sessionmanager.SessionManager;


public class RegisterActivity extends AppCompatActivity {
    private SessionManager session;
    EditText etEmail, etUsername, etPassword,etConfirmPassword;
    String email, userName, userPass,confirmPassword;
    TextView registerButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        etEmail = (EditText)findViewById(R.id.reg_email);
        etUsername = (EditText)findViewById(R.id.reg_username);
        etPassword = (EditText)findViewById(R.id.reg_password);
        etConfirmPassword = (EditText)findViewById(R.id.reg_confirmpassword);
        registerButton = (TextView)findViewById(R.id.reg_btnRegister);
        // Session manager
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            // User ites already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this, Welcome.class);
            startActivity(intent);
            finish();
        }

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString();
                userName = etUsername.getText().toString();
                userPass = etPassword.getText().toString();
                confirmPassword = etConfirmPassword.getText().toString();

                if(userPass.equals(confirmPassword)){

                    registerUser(email, userName, userPass, confirmPassword);

                }else{

                    Toast.makeText(getApplicationContext(),"Passwords don't match",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void registerUser(final String email, final String userName,
                              final String password,final String confirmPassword) {
        // Tag used to cancel the request

        HttpsTrustManager.sssMethod();
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Response", "Register Response: " + response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equals("success")) {



                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("id","");
                        editor.putString("email", email);
                        editor.putString("user_name", userName);
                        editor.putString("user_pass", userPass);
                        editor.putString("confirm_pass", confirmPassword);

                        Intent intent = new Intent(
                                RegisterActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    else if (jsonObject.getString("result").equals("fail")) {

                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                    }

                }catch(JSONException e){
                    e.printStackTrace();
                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id","");
                params.put("email", email);
                params.put("user_name", userName);
                params.put("user_pass", password);
                params.put("confirm_pass", confirmPassword);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        return (keyCode == KeyEvent.KEYCODE_BACK ? true : super.onKeyDown(keyCode, event));
    }
}