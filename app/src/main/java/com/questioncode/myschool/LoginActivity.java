package com.questioncode.myschool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "XXXXXXXXX";
    private static Globals globals = Globals.getInstance();

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

    public void loginRequest(final String email, final String password) {

        // TODO test with this URL then use your server URL :

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

        /**
         * Volley
         *
         *  JsonObjectRequest to send and receive JSON object
         *
         *  JsonObjectRequest
         *  Parameters:
         *      method - the HTTP method to use
         *      url - URL to fetch the JSON from
         *      jsonRequest - A JSONObject to post with the request. Null is allowed and indicates no parameters will be posted along with request.
         *      listener - Listener to receive the JSON response
         *      errorListener - Error listener, or null to ignore errors.
         *
         *  JsonArrayRequest to receive JSON Array
         *  Parameters:
         *      url - URL to fetch the JSON from
         *      listener - Listener to receive the JSON response
         *      errorListener - Error listener, or null to ignore errors.
         *
         *  StringRequest class will be used to fetch any kind of string data. The response can be json, xml, html or plain text
         *
         */

        // Display in progress dialog
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.act_login_progress_message));
        pDialog.show();

        final StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                globals.AUTH_URL,
                new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                pDialog.hide();
                CookieStore cs = MySingleton.getInstance(LoginActivity.this).getmHttpClient().getCookieStore();
                for (Cookie cookie : cs.getCookies()) {
                    Log.d(TAG,"Found cookie " +  cookie.toString());
                };


                testProtected(response);
                // TODO start next activityET

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
                pDialog.hide();

                /**
                 *
                 * Display error message to user
                 *

                 * AuthFailureError — If you are trying to do Http Basic authentication then this
                 * error is most likely to come.
                 *
                 * NetworkError — Socket disconnection, server down, DNS issues might result in this
                 * error.
                 *
                 * NoConnectionError — Similar to NetworkError, but fires when device does not have
                 * internet connection, your error handling logic can club NetworkError and
                 * NoConnectionError together and treat them similarly.
                 *
                 * ParseError — While using JsonObjectRequest or JsonArrayRequest if the received
                 * JSON is malformed then this exception will be generated. If you get this error then
                 * it is a problem that should be fixed instead of being handled.
                 *
                 * ServerError — The server responded with an error, most likely with 4xx or 5xx HTTP
                 * status codes.
                 *
                 * TimeoutError — Socket timeout, either server is too busy to handle the request or
                 * there is some network latency issue. By default Volley times out the request after
                 * 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                 *
                 */

                // display "Invalid login" if http status 401
                NetworkResponse networkResponse = error.networkResponse;
                if (error instanceof ServerError ||  error instanceof AuthFailureError) {
                    Toast.makeText(LoginActivity.this, R.string.act_login_message_invalid_email_or_password, Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof NetworkError || error instanceof NoConnectionError) {
                    // display "No Network Connection"
                    Toast.makeText(LoginActivity.this, R.string.act_login_message_network_error, Toast.LENGTH_SHORT).show();
                }
                else if (error instanceof TimeoutError) {
                    Toast.makeText(LoginActivity.this, R.string.act_login_message_timeout_error, Toast.LENGTH_SHORT).show();
                }
                else {
                    // display error message
                    Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }


        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        /**
         * Adding request to request queue
         */

        // This can be used to cancel the request if needed
        String tag_json_obj = "json_obj_req";
        MySingleton.getInstance(this).addToRequestQueue(stringRequest, tag_json_obj);

    }

    private void testProtected(String response) {
        // TODO test with this URL then use your server URL :

            /**
             * Volley
             *
             *  JsonObjectRequest to send and receive JSON object
             *
             *  JsonObjectRequest
             *  Parameters:
             *      method - the HTTP method to use
             *      url - URL to fetch the JSON from
             *      jsonRequest - A JSONObject to post with the request. Null is allowed and indicates no parameters will be posted along with request.
             *      listener - Listener to receive the JSON response
             *      errorListener - Error listener, or null to ignore errors.
             *
             *  JsonArrayRequest to receive JSON Array
             *  Parameters:
             *      url - URL to fetch the JSON from
             *      listener - Listener to receive the JSON response
             *      errorListener - Error listener, or null to ignore errors.
             *
             *  StringRequest class will be used to fetch any kind of string data. The response can be json, xml, html or plain text
             *
             */

            // Display in progress dialog
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage(getResources().getString(R.string.act_login_progress_message));
            pDialog.show();

            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    globals.PROTECTED_URL, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, response.toString());
                    pDialog.hide();
                    // TODO start next activityET

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, error.toString());
                    pDialog.hide();

                    /**
                     *
                     * Display error message to user
                     *

                     * AuthFailureError — If you are trying to do Http Basic authentication then this
                     * error is most likely to come.
                     *
                     * NetworkError — Socket disconnection, server down, DNS issues might result in this
                     * error.
                     *
                     * NoConnectionError — Similar to NetworkError, but fires when device does not have
                     * internet connection, your error handling logic can club NetworkError and
                     * NoConnectionError together and treat them similarly.
                     *
                     * ParseError — While using JsonObjectRequest or JsonArrayRequest if the received
                     * JSON is malformed then this exception will be generated. If you get this error then
                     * it is a problem that should be fixed instead of being handled.
                     *
                     * ServerError — The server responded with an error, most likely with 4xx or 5xx HTTP
                     * status codes.
                     *
                     * TimeoutError — Socket timeout, either server is too busy to handle the request or
                     * there is some network latency issue. By default Volley times out the request after
                     * 2.5 seconds, use a RetryPolicy if you are consistently getting this error.
                     *
                     */

                    // display "Invalid login" if http status 401
                    NetworkResponse networkResponse = error.networkResponse;
                    if (error instanceof ServerError ||  error instanceof AuthFailureError) {
                        Toast.makeText(LoginActivity.this, R.string.act_login_message_authentication_error, Toast.LENGTH_SHORT).show();
                    }
                    else if (error instanceof NetworkError || error instanceof NoConnectionError) {
                        // display "No Network Connection"
                        Toast.makeText(LoginActivity.this, R.string.act_login_message_network_error, Toast.LENGTH_SHORT).show();
                    }
                    else if (error instanceof TimeoutError) {
                        Toast.makeText(LoginActivity.this, R.string.act_login_message_timeout_error, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // display error message
                        Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }


            });

            /**
             * Adding request to request queue
             */

            // This can be used to cancel the request if needed
            String tag_json_obj = "json_obj_req";
            MySingleton.getInstance(this).addToRequestQueue(jsonObjReq, tag_json_obj);

        }

}
