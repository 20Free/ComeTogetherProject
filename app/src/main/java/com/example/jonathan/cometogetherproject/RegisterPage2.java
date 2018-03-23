package com.example.jonathan.cometogetherproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Arrays;

public class RegisterPage2 extends AppCompatActivity {

    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String country;
    private ArrayList<String> countries;
    private String provinceState;
    private ArrayList<String> provincesStates;
    private String city;
    private ArrayList<String> cities;
    private String language;
    private ArrayList<String> languageShorts;

    private Intent registerPage3Intent;

    /**
     * Page 2 of the registration initialization
     *
     * @param savedInstanceState the state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page_2_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(R.string.title_activity_register);

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = getString(R.string.url) + getString(R.string.register_service_url) + getString(R.string.blank);
        StringRequest blankRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(blankRequest);

        Bundle extras = getIntent().getExtras();
        username = extras.getString(getString(R.string.map_username_key));
        password = extras.getString(getString(R.string.map_password_key));
        email = extras.getString(getString(R.string.map_email_key));
        firstName = extras.getString(getString(R.string.map_first_name_key));
        lastName = extras.getString(getString(R.string.map_last_name_key));
        phoneNumber = extras.getString(getString(R.string.map_phone_number_key));

        Spinner countrySpinner = (Spinner) findViewById(R.id.register_country_spinner);
        countries = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.register_countries)));
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(countryAdapter);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                country = countries.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner provinceStateSpinner = (Spinner) findViewById(R.id.register_province_state_spinner);
        provincesStates = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.register_provinces_states)));
        ArrayAdapter<String> provinceStateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, provincesStates);
        provinceStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceStateSpinner.setAdapter(provinceStateAdapter);
        provinceStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                provinceState = provincesStates.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner citySpinner = (Spinner) findViewById(R.id.register_city_spinner);
        cities = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.register_cities)));
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city = cities.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Spinner languageSpinner = (Spinner) findViewById(R.id.register_language_spinner);
        ArrayList<String> languages = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.register_languages)));
        languageShorts = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.register_language_shorts)));
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                language = languageShorts.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        registerPage3Intent = new Intent(this, RegisterPage3.class);
    }

    /**
     * Grabs the texts in the TextViews and moves them onto the
     * next activity.
     *
     * @param view the fab clicked.
     */
    public void saveAndRegisterPage3(View view) {
        registerPage3Intent.putExtra(getString(R.string.map_username_key), username);
        registerPage3Intent.putExtra(getString(R.string.map_password_key), password);
        registerPage3Intent.putExtra(getString(R.string.map_email_key), email);
        registerPage3Intent.putExtra(getString(R.string.map_first_name_key), firstName);
        registerPage3Intent.putExtra(getString(R.string.map_last_name_key), lastName);
        registerPage3Intent.putExtra(getString(R.string.map_phone_number_key), phoneNumber);
        registerPage3Intent.putExtra(getString(R.string.map_country_key), country);
        registerPage3Intent.putExtra(getString(R.string.map_province_state_key), provinceState);
        registerPage3Intent.putExtra(getString(R.string.map_city_key), city);
        registerPage3Intent.putExtra(getString(R.string.map_language_key), language);
        startActivity(registerPage3Intent);
    }
}