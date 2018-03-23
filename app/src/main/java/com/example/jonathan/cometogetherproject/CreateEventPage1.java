package com.example.jonathan.cometogetherproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateEventPage1 extends AppCompatActivity {

    private EditText eventTitleText;
    private EditText eventDetailsText;
    private EditText eventDescriptionText;
    private EditText eventTypeText;
    private EditText eventTicketsLinkText;
    private String eventType;
    private List<String> eventTypes;

    /**
     * Event create page initialization.
     *
     * @param savedInstanceState state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event_activity_page_1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        eventTitleText = (EditText) findViewById(R.id.eventTitleText);
        eventDetailsText = (EditText) findViewById(R.id.eventDetailsText);
        eventDescriptionText = (EditText) findViewById(R.id.eventDescriptionText);
        Spinner eventTypeChooser = (Spinner) findViewById(R.id.eventTypeChooser);
        eventTypeText = (EditText) findViewById(R.id.eventTypeText);
        eventTicketsLinkText = (EditText) findViewById(R.id.eventTicketsLinkText);

        eventTypes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.event_types)));
        ArrayAdapter<String> eventTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeChooser.setAdapter(eventTypeAdapter);
        eventTypeChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                eventType = eventTypes.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Saves event information and moves to the next event creation page.
     *
     * @param view fab that contains the onClick method.
     */
    public void nextEventPage(View view){
        String eventTitle = eventTitleText.getText().toString();
        String eventDetails = eventDetailsText.getText().toString();
        String eventDescription = eventDescriptionText.getText().toString();

        if(!eventTypeText.getText().toString().equals("")){
            eventType = eventTypeText.getText().toString();
        }

        String eventTicketsLink = eventTicketsLinkText.getText().toString();

        Intent intent = new Intent(this, CreateEventPage2.class);
        intent.putExtra(getString(R.string.map_event_title_key), eventTitle);
        intent.putExtra(getString(R.string.map_event_details_key), eventDetails);
        intent.putExtra(getString(R.string.map_event_description_key), eventDescription);
        intent.putExtra(getString(R.string.map_event_type_key), eventType);
        intent.putExtra(getString(R.string.map_event_tickets_link_key), eventTicketsLink);
        startActivity(intent);
    }

}
