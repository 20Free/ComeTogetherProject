package com.example.jonathan.cometogetherproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A login screen that offers login via username/password.
 */
public class ComeTogetherLogin extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private String username;
    private String password;

    private RequestQueue queue;

    /**
     * Login page initialization
     *
     * @param savedInstanceState the state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.come_together_login_activity);
        setTitle(getString(R.string.title_activity_login));
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress_view);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);

        showProgress(true);
        queue = Volley.newRequestQueue(this);

        String url = getString(R.string.url) + getString(R.string.login_service_url) + getString(R.string.blank);
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgress(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest1.setRetryPolicy(policy);
        queue.add(stringRequest1);
    }

    /**
     * Onclick method for the Sign in button.
     * Starts the login attempt.
     *
     * @param view references button for signing in.
     */
    public void signIn(View view){
        attemptLogin();
    }

    /**
     * Onclick method for the Register button.
     * Opens the first register page.
     *
     * @param view references button for registering.
     */
    public void registerAccount(View view){
        Intent registerIntent = new Intent(this, RegisterPage1.class);
        startActivity(registerIntent);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        username = mUsernameView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check if user entered a username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            String url = getString(R.string.url) + getString(R.string.login_service_url);
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    showProgress(false);
                    hideKeyboard();
                    try {
                        if(!response.equals("")) {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString(getString(R.string.json_username_key)).equals(getString(R.string.unknown_username))) {
                                mUsernameView.setError(getString(R.string.error_invalid_username));
                                mUsernameView.requestFocus();
                            } else if (!jsonObject.getString(getString(R.string.json_password_key)).equals(password)) {
                                mPasswordView.setError(getString(R.string.error_incorrect_password));
                                mPasswordView.requestFocus();
                            } else {
                                JSONArray locations = jsonObject.optJSONArray(getString(R.string.json_locations_key));
                                Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                                intent.putExtra(getString(R.string.map_username_key), jsonObject.getString(getString(R.string.json_username_key)));
                                intent.putExtra(getString(R.string.map_email_key), jsonObject.getString(getString(R.string.json_email_address_key)));
                                intent.putExtra(getString(R.string.map_users_name_key), jsonObject.getString(getString(R.string.json_full_name_key)));
                                intent.putExtra(getString(R.string.map_phone_number_key), jsonObject.getString(getString(R.string.json_phone_number_key)));
                                intent.putExtra(getString(R.string.map_photo_link_key), jsonObject.getString(getString(R.string.json_photo_link_key)));
                                intent.putExtra(getString(R.string.map_country_key), locations.getJSONObject(0).getString(getString(R.string.json_country_key)));
                                intent.putExtra(getString(R.string.map_province_state_key), locations.getJSONObject(0).getString(getString(R.string.json_province_state_key)));
                                intent.putExtra(getString(R.string.map_city_key), locations.getJSONObject(0).getString(getString(R.string.json_city_key)));
                                intent.putExtra(getString(R.string.map_language_key), jsonObject.getString(getString(R.string.json_language_key)));
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No users found, please create an account", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_could_not_login), Toast.LENGTH_LONG).show();
                    showProgress(false);
                    mLoginFormView.setVisibility(View.GONE);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put(getString(R.string.map_username_key), username);
                    params.put(getString(R.string.map_password_key), password);
                    return params;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest2.setRetryPolicy(policy);
            queue.add(stringRequest2);
        }
    }

    /**
     * Checks to see if the password is long enough.
     *
     * @param password the password entered
     * @return true, if the password is long enough
     *         false, if the password is too short
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Toggles the progress UI and login form.
     *
     * @param show if true, shows the progress spinner and hides the login form
     *             if false, hides the progress spinner and shows the login form
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Hides the keyboard
     */
    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) ComeTogetherLogin.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        try {
            inputManager.hideSoftInputFromWindow(
                    ComeTogetherLogin.this.getCurrentFocus()
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {
            Log.e("null keyboard", "Keyboard is null");
        }
    }
}