package pebsecret.mitra3;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import static java.text.NumberFormat.getInstance;


public class LoginActivity extends AppCompatActivity {
    EditText etName,etPass;
    String login_name;
    String login_pass;
    private SessionManager session;
    TextView forgotPassBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        etName = (EditText)findViewById(R.id.user_name);
        etPass = (EditText)findViewById(R.id.user_pass);

        login_name = etName.getText().toString();
        login_pass = etName.getText().toString();

        forgotPassBtn = (TextView) findViewById(R.id.forgotPass);

        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, Welcome.class);
            startActivity(intent);
            finish();
        }

        forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(i);
            }
        });

    }


    public void userLogin(View view){
        login_name = etName.getText().toString();
        login_pass = etPass.getText().toString();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("id","");
        editor.putString("user_name",login_name);
        editor.putString("user_pass",login_pass);
        editor.commit();

        loginUser(login_name, login_pass);
    }
    private void loginUser(final String userName,
                           final String password) {
        // Tag used to cancel the request
        HttpsTrustManager.sssMethod();
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("Response", "Register Response: " + response.toString());

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("result").equals("success")) {
                        session.setLogin(true);
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(LoginActivity.this,
                                Welcome.class);
                        startActivity(intent);
                        finish();
                    }

                    else if (jsonObject.getString("result").equals("fail")) {

                        Toast.makeText(getApplicationContext(),jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                    }

                }catch(JSONException e){
                    e.printStackTrace();
                }
                // Launch login activity


                //Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

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
                //params.put("id","");
                params.put("user_name", userName);
                params.put("user_pass", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
