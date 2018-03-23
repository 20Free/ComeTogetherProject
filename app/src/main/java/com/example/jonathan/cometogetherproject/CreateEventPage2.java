package com.example.jonathan.cometogetherproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CreateEventPage2 extends AppCompatActivity {

    private Spinner eventCountrySpinner;
    private Spinner eventProvinceStateSpinner;
    private Spinner eventCitySpinner;
    private List<String> countries;
    private List<String> provincesStates;
    private List<String> cities;
    private String eventTitle;
    private String eventDetails;
    private String eventDescription;
    private String eventType;
    private String eventTicketsLink;
    private String eventCountry;
    private String eventProvinceState;
    private String eventCity;
    private String eventDateTimeString;
    private LinearLayout mProgressView;
    private RelativeLayout mCreateEventFormView;
    private RequestQueue queue;

    private static final String URL = "http://cometogetherr.azurewebsites.net/";
    private static final String SERVICE_URL = URL + "cometogetherapp/event/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_activity_page_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        Bundle extras = getIntent().getExtras();
        Log.i("eventType", extras.getString("eventType"));
        if (extras != null) {
            eventTitle = extras.getString("eventTitle");
            eventDetails = extras.getString("eventDetails");
            eventDescription = extras.getString("eventDescription");
            eventType = extras.getString("eventType");
            eventTicketsLink = extras.getString("eventTicketsLink");
        }

        queue = Volley.newRequestQueue(this);

        eventCountrySpinner = (Spinner) findViewById(R.id.eventCountrySpinner);
        eventProvinceStateSpinner = (Spinner) findViewById(R.id.eventProvinceStateSpinner);
        eventCitySpinner = (Spinner) findViewById(R.id.eventCitySpinner);
        mProgressView = (LinearLayout) findViewById(R.id.create_event_progress);
        mCreateEventFormView = (RelativeLayout) findViewById(R.id.create_event_form_view);

        countries = new ArrayList<>();
        countries.add("Canada");
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventCountrySpinner.setAdapter(countryAdapter);
        eventCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                eventCountry = countries.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        provincesStates = new ArrayList<>();
        provincesStates.add("ON");
        ArrayAdapter<String> provinceStateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provincesStates);
        provinceStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventProvinceStateSpinner.setAdapter(provinceStateAdapter);
        eventProvinceStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                eventProvinceState = provincesStates.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cities = new ArrayList<>();
        cities.add("Ottawa");
        cities.add("Toronto");
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventCitySpinner.setAdapter(cityAdapter);
        eventCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                eventCity = cities.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCreateEventFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCreateEventFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCreateEventFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mCreateEventFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void saveEvent(View view) {
        Calendar eventDate = DatePickerDialogTheme.eventDate;
        Calendar eventTime = TimePickerDialogTheme.eventTime;
        final Calendar eventDateTime = Calendar.getInstance();
        eventDateTime.set(eventDate.get(Calendar.YEAR),
                eventDate.get(Calendar.MONTH),
                eventDate.get(Calendar.DAY_OF_MONTH),
                (eventTime.get(Calendar.HOUR)),
                eventTime.get(Calendar.MINUTE));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SS'Z'", Locale.CANADA);

        eventDateTimeString = format.format(eventDateTime.getTime());

        showProgress(true);

        final Activity activity = this;

        String url = SERVICE_URL;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showProgress(false);
                Log.i("EVENT RESPONSE", response);
                Intent intent = new Intent(activity, WelcomeActivity.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showProgress(false);
                Toast.makeText(activity, "Error", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                Log.i("TITLE", eventTitle);
                Log.i("DETAILS", eventDetails);
                Log.i("DESCRIPTION", eventDescription);
                Log.i("TYPE", eventType);
                Log.i("TICKETS LINK", eventTicketsLink);
                Log.i("DATE TIME", eventDateTimeString);
                Log.i("COUNTRY", eventCountry);
                Log.i("PROVINCE OR STATE", eventProvinceState);
                Log.i("CITY", eventCity);

                params.put("TITLE", eventTitle);
                params.put("DETAILS", eventDetails);
                params.put("DESCRIPTION", eventDescription);
                params.put("TYPE", eventType);
                params.put("TICKETS LINK", eventTicketsLink);
                params.put("DATE TIME", eventDateTimeString);
                params.put("COUNTRY", eventCountry);
                params.put("PROVINCE OR STATE", eventProvinceState);
                params.put("CITY", eventCity);
                return params;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        queue.add(stringRequest);
    }


    public void pickEventDate(View view) {
        DialogFragment dialogFragment = new DatePickerDialogTheme();
        dialogFragment.show(getSupportFragmentManager(), "Date Theme");
    }

    public void pickEventTime(View view) {
        DialogFragment dialogFragment = new TimePickerDialogTheme();
        dialogFragment.show(getSupportFragmentManager(), "Time Theme");
    }

    public static class DatePickerDialogTheme extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public static Calendar eventDate;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);


            return new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog, this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            List<String> months = new ArrayList<>();
            months.add("Jan");
            months.add("Feb");
            months.add("Mar");
            months.add("Apr");
            months.add("May");
            months.add("Jun");
            months.add("Jul");
            months.add("Aug");
            months.add("Sep");
            months.add("Oct");
            months.add("Nov");
            months.add("Dec");

            String eventButtonDate = months.get(month) + ":" + day + ":" + year;
            Button eventDatePick = (Button) getActivity().findViewById(R.id.buttonEventDatePicker);
            eventDatePick.setText(eventButtonDate);

            eventDate = Calendar.getInstance();
            eventDate.set(year, month, day);
        }
    }

    public static class TimePickerDialogTheme extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        public static Calendar eventTime;

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int hours = calendar.get(Calendar.HOUR);
            int minutes = calendar.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog, this, hours, minutes, false);
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int minutes, int seconds) {
            String eventButtonTime = minutes + ":" + seconds;
            Button eventTimePick = (Button) getActivity().findViewById(R.id.buttonEventTimePicker);
            eventTimePick.setText(eventButtonTime);

            eventTime = Calendar.getInstance();
            eventTime.set(0, 0, 0, minutes, seconds);
        }
    }
}