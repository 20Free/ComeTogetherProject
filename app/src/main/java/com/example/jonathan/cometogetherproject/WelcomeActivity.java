package com.example.jonathan.cometogetherproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    private String username;
    private String email;
    private String nameFirstLast;
    private String phoneNumber;
    private String photoUrl;
    private String country;
    private String provinceOrState;
    private String city;
    private String language;
    private ArrayList<HashMap<String, String>> events;
    private ListView eventFeedList;
    private LinearLayout progressView;

    /**
     *
     *
     * @param savedInstanceState state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(getString(R.string.title_welcome_page));

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            username = extras.getString(getString(R.string.map_username_key));
            email = extras.getString(getString(R.string.map_email_key));
            nameFirstLast = extras.getString(getString(R.string.map_users_name_key));
            phoneNumber = extras.getString(getString(R.string.map_phone_number_key));
            photoUrl = extras.getString(getString(R.string.map_photo_link_key));
            country = extras.getString(getString(R.string.map_country_key));
            provinceOrState = extras.getString(getString(R.string.map_province_state_key));
            city = extras.getString(getString(R.string.map_city_key));
            language = extras.getString(getString(R.string.map_language_key));
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        eventFeedList = (ListView)findViewById(R.id.eventFeedList);
        progressView = (LinearLayout) findViewById(R.id.welcomeFeedProgress);
        events = new ArrayList<>();
        showProgress(true);

        String url = getString(R.string.url) + getString(R.string.event_service_url) + getString(R.string.load_events);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgress(false);
                try {
                    JSONArray eventJSONArray = new JSONArray(response);
                    for(int i = 0; i < eventJSONArray.length(); i++) {
                        HashMap<String, String> event = new HashMap<>();
                        event.put(getString(R.string.key_event_name), eventJSONArray.getJSONObject(i).getString(getString(R.string.json_event_title_key)));
                        event.put(getString(R.string.key_event_details), eventJSONArray.getJSONObject(i).getString(getString(R.string.json_event_details_key)));
                        event.put(getString(R.string.key_event_description), eventJSONArray.getJSONObject(i).getString(getString(R.string.json_event_description_key)));
                        event.put(getString(R.string.key_event_type), eventJSONArray.getJSONObject(i).getString(getString(R.string.json_event_type_key)));
                        event.put(getString(R.string.key_event_tickets_link), eventJSONArray.getJSONObject(i).getString(getString(R.string.json_event_tickets_link_key)));
                        String eventDateString = eventJSONArray.getJSONObject(i).getString(getString(R.string.json_event_date_key));
                        String eventTimeString = eventJSONArray.getJSONObject(i).getString(getString(R.string.json_event_time_key));
                        String eventDateTime = eventDateString + getString(R.string.space) + eventTimeString;
                        Date eventDate = new SimpleDateFormat(getString(R.string.date_format), Locale.CANADA).parse(eventDateTime);
                        event.put(getString(R.string.key_event_time), eventDate.toString());
                        event.put(getString(R.string.key_event_pic), eventJSONArray.getJSONObject(i).getString(getString(R.string.json_event_photo_link_key)));
                        JSONArray locationArray = eventJSONArray.getJSONObject(i).getJSONArray(getString(R.string.json_event_locations_key));
                        List<String> locations = new ArrayList<>();
                        for(int j = 0; j < locationArray.length(); j++) {
                            locations.add(locationArray.getJSONObject(j).getString(getString(R.string.json_city_key))
                                    + getString(R.string.comma)
                                    + locationArray.getJSONObject(j).getString(getString(R.string.json_province_state_key))
                                    + getString(R.string.comma)
                                    + locationArray.getJSONObject(j).getString(getString(R.string.json_country_key)));
                        }
                        String locationsString = "";
                        for(int j = 0; j < locations.size(); j++) {
                            locationsString += locations.get(j) + getString(R.string.enter);
                        }
                        event.put(getString(R.string.key_event_location), locationsString);

                        events.add(event);
                    }
                    CustomEventFeedAdapter eventFeedAdapter = new CustomEventFeedAdapter(WelcomeActivity.this, events);
                    eventFeedList.setAdapter(eventFeedAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_could_not_fetch_events), Toast.LENGTH_LONG).show();
                showProgress(false);
            }
        });

        queue.add(stringRequest);


        eventFeedList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectEvent(i);
            }
        });
        setSupportActionBar(toolbar);
    }

    /**
     * Opens the page for creating a new event.
     *
     * @param view the fab in welcome_activity.xml
     */
    public void createEvent(View view){
        Intent createEventIntent = new Intent(this, CreateEventPage1.class);
        startActivity(createEventIntent);
    }

    /**
     * Overrides the back button to do nothing.
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * Attempts to inflate the menu in the toolbar.
     *
     * @param menu welcome_menu.xml
     * @return true, if the menu successfully inflates.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.welcome_menu, menu);
        return true;
    }

    /**
     * Performs the action of the menu item selected.
     *
     * @param item the menu item selected from welcome_menu.xml
     * @return true, if a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.user_profile) {
            Intent profileIntent = new Intent(this, ProfileActivity.class);
            profileIntent.putExtra(getString(R.string.map_username_key), username);
            profileIntent.putExtra(getString(R.string.map_email_key), email);
            profileIntent.putExtra(getString(R.string.map_users_name_key), nameFirstLast);
            profileIntent.putExtra(getString(R.string.map_phone_number_key), phoneNumber);
            profileIntent.putExtra(getString(R.string.map_photo_link_key), photoUrl);
            profileIntent.putExtra(getString(R.string.map_country_key), country);
            profileIntent.putExtra(getString(R.string.map_province_state_key), provinceOrState);
            profileIntent.putExtra(getString(R.string.map_city_key), city);
            profileIntent.putExtra(getString(R.string.map_language_key), language);
            profileIntent.putExtra(getString(R.string.map_friend_key), true);
            profileIntent.putExtra(getString(R.string.map_user_key), true);
            startActivity(profileIntent);
        }

        if (id == R.id.messenger) {
            Intent messengerIntent = new Intent(this, MessengerActivity.class);
            startActivity(messengerIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Opens the event page at the selected position on the ListView.
     *
     * @param position position of the event selected on the ListView.
     */
    public void selectEvent(int position){
        HashMap<String, String> event = events.get(position);
        Intent intent = new Intent(this, ViewEvent.class);
        intent.putExtra(getString(R.string.map_username_key), username);
        intent.putExtra(getString(R.string.key_event_name), event.get(getString(R.string.key_event_name)));
        intent.putExtra(getString(R.string.key_event_details), event.get(getString(R.string.key_event_details)));
        intent.putExtra(getString(R.string.key_event_description), event.get(getString(R.string.key_event_description)));
        intent.putExtra(getString(R.string.key_event_type), event.get(getString(R.string.key_event_type)));
        intent.putExtra(getString(R.string.key_event_tickets_link), event.get(getString(R.string.key_event_tickets_link)));
        intent.putExtra(getString(R.string.key_event_time), event.get(getString(R.string.key_event_time)));
        intent.putExtra(getString(R.string.key_event_pic), event.get(getString(R.string.key_event_pic)));
        intent.putExtra(getString(R.string.key_event_location), event.get(getString(R.string.key_event_location)));
        startActivity(intent);
    }

    /**
     * Toggles the progress UI and the event feed list.
     *
     * @param show if true, shows the progress spinner and hides the event feed list.
     *             if false, hides the progress spinner and shows the event feed list.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            eventFeedList.setVisibility(show ? View.GONE : View.VISIBLE);
            eventFeedList.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    eventFeedList.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            eventFeedList.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}