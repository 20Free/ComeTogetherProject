package com.example.jonathan.cometogetherproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

class CustomEventFeedAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private LayoutInflater inflater;

    /**
     * Event feed list adapter initialization.
     *
     * @param a the activity that the adapter is called from.
     * @param d the data to be stored in the list.
     */
    CustomEventFeedAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * @return the size of the list.
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * @param position position of the current item.
     * @return the position of the current item.
     */
    @Override
    public Object getItem(int position) {
        return position;
    }

    /**
     * @param position position of the current item.
     * @return the position of the current item as the item's ID.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Builds the event feed row.
     *
     * @param position position of the current item.
     * @param convertView the converted view.
     * @param parent the parent view of the list.
     * @return the event feed list view created.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(convertView == null){
            view = inflater.inflate(R.layout.event_feed_row, parent, false);
        }

        ImageView eventImage = (ImageView) view.findViewById(R.id.eventPic);
        TextView eventTitle = (TextView)view.findViewById(R.id.eventTitle);
        TextView eventLocation = (TextView)view.findViewById(R.id.eventLocation);
        TextView eventTime = (TextView)view.findViewById(R.id.eventTime);

        HashMap<String, String> event;
        event = data.get(position);

        eventImage.setImageResource(activity.getResources().getIdentifier(event.get(activity.getString(R.string.key_event_pic)), activity.getString(R.string.drawable), activity.getPackageName()));
        eventTitle.setText(event.get(activity.getResources().getString(R.string.key_event_name)));
        eventLocation.setText(event.get(activity.getResources().getString(R.string.key_event_location)));
        eventTime.setText(event.get(activity.getResources().getString(R.string.key_event_time)));

        return view;
    }
}