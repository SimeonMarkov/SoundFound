package com.soundfound.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.soundfound.R;
import com.soundfound.db.DBHelper;
import com.soundfound.model.Song;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final DBHelper myDb = new DBHelper(this);
    private static final String TAG = PlaylistActivity.class.getName();
    private static List<Song> songsForUser;
    private static int playlistSize;

    public PlaylistActivity() throws SQLException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            songsForUser = myDb.getSongsForUser(this.getIntent().getStringExtra("userId"));
            playlistSize = songsForUser.size();
            Log.i(TAG, myDb.getSongsForUser(this.getIntent().getStringExtra("userId")).toString());
            if(playlistSize > 0) {
               ListView songsListView = (ListView) findViewById(R.id.saved_songs_list);
                TextView songsTtextView = (TextView) findViewById(R.id.song_result_textview);
                songsListView.setVisibility(View.VISIBLE);
                songsTtextView.setVisibility(View.VISIBLE);
                songsTtextView.setText("Here's your songs");

                String[] listItems = new String[songsForUser.size()];
                for(int i =0; i < songsForUser.size(); i++) {
                   listItems[i] = songsForUser.get(i).toString();
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItems);
                songsListView.setAdapter(adapter);
            } else {
               TextView textView = (TextView) findViewById(R.id.song_result_textview);
                textView.setVisibility(View.VISIBLE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return playlistSize;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custom_layout, null);
            TextView songArtistTextView = (TextView)convertView.findViewById(R.id.song_name_artist_textview);
            TextView durationTextView = (TextView) convertView.findViewById(R.id.song_duration_textview);

            songArtistTextView.setText(songsForUser.get(position).getArtist() + " - " + songsForUser.get(position).getTrack());
            durationTextView.setText(songsForUser.get(position).getDuration());

            return convertView;
        }
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
        getMenuInflater().inflate(R.menu.playlist, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if(id == R.id.my_songs) {
        }
        else if (id == R.id.search_bar) {
            intent = new Intent(getBaseContext(), SearchActivity.class);
            intent.putExtra("userId", this.getIntent().getStringExtra("userId"));
            startActivity(intent);
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
