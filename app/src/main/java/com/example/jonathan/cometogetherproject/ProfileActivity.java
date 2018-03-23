package com.example.jonathan.cometogetherproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.Metadata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private NetworkImageView profilePhoto;
    private RequestQueue queue;
    private Bitmap photo;
    private String photoUrl;
    private boolean friend;
    private boolean user;
    private LinearLayout mProgressView;
    private String email;
    private String nameFirstLast;
    private String city;
    private String phoneNumber;
    private String country;
    private String provinceOrState;
    private String language;
    private HashMap<String, String> languageMap;
    private PopupWindowCompat popupWindow;
    private String username;
    private String viewerUsername;
    private TextView friendRequestCountView;
    private List<String> friendList;
    private List<String> requestList;
    private String currentUser;
    private JSONArray friendArray;
    private RelativeLayout profileActivityView;
    private String deleteUsername;
    private boolean deleteFriend;
    private ArrayAdapter<String> friendRequestAdapter;
    private ArrayAdapter<String> friendListAdapter;
    private ListView listViewRequests;
    private ListView listViewFriends;
    int menuItemCount;

    private static final String URL = "http://cometogetherr.azurewebsites.net/";
    private static final String SERVICE_FRIEND_URL = URL + "cometogetherapp/friend/";
    private static final String ADD = SERVICE_FRIEND_URL + "add";
    private static final String LOAD_REQUESTS = SERVICE_FRIEND_URL + "requests";
    private static final String ACCEPT = SERVICE_FRIEND_URL + "accept";
    private static final String GET_FRIENDS = SERVICE_FRIEND_URL + "friends";
    private static final String REMOVE_FRIEND = SERVICE_FRIEND_URL + "remove";
    private static final String SERVICE_EVENT_URL = URL + "cometogetherapp/event/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = "";
        viewerUsername = "";
        email = "";
        nameFirstLast = "";
        phoneNumber = "";
        country = "";
        provinceOrState = "";
        city = "";
        language = "";

        languageMap = new HashMap<>();
        languageMap.put("en", "English");
        languageMap.put("fr", "French");
        languageMap.put("sp", "Spanish");

        mProgressView = (LinearLayout) findViewById(R.id.profilePictureProgress);
        profileActivityView = (RelativeLayout) findViewById(R.id.profileActivityView);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            username = extras.getString("USERNAME");
            viewerUsername = extras.getString("VIEWER_USERNAME");
            email = extras.getString("EMAIL");
            nameFirstLast = extras.getString("USER'S NAME");
            phoneNumber = extras.getString("PHONE_NUMBER");
            photoUrl = extras.getString("PHOTO_LINK");
            country = extras.getString("COUNTRY");
            provinceOrState = extras.getString("PROVINCE_STATE");
            city = extras.getString("CITY");
            language = extras.getString("LANGUAGE");
            friend = extras.getBoolean("FRIEND");
            user = extras.getBoolean("USER");
        }

        queue = Volley.newRequestQueue(this);

        language = languageMap.get(language);

        profilePhoto = (NetworkImageView) findViewById(R.id.profilePhoto);
        DbxRequestConfig requestConfig = new DbxRequestConfig("/User Images");
        DbxClientV2 clientV2 = new DbxClientV2(requestConfig, getString(R.string.dropbox_access_key));

        ProfileDownloadPhotoTask downloadPhotoTask = new ProfileDownloadPhotoTask(clientV2, this);
        downloadPhotoTask.execute();

        String location = city + "," + provinceOrState + "," + country;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        TextView userProfileName = (TextView) findViewById(R.id.userProfileName);
        TextView usersname = (TextView) findViewById(R.id.usersName);
        TextView emailTextView = (TextView) findViewById(R.id.emailTextView);
        TextView phoneNumberTextView = (TextView) findViewById(R.id.phoneNumberTextView);
        TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
        TextView languageTextView = (TextView) findViewById(R.id.languageTextView);

        ImageView addFriend = (ImageView) findViewById(R.id.addFriend);

        RelativeLayout friendRequestView = (RelativeLayout) findViewById(R.id.friendRequestView);

        userProfileName.setText(username);
        usersname.setText(nameFirstLast);
        emailTextView.setText(email);
        phoneNumberTextView.setText(phoneNumber);
        locationTextView.setText(location);
        languageTextView.setText(language);

        if (!friend) {
            addFriend.setVisibility(View.VISIBLE);
        }

        setTitle(username + "'s Profile");

        if (user) {
            setTitle("Personal Profile");
            friendRequestView.setVisibility(View.VISIBLE);
            friendRequestCountView = (TextView) findViewById(R.id.friendRequestCountView);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, LOAD_REQUESTS, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray friendRequestArray = new JSONArray(response);
                        int friendRequestCount = friendRequestArray.length();
                        if (friendRequestCount != 0) {
                            String friendRequestCountText = friendRequestCount + "";
                            friendRequestCountView.setText(friendRequestCountText);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Failed to load requests", Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("USERNAME", username);
                    return params;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            queue.add(stringRequest);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void editOrSendEmail(View view) {
        if (user) {
            new AlertDialog.Builder(this)
                    .setTitle("Edit Email")
                    .setMessage("Are you sure you want to edit your email?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
        } else {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email));
            startActivity(Intent.createChooser(intent, "Choose an Email Client"));
        }
    }

    public void editOrCallPhoneNumber(View view) {
        if (user) {
            new AlertDialog.Builder(this)
                    .setTitle("Edit Phone Number")
                    .setMessage("Are you sure you want to edit your email?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
        } else {
            final List<Intent> textIntents = new ArrayList<>();
            final Intent textIntent = new Intent(Intent.ACTION_VIEW);
            textIntent.setType("text/plain");
            textIntent.setData(Uri.parse("sms:" + phoneNumber));
            final PackageManager packageManager = getPackageManager();
            final List<ResolveInfo> list = packageManager.queryIntentActivities(textIntent, 0);
            for (ResolveInfo res : list) {
                final Intent intent = new Intent(textIntent);
                intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                textIntents.add(intent);
            }

            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            Intent chooserIntent = Intent.createChooser(callIntent, "Call or Text Number");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, textIntents.toArray(new Parcelable[]{}));
            startActivity(chooserIntent);
        }
    }

    /**
     * Allows you to view the other location in google maps.
     *
     * @param view clickable textview in profile_activity.xml
     */
    public void viewEditOrAddLocation(View view) {
        if (user) {
            new AlertDialog.Builder(this)
                    .setTitle("Edit or Add Location")
                    .setMessage("Choose whether to edit or add a location")
                    .setPositiveButton("Edit Location", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setNegativeButton("Add Location", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
        } else {
            int zoom = 0;
            String label = "";
            if (city.equals("Ottawa")) {
                zoom = 10;
                label = "Ottawa, ON";
            } else if (city.equals("Toronto")) {
                zoom = 10;
                label = "Toronto, ON";
            }
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("ZOOM", zoom);
            intent.putExtra("LABEL", label);
            startActivity(intent);
        }
    }

    /**
     * Opens up the friend list of the user and allows them
     * different options depending on if it's their personal
     * profile or not.
     *
     * @param view clickable textview in profile_activity.xml
     */
    public void viewFriendList(View view) {
        PopupWindowCompat popup = popupWindowViewFriends();
        popup.showAtLocation(findViewById(R.id.listOfFriendsView),Gravity.FILL_HORIZONTAL, 0, 0);
    }

    /**
     * not in use
     * @param view
     */
    public void seeEventList(View view) {
        //PopupMenu popUp = popupWindowViewEventsGoing(view);
        //popUp.show();
    }

    /**
     * Open the friend requests in a popUp window
     *
     * @param view clickable textview to view friend requests in profile_activity.xml
     */
    public void viewFriendRequests(View view) {
        PopupWindowCompat requestPopupWindow = popupWindowViewRequests();
        requestPopupWindow.showAsDropDown(view);
    }

    /**
     * Shows a popUp window of friend requests from other users
     * Allows a request to be accepted, thus "adding a friend"
     *
     * @return the popUp window, which contains a list of friend requests
     */
    public PopupWindowCompat popupWindowViewRequests() {


        // initialize a pop up window type
        popupWindow = new PopupWindowCompat(this);

        requestList = new ArrayList<>();
        // the drop down list is a list view
        listViewRequests = new ListView(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOAD_REQUESTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("RESPONSE", response);
                try {
                    friendArray = new JSONArray(response);
                    for(int i = 0; i < friendArray.length(); i++) {
                        requestList.add(friendArray.getJSONObject(i).getString("username"));
                    }
                    friendRequestAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, requestList);
                    listViewRequests.setAdapter(friendRequestAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("USERNAME", username);
                return params;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);


        // set on item selected
        listViewRequests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentUser = requestList.get(i);

                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Accept Request")
                        .setMessage("Would you like to be friends with" + currentUser + "?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                StringRequest stringRequest1 = new StringRequest(Request.Method.POST, ACCEPT, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Toast.makeText(getApplicationContext(), "user added successfully", Toast.LENGTH_LONG).show();
                                        dialogInterface.dismiss();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(getApplicationContext(), "Failed to add user", Toast.LENGTH_LONG).show();
                                    }
                                }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        HashMap<String, String> params = new HashMap<>();
                                        params.put("USERNAME", currentUser);
                                        params.put("REQUEST_USERNAME", username);
                                        return params;
                                    }
                                };
                                RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                stringRequest1.setRetryPolicy(policy);
                                queue.add(stringRequest1);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
                }
            });



        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(250);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the listview as popup content
        popupWindow.setContentView(listViewRequests);

        return popupWindow;
    }

    /**
     * Show the friends list of the user, and if the user is
     * viewing their own profile, allow theme to remove friends on their friend list
     *
     * @return the popUp window, which contains a friend list
     */
    private PopupWindowCompat popupWindowViewFriends() {

        // initialize a pop up window type
        popupWindow = new PopupWindowCompat(this);

        friendList = new ArrayList<>();

        // the drop down list is a list view
        listViewFriends = new ListView(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_FRIENDS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    friendArray = new JSONArray(response);
                    for (int i = 0; i < friendArray.length(); i++) {
                        friendList.add(friendArray.getJSONObject(i).getString("username"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                friendListAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line,
                        friendList);
                // set our adapter and pass our pop up window contents
                listViewFriends.setAdapter(friendListAdapter);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed to load friends", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("USERNAME", username);
                return params;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);

        // set on item selected
        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int pos = i;
                if (user) {
                    new AlertDialog.Builder(ProfileActivity.this)
                            .setTitle("View or Remove Friend")
                            .setMessage("Would you like to view or remove this friend?")
                            .setPositiveButton("View", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    friend = true;
                                    Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);

                                    try {
                                        intent.putExtra("USERNAME", friendArray.getJSONObject(pos).getString("username"));
                                        intent.putExtra("VIEWER_USERNAME", username);
                                        intent.putExtra("EMAIL", friendArray.getJSONObject(pos).getString("emailAddress"));
                                        intent.putExtra("USER'S NAME", friendArray.getJSONObject(pos).getString("fullName"));
                                        intent.putExtra("PHONE_NUMBER", friendArray.getJSONObject(pos).getString("phoneNumber"));
                                        intent.putExtra("PHOTO_LINK", friendArray.getJSONObject(pos).getString("photoLink"));
                                        JSONArray locationArray = friendArray.getJSONObject(pos).optJSONArray("locations");
                                        intent.putExtra("COUNTRY", locationArray.getJSONObject(0).getString("country"));
                                        intent.putExtra("PROVINCE/STATE", locationArray.getJSONObject(0).getString("provinceOrState"));
                                        intent.putExtra("CITY", locationArray.getJSONObject(0).getString("city"));
                                        intent.putExtra("LANGUAGE", friendArray.getJSONObject(pos).getString("language"));
                                        intent.putExtra("FRIEND", friend);
                                        intent.putExtra("USER", false);
                                        startActivity(intent);
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            })
                            .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteUsername = friendList.get(i);
                                    new AlertDialog.Builder(ProfileActivity.this)
                                            .setTitle("Remove Friend")
                                            .setMessage("Are you sure you want to remove this user from your friends list?")
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(final DialogInterface dialogInterface, int i) {
                                                    StringRequest stringRequest1 = new StringRequest(Request.Method.POST, REMOVE_FRIEND, new Response.Listener<String>() {
                                                        @Override
                                                        public void onResponse(String response) {
                                                            Toast.makeText(getApplicationContext(), "Friend successfully removed!", Toast.LENGTH_LONG).show();
                                                            dialogInterface.dismiss();
                                                        }
                                                    }, new Response.ErrorListener() {
                                                        @Override
                                                        public void onErrorResponse(VolleyError error) {
                                                            Toast.makeText(getApplicationContext(), "Remove user failed", Toast.LENGTH_LONG).show();
                                                        }
                                                    }) {
                                                        @Override
                                                        protected Map<String, String> getParams() throws AuthFailureError {
                                                            HashMap<String, String> params = new HashMap<>();
                                                            params.put("USERNAME", username);
                                                            params.put("FRIEND_USERNAME", deleteUsername);
                                                            return params;
                                                        }
                                                    };
                                                    RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                                    stringRequest1.setRetryPolicy(policy);
                                                    queue.add(stringRequest1);
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    deleteFriend = false;
                                                    dialogInterface.cancel();
                                                }
                                            })
                                            .show();

                                }
                            })
                            .show();
                } else {
                    friend = true;
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    try {
                        intent.putExtra("USERNAME", friendArray.getJSONObject(pos).getString("username"));
                        intent.putExtra("VIEWER_USERNAME", username);
                        intent.putExtra("EMAIL", friendArray.getJSONObject(pos).getString("emailAddress"));
                        intent.putExtra("USER'S NAME", friendArray.getJSONObject(pos).getString("fullName"));
                        intent.putExtra("PHONE_NUMBER", friendArray.getJSONObject(pos).getString("phoneNumber"));
                        intent.putExtra("PHOTO_LINK", friendArray.getJSONObject(pos).getString("photoLink"));
                        JSONArray locationArray = friendArray.getJSONObject(pos).optJSONArray("locations");
                        intent.putExtra("COUNTRY", locationArray.getJSONObject(0).getString("country"));
                        intent.putExtra("PROVINCE/STATE", locationArray.getJSONObject(0).getString("provinceOrState"));
                        intent.putExtra("CITY", locationArray.getJSONObject(0).getString("city"));
                        intent.putExtra("LANGUAGE", friendArray.getJSONObject(pos).getString("language"));
                        intent.putExtra("FRIEND", friend);
                        intent.putExtra("USER", false);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        });

        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        // popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.white));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the listview as popup content
        popupWindow.setContentView(listViewFriends);
        return popupWindow;
    }

    /*
    private PopupWindow popupWindowViewEventsGoing() {

        // initialize a pop up window type
        popupWindow = new PopupWindow(this);

        friendList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOAD_REQUESTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    friendArray = new JSONArray(response);
                    for(int i = 0; i < friendArray.length(); i++) {
                        friendList.add(friendArray.getJSONObject(i).getString("username"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("USERNAME", username);
                return params;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                friendList);
        // the drop down list is a list view
        ListView listViewFriends = new ListView(this);

        // set our adapter and pass our pop up window contents
        listViewFriends.setAdapter(adapter);

        // set on item selected
        listViewFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                friend = true;
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                try {
                    intent.putExtra("USERNAME", friendArray.getJSONObject(i).getString("username"));
                    intent.putExtra("VIEWER_USERNAME", username);
                    intent.putExtra("EMAIL", friendArray.getJSONObject(i).getString("emailAddress"));
                    intent.putExtra("USER'S NAME", friendArray.getJSONObject(i).getString("fullName"));
                    intent.putExtra("PHONE_NUMBER", friendArray.getJSONObject(i).getString("phoneNumber"));
                    intent.putExtra("PHOTO_LINK", friendArray.getJSONObject(i).getString("photoLink"));
                    JSONArray locationArray = friendArray.getJSONObject(i).optJSONArray("locations");
                    intent.putExtra("COUNTRY", locationArray.getJSONObject(0).getString("country"));
                    intent.putExtra("PROVINCE/STATE", locationArray.getJSONObject(0).getString("provinceOrState"));
                    intent.putExtra("CITY", locationArray.getJSONObject(0).getString("city"));
                    intent.putExtra("LANGUAGE", friendArray.getJSONObject(i).getString("language"));
                    intent.putExtra("FRIEND", friend);
                    intent.putExtra("USER", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(250);
        // popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.white));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the listview as popup content
        popupWindow.setContentView(listViewFriends);

        return popupWindow;
    }*/

    /**
     * Sends a friend request to the user of the current profile
     *
     * @param view the "friend request" button in profile_activity.xml
     */
    public void sendFriendRequest(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Send Friend Request")
                .setMessage("Are you sure you would like to add " + username + " as a friend?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                friend = true;
                                Toast.makeText(getApplicationContext(), "Request Sent!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                intent.putExtra("USERNAME", username);
                                intent.putExtra("VIEWER_USERNAME", viewerUsername);
                                intent.putExtra("EMAIL", email);
                                intent.putExtra("USER'S NAME", nameFirstLast);
                                intent.putExtra("PHONE_NUMBER", phoneNumber);
                                intent.putExtra("PHOTO_LINK", photoUrl);
                                intent.putExtra("COUNTRY", country);
                                intent.putExtra("PROVINCE/STATE", provinceOrState);
                                intent.putExtra("CITY", city);
                                intent.putExtra("LANGUAGE", language);
                                intent.putExtra("FRIEND", friend);
                                intent.putExtra("USER", false);
                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Sending Request Failed!", Toast.LENGTH_LONG).show();
                            }
                        }) {
                            //send friend request to current profile
                            @Override
                            public HashMap<String, String> getParams() {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("USERNAME", viewerUsername);
                                params.put("REQUEST_USERNAME", username);
                                return params;
                            }
                        };
                        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                        stringRequest.setRetryPolicy(policy);
                        queue.add(stringRequest);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();

    }

    /**
     * Downloads profile photo from dropbox and upon success, loads the image as the profile picture
     */
    public class ProfileDownloadPhotoTask extends AsyncTask<Object, Object[], Object> {

        private DbxClientV2 dbxClient;
        private Context context;

        ProfileDownloadPhotoTask(DbxClientV2 dbxClient, Context context) {
            this.dbxClient = dbxClient;
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            showProgress(true);
            // Upload to Dropbox
            Metadata metadata;
            Bitmap bitmap = null;
            byte[] data;
            JSONObject jsonObject;
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                metadata = dbxClient.files().downloadBuilder(photoUrl.replace("\\", "")).download(outputStream);
                jsonObject = new JSONObject(metadata.toString());
                outputStream.write(jsonObject.getInt("size"));
                outputStream.close();
                data = outputStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(data, 0, jsonObject.getInt("size"));
                Log.i("PATH", metadata.toString());
            } catch (Exception e) {
                showProgress(false);
                Toast.makeText(getApplicationContext(), "Image could not be downloaded", Toast.LENGTH_LONG).show();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            showProgress(false);
            Toast.makeText(context, "Image downloaded successfully", Toast.LENGTH_SHORT).show();
            photo = (Bitmap) o;
            Log.i("PHOTO BYTE COUNT", photo.getByteCount() + "");
            profilePhoto.setBackground(new BitmapDrawable(getResources(), photo));
        }
    }

    /**
     * Shows the progress UI and hides the profile photo.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            profilePhoto.setVisibility(show ? View.GONE : View.VISIBLE);
            profilePhoto.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    profilePhoto.setVisibility(show ? View.GONE : View.VISIBLE);
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
            profilePhoto.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}