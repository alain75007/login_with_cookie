package com.questioncode.myschool;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.act_login_submit_btn).setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.act_login_submit_btn) {
            // Verify email and password
            String email = ((EditText) findViewById(R.id.act_login_email)).getText().toString().trim();
            String password = ((EditText) findViewById(R.id.act_login_password)).getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, getResources().getString(R.string.act_login_message_email_empty), Toast.LENGTH_LONG).show();
            }
            else if (!email.contains("@")) {
                // TODO Better email verification here: http://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
                Toast.makeText(this, getResources().getString(R.string.act_login_message_email_not_good), Toast.LENGTH_LONG).show();
            }
            else if (password.isEmpty()) {
                Toast.makeText(this, getResources().getString(R.string.act_login_message_password_empty), Toast.LENGTH_LONG).show();
            }
            else {
                loginRequest(email, password);
            }
        }
    }

    public void loginRequest(String email, String password) {

        // TODO test with this URL then use your server URL :
        String url = "http://api.androidhive.info/volley/person_object.json";

        // Prepare JSON
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.act_login_json_exception_message, Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO Send request and handle response

    }
}
