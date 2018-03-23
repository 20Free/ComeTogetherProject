package com.example.jonathan.cometogetherproject;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.github.clans.fab.FloatingActionMenu;

import java.util.HashMap;
import java.util.Map;

public class ViewEvent extends AppCompatActivity{

    private String username;
    private String eventTitle;
    private String eventDetails;
    private String eventDescription;
    private String eventType;
    private String eventTicketsLink;
    private String eventDateTime;
    private String eventPhotoLink;
    private String eventLocation;
    private RequestQueue queue;
    private FloatingActionMenu eventFloatingActionMenu;
    private static String URL = "http://cometogetherr.azurewebsites.net/cometogetherapp/event/";
    private static String GOING_URL = URL + "going";

    private static String MAYBE_URL = URL + "maybe";

    private static String WANT_TO_GO_URL = URL + "wanttogo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        queue = Volley.newRequestQueue(this);

        eventFloatingActionMenu = (FloatingActionMenu) findViewById(R.id.eventFloatingActionMenu);
        Bundle extras = getIntent().getExtras();

        if(extras != null) {
            username = extras.getString("USERNAME");
            eventTitle = extras.getString(getString(R.string.key_event_name));
            eventDateTime = extras.getString(getString(R.string.key_event_time));
            eventLocation = extras.getString(getString(R.string.key_event_location));
            eventDetails = extras.getString(getString(R.string.key_event_details));
            eventDescription = extras.getString(getString(R.string.key_event_description));
            eventType = extras.getString(getString(R.string.key_event_type));
            eventTicketsLink = extras.getString(getString(R.string.key_event_tickets_link));
            eventPhotoLink = extras.getString(getString(R.string.key_event_pic));

            TextView eventTitleView = (TextView) findViewById(R.id.eventTitleTextView);
            eventTitleView.setText(eventTitle);
            TextView eventDetailsView = (TextView) findViewById(R.id.eventDetailsTextView);
            eventDetailsView.setText(eventDetails);
            TextView eventDescriptionView = (TextView) findViewById(R.id.eventDescriptionTextView);
            eventDescriptionView.setText(eventDescription);
            TextView eventTypeView = (TextView) findViewById(R.id.eventTypeTextView);
            eventTypeView.setText(eventType);
            TextView eventTicketsLinkView = (TextView) findViewById(R.id.eventTicketsLinkTextView);
            eventTicketsLinkView.setText(eventTicketsLink);
            TextView eventDateTimeView = (TextView) findViewById(R.id.eventDateTimeTextView);
            eventDateTimeView.setText(eventDateTime);
            ImageView eventPicView = (ImageView) findViewById(R.id.eventPicView);
            eventPicView.setImageResource(getResources().getIdentifier(eventPhotoLink, "drawable", getPackageName()));
            TextView eventLocationView = (TextView) findViewById(R.id.eventLocationTextView);
            eventLocationView.setText(eventLocation);
        }

        setTitle(eventTitle + " Event Feed");
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

    public void viewEventStatus(View view) {
        FragmentManager manager = getSupportFragmentManager();
        GoingMaybeWantToGoTabFragment fragment = new GoingMaybeWantToGoTabFragment();

        Bundle args = new Bundle();
        args.putString("title", "Event Status");
        args.putString("EVENT TITLE", eventTitle);
        args.putString("USERNAME", username);
        fragment.setArguments(args);
        fragment.show(manager, "ok");


    }


    public void goingToEvent(View view) {
        eventFloatingActionMenu.close(true);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GOING_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("RESPONSE", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Could not mark as going", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("USERNAME", username);
                params.put("EVENT TITLE", eventTitle);
                return params;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }

    public void maybeGoingToEvent(View view) {
        eventFloatingActionMenu.close(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MAYBE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("RESPONSE", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Could not mark as maybe", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("USERNAME", username);
                params.put("EVENT TITLE", eventTitle);
                return params;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }

    public void wantToGoToEvent(View view) {
        eventFloatingActionMenu.close(true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, WANT_TO_GO_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("RESPONSE", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Could not mark as Want to go", Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("USERNAME", username);
                params.put("EVENT TITLE", eventTitle);
                return params;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }


}
