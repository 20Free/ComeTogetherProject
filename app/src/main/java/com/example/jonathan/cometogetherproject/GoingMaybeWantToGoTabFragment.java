package com.example.jonathan.cometogetherproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jonathan on 12/2/2016.
 */

public class GoingMaybeWantToGoTabFragment extends DialogFragment {


    private FragmentTabHost mTabHost;
    private ViewPager viewPager;
    private VotersPagerAdapter adapter;
    private static String URL = "http://cometogetherr.azurewebsites.net/cometogetherapp/event/";
    private static String GOING_GET_URL = URL + "get_going";
    private static String MAYBE_GET_URL = URL + "get_maybe";
    private static String WANT_TO_GO_GET_URL = URL + "get_want_to_go";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.going_maybe_want_to_go_fragment, container);

        getDialog().setTitle(getArguments().getString("title"));
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);

        mTabHost = (FragmentTabHost) view.findViewById(R.id.tabs);
        Button positiveButton = (Button) view.findViewById(R.id.positive_button);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        mTabHost.setup(getActivity(), getChildFragmentManager());
        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("Going"), Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("Maybe"), Fragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator("WantToGo"), Fragment.class, null);


        adapter = new VotersPagerAdapter(getChildFragmentManager(), getArguments());
        adapter.setTitles(new String[]{"Going", "Maybe", "Want To Go"});

        viewPager = (ViewPager)view.findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                mTabHost.setCurrentTab(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String s) {
                int i = mTabHost.getCurrentTab();
                viewPager.setCurrentItem(i);
            }
        });

        return view;
    }




    public class VotersPagerAdapter extends FragmentPagerAdapter {

        Bundle bundle;
        String [] titles;

        public VotersPagerAdapter(FragmentManager fm, Bundle bundle) {
            super(fm);
            this.bundle = bundle;
        }

        @Override
        public Fragment getItem(int num) {
            Fragment fragment = new VotersListFragment();
            Bundle args = new Bundle();
            args.putString("EVENT TITLE", bundle.getString("EVENT TITLE"));
            args.putString("USERNAME", bundle.getString("USERNAME"));
            switch (num) {
                case 0:
                    args.putString("list", "goingList");
                    break;
                case 1:
                    args.putString("list", "maybeList");
                    break;
                case 2:
                    args.putString("list", "wantToGoList");
                    break;
            }

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        public void setTitles(String[] titles) {
            this.titles = titles;
        }
    }

    public static class VotersListFragment extends ListFragment {

        private ArrayList<String> goingThings;
        private ArrayList<String> maybeThings;
        private ArrayList<String> wantToGoThings;
        private String eventTitle;
        private RequestQueue queue;
        private JSONObject userJSONObject;
        private String username;
        private String statusUsername;
        private boolean friend;
        private Intent profileIntent;

        private static final String SERVICE_ACCOUNT_URL = "http://cometogetherr.azurewebsites.net/cometogetherapp/account/";
        private static final String SERVICE_FRIEND_URL = "http://cometogetherr.azurewebsites.net/cometogetherapp/friend/";
        private static final String GET_FRIEND_REQUESTS = SERVICE_FRIEND_URL + "requests";
        private static final String GET_FRIENDS = SERVICE_FRIEND_URL + "friends";
        private static final String GET_USER = SERVICE_ACCOUNT_URL + "get_user";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            goingThings = new ArrayList<>();
            maybeThings = new ArrayList<>();
            wantToGoThings = new ArrayList<>();
            userJSONObject = null;
            return inflater.inflate(R.layout.going_tab_row_view, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {

            super.onActivityCreated(savedInstanceState);
            eventTitle = getArguments().getString("EVENT TITLE");
            username = getArguments().getString("USERNAME");
            String list = getArguments().getString("list");


            queue = Volley.newRequestQueue(getActivity());
            StringRequest goingTabRequest = new StringRequest(Request.Method.POST, GOING_GET_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("RESPONSE", response);

                    try {
                        JSONArray goingJSONArray = new JSONArray(response);
                        for(int i = 0; i < goingJSONArray.length(); i++) {
                            JSONObject goingJSONObject = goingJSONArray.getJSONObject(i);
                            if(!goingJSONObject.getString("username").equals(username)) {
                                goingThings.add(goingJSONObject.getString("username"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, goingThings);
                    setListAdapter(adapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Could not load people going", Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("EVENT TITLE", eventTitle);
                    return params;
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            goingTabRequest.setRetryPolicy(policy);

            StringRequest maybeTabRequest = new StringRequest(Request.Method.POST, MAYBE_GET_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("RESPONSE", response);
                    try {
                        JSONArray maybeJSONArray = new JSONArray(response);
                        for(int i = 0; i < maybeJSONArray.length(); i++) {
                            JSONObject maybeJSONObject = maybeJSONArray.getJSONObject(i);
                            if(!maybeJSONObject.getString("username").equals(username)){
                                maybeThings.add(maybeJSONObject.getString("username"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, maybeThings);
                    setListAdapter(adapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Could not load people maybe going", Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("EVENT TITLE", eventTitle);
                    return params;
                }
            };
            RetryPolicy policy2 = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            maybeTabRequest.setRetryPolicy(policy2);

            StringRequest wantToGoTabRequest = new StringRequest(Request.Method.POST, WANT_TO_GO_GET_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("RESPONSE", response);

                    try {
                        JSONArray wantToGoJSONArray = new JSONArray(response);
                        for(int i = 0; i < wantToGoJSONArray.length(); i++) {
                            JSONObject wantToGoJSONObject = wantToGoJSONArray.getJSONObject(0);
                            if(! wantToGoJSONObject.getString("username").equals(username)) {
                                wantToGoThings.add(wantToGoJSONObject.getString("username"));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, wantToGoThings);
                    setListAdapter(adapter);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "Could not load people that want to go", Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("EVENT TITLE", eventTitle);
                    return params;
                }
            };
            RetryPolicy policy3 = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            wantToGoTabRequest.setRetryPolicy(policy3);


            if(list.equals("goingList")) {
                queue.add(goingTabRequest);
            } else if (list.equals("maybeList")) {
                queue.add(maybeTabRequest);
            } else {
                queue.add(wantToGoTabRequest);
            }



            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    profileIntent = new Intent(getActivity(), ProfileActivity.class);
                    friend = false;

                    statusUsername = (String)getListView().getItemAtPosition(i);
                    Log.i("STATUS USERNAME", statusUsername);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, GET_FRIEND_REQUESTS, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray friendRequestArray = new JSONArray(response);
                                for(int i = 0; i < friendRequestArray.length(); i++) {
                                    if(friendRequestArray.getJSONObject(i).getString("username").equals(username)){
                                        friend = true;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            StringRequest stringRequest1 = new StringRequest(Request.Method.POST, GET_FRIENDS, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.i("RESPONSE", response);
                                    try {
                                        JSONArray friendArray = new JSONArray(response);
                                        for(int i = 0; i < friendArray.length(); i++) {
                                            Log.i("USERNAME", friendArray.getJSONObject(i).getString("username"));
                                            if(friendArray.getJSONObject(i).getString("username").equals(username)) {
                                                friend = true;
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    StringRequest stringRequest2 = new StringRequest(Request.Method.POST, GET_USER, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                userJSONObject = new JSONObject(response);
                                                profileIntent.putExtra("EMAIL", userJSONObject.getString("emailAddress"));
                                                profileIntent.putExtra("USER'S NAME", userJSONObject.getString("fullName"));
                                                profileIntent.putExtra("PHONE_NUMBER", userJSONObject.getString("phoneNumber"));
                                                profileIntent.putExtra("PHOTO_LINK", userJSONObject.getString("photoLink"));
                                                JSONArray locationArray = userJSONObject.optJSONArray("locations");
                                                profileIntent.putExtra("COUNTRY", locationArray.getJSONObject(0).getString("country"));
                                                profileIntent.putExtra("PROVINCE_STATE", locationArray.getJSONObject(0).getString("provinceOrState"));
                                                profileIntent.putExtra("CITY", locationArray.getJSONObject(0).getString("city"));
                                                profileIntent.putExtra("LANGUAGE", userJSONObject.getString("language"));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            profileIntent.putExtra("USERNAME", statusUsername);
                                            profileIntent.putExtra("VIEWER_USERNAME", username);

                                            profileIntent.putExtra("FRIEND", friend);
                                            profileIntent.putExtra("USER", false);
                                            startActivity(profileIntent);
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {

                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            HashMap<String, String> params = new HashMap<>();
                                            params.put("USERNAME", statusUsername);
                                            return params;
                                        }
                                    };
                                    RetryPolicy policy2 = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                                    stringRequest2.setRetryPolicy(policy2);
                                    queue.add(stringRequest2);
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put("USERNAME", statusUsername);
                                    return params;
                                }
                            };
                            RetryPolicy policy1 = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                            stringRequest1.setRetryPolicy(policy1);
                            queue.add(stringRequest1);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("USERNAME", statusUsername);
                            return params;
                        }
                    };
                    RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                    stringRequest.setRetryPolicy(policy);
                    queue.add(stringRequest);




                }
            });

        }

        @Override
        public void onStart() {
            super.onStart();

        }
    }

}