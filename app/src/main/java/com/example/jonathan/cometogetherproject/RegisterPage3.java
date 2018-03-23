package com.example.jonathan.cometogetherproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterPage3 extends AppCompatActivity {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String country;
    private String provinceState;
    private String city;
    private String language;
    private String photoLink;
    private RequestQueue queue;
    private LinearLayout mProgressView;
    private RelativeLayout mRegisterFormView;
    private LinearLayout mUploadProgressView;
    private ImageView photoView;

    private Uri outputFileUri;

    private static final int RESULT_CODE = 1;

    /**
     * Page 3 of the registration initialization
     *
     * @param savedInstanceState the state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page_3_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.title_activity_register));
        
        queue = Volley.newRequestQueue(this);

        mProgressView = (LinearLayout) findViewById(R.id.registerUserProgress);
        mRegisterFormView = (RelativeLayout) findViewById(R.id.register_form_layout);

        Bundle extras = getIntent().getExtras();
        username = extras.getString(getString(R.string.map_username_key));
        password = extras.getString(getString(R.string.map_password_key));
        email = extras.getString(getString(R.string.map_email_key));
        firstName = extras.getString(getString(R.string.map_first_name_key));
        lastName = extras.getString(getString(R.string.map_last_name_key));
        phoneNumber = extras.getString(getString(R.string.map_phone_number_key));
        country = extras.getString(getString(R.string.map_country_key));
        provinceState = extras.getString(getString(R.string.map_province_state_key));
        city = extras.getString(getString(R.string.map_city_key));
        language = extras.getString(getString(R.string.map_language_key));

        mUploadProgressView = (LinearLayout) findViewById(R.id.imageUploadProgressView);
        photoView = (ImageView) findViewById(R.id.user_image_view);
    }

    /**
     * Opens the chooser for a photo to upload to dropbox.
     *
     * @param view button to upload photo
     */
    public void uploadPhoto(View view) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        File outputFile = null;

        try {
            outputFile = File.createTempFile("user", ".jpg", getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputFileUri = Uri.fromFile(outputFile);

        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            cameraIntents.add(intent);
        }

        Intent chooserIntent = Intent.createChooser(galleryIntent, "Upload Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, RESULT_CODE);
    }

    /**
     * The result of the user choosing the photo.
     * Initiates the upload of the photo chosen to Dropbox
     *
     * @param requestCode the request code
     * @param resultCode the result code
     * @param data the data of the image chosen
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        File file;

        if (resultCode != RESULT_OK || data == null) return;
        // Check which request we're responding to
        if (requestCode == RESULT_CODE) {
            //Image URI received
            Bitmap bmp;
            if (data.hasExtra("data")) {
                Bundle extras = data.getExtras();
                bmp = (Bitmap) extras.get("data");
            } else {
                AssetFileDescriptor fd = null;
                try {
                    fd = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
                } catch (FileNotFoundException pE) {
                    pE.printStackTrace();
                }
                bmp = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor());
            }
            try {
                FileOutputStream out = new FileOutputStream(new File(outputFileUri.getPath()));
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            file = new File(outputFileUri.getPath());

            photoView.setImageURI(outputFileUri);

            showProgressImageUpload(true);
            DbxRequestConfig requestConfig = new DbxRequestConfig("/User Images");
            DbxClientV2 dbxClientV2 = new DbxClientV2(requestConfig, getString(R.string.dropbox_access_key));
            RegisterUploadPhotoTask registerUploadPhotoTask = new RegisterUploadPhotoTask(dbxClientV2,file, getApplicationContext());
            registerUploadPhotoTask.execute();
        }
    }

    /**
     *  Task created to upload the photo to Dropbox
     */
    public class RegisterUploadPhotoTask extends AsyncTask<Object, Object[], Object> {

        private DbxClientV2 dbxClient;
        private File file;
        private Context context;
        private String path;

        RegisterUploadPhotoTask(DbxClientV2 dbxClient, File file, Context context) {
            this.dbxClient = dbxClient;
            this.file = file;
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                // Upload to Dropbox
                InputStream inputStream = new FileInputStream(file);
                path = dbxClient.files().uploadBuilder("/User Images/" + file.getName()) //Path in the user's Dropbox to save the file.
                        .withMode(WriteMode.OVERWRITE) //always overwrite existing file
                        .uploadAndFinish(inputStream).getPathDisplay();
                Log.d("Upload Status", "Success");

                return path;

            } catch (Exception e) {
                Toast.makeText(context, "Image upload failed", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            showProgressImageUpload(false);
            Toast.makeText(context, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
            photoLink = (String) o;
        }
    }

    /**
     * Sends all of the information from the registration and handles the response from the server
     *
     * @param view the fab clicked to finish registration
     */
    public void registerUser(View view){
        String url = getString(R.string.url) + getString(R.string.register_service_url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    showProgressRegister(false);
                    JSONObject registerJSONObject = new JSONObject(response);
                    String username = registerJSONObject.getString(getString(R.string.json_username_key));
                    if(username.equals(getString(R.string.unknown_username))){
                        Intent intent = new Intent(getApplication(), RegisterPage1.class);
                        intent.putExtra(getString(R.string.map_username_key), getString(R.string.username_equal));
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplication(), ComeTogetherLogin.class);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), getString(R.string.registration_error), Toast.LENGTH_LONG).show();
                showProgressRegister(false);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(getString(R.string.map_username_key), username);
                params.put(getString(R.string.map_password_key), password);
                params.put(getString(R.string.map_email_key), email);
                params.put(getString(R.string.map_first_name_key), firstName);
                params.put(getString(R.string.map_last_name_key), lastName);
                params.put(getString(R.string.map_phone_number_key), phoneNumber);
                params.put(getString(R.string.map_country_key), country);
                params.put(getString(R.string.map_province_state_key), provinceState);
                params.put(getString(R.string.map_city_key), city);
                params.put(getString(R.string.map_language_key), language);
                params.put(getString(R.string.map_photo_link_key), photoLink);
                return params;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        preQueue();
        queue.add(stringRequest);
    }

    /**
     * Hides the keyboard and shows the progress spinner
     */
    protected void preQueue() {
        hideKeyboard();
        showProgressRegister(true);
    }

    /**
     * Hides the keyboard
     */
    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) RegisterPage3.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        try {
            inputManager.hideSoftInputFromWindow(
                    RegisterPage3.this.getCurrentFocus()
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException e) {
            Log.e("null keyboard", "Keyboard is null");
        }
    }

    /**
     * Toggles the progress UI and image form.
     *
     * @param show boolean variable
     *             if true, shows the progress spinner and hides the ImageView,
     *             if false, hides the progress spinner and shows the ImageView
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgressImageUpload(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            photoView.setVisibility(show ? View.GONE : View.VISIBLE);
            photoView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    photoView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mUploadProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mUploadProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mUploadProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mUploadProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            photoView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Toggles the progress UI and the registration form.
     *
     * @param show boolean variable
     *             if true, shows the progress spinner and hides the register form,
     *             if false, hides the progress spinner and shows the register form
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgressRegister(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}