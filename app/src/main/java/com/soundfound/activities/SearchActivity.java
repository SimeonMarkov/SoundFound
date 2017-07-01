package com.soundfound.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.soundfound.R;
import com.soundfound.db.DBHelper;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


public class SearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = SearchActivity.class.getName();

    private Request request;

    private final String API_KEY = "b0571a68f465528ebef054188d4e2201";
    final DBHelper myDb = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final String USER_ID = this.getIntent().getStringExtra("userId");
        final EditText searchText = (EditText) findViewById(R.id.search_bar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final TextView searchResult = (TextView) findViewById(R.id.search_result);
        final ImageView saveImage = (ImageView) findViewById(R.id.save_song);
        setSupportActionBar(toolbar);

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if(!StringUtils.isBlank(searchText.getText())) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    Log.i(SearchActivity.class.getName(), "Searching song " + searchText.getText());
                    String[] songAndArtist = searchText.getText().toString().split("-");
                    String artist;
                    final String song;

                    try {
                        artist = songAndArtist[0].trim();
                        song = songAndArtist[1].trim();
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Toast.makeText(getApplicationContext(), "Search should be in format {artist} - {song}", Toast.LENGTH_SHORT).show();
                        searchText.setText("");
                        searchResult.setText("");
                        saveImage.setVisibility(View.INVISIBLE);
                        return false;
                    }



                    final String url = "http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=" + API_KEY + "&artist=" + artist + "&track=" + song + "&format=json";
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(final JSONObject response) {
                                Log.i(TAG, url + "\n" + response.toString());
                                v.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            searchResult
                                                    .setText(response.getJSONObject("track").getJSONObject("artist").getString("name")
                                                            + "-" + response.getJSONObject("track").getString("name")
                                                            + "\t" + convertLongToMinutesFormat(response.getJSONObject("track").getLong("duration")));
                                            saveImage.setVisibility(View.VISIBLE);
                                            searchText.setText("");
                                            saveImage.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String[] songArtistDuration = searchResult.getText().toString().split("\t");
                                                    String duration = songArtistDuration[1];
                                                    String artist = songArtistDuration[0].split("-")[0];
                                                    String song = songArtistDuration[0].split("-")[1];
                                                    try {
                                                        myDb.insertSongs(artist, song, duration, USER_ID);
                                                        Toast.makeText(getApplicationContext(), "Song was saved", Toast.LENGTH_SHORT).show();
                                                        saveImage.setVisibility(View.INVISIBLE);
                                                        searchResult.setVisibility(View.INVISIBLE);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        } catch (JSONException e) {
                                            searchResult.setText(R.string.no_result);
                                            searchText.setText("");
                                            e.printStackTrace();
                                        }
                                    }
                                });


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    queue.add(jsonObjectRequest);

                    handled = true;
                }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Emtpy search. Try again!", Toast.LENGTH_SHORT).show();
                    saveImage.setVisibility(View.INVISIBLE);
                }
                return handled;
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private String convertLongToMinutesFormat(long number) {
        int secs = (int)(number / 1000) % 60;
        return (number / 1000) / 60 + ":" + ((secs > 9) ? String.valueOf(secs) :  new StringBuilder("0").append(secs).toString()) ;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if(id == R.id.my_songs) {
            intent = new Intent(getBaseContext(), PlaylistActivity.class);
            intent.putExtra("userId", this.getIntent().getStringExtra("userId"));
            startActivity(intent);
        }
        else if (id == R.id.search_bar) {
        }

        else if(id == R.id.logout) {
            intent = new Intent(getBaseContext(), LoginActivity.class);
            intent.putExtra("logged", "false");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
