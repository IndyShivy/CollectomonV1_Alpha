package com.example.collectomon;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchTist extends AppCompatActivity {
    private ListView listViewArtists;
    private List<String> artistNames;
    private ArrayAdapter<String> arrayAdapter;
    public Button addArtist, deleteArtist,continueButton;
    EditText artistName;
    private int checkedPosition = -1;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tist);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Search Artist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(navListener);

        addArtist = findViewById(R.id.addArtistButton);
        deleteArtist = findViewById(R.id.deleteButton);
        continueButton = findViewById(R.id.continueButton);
        artistName = findViewById(R.id.addArtist);
        sharedPreferences = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        artistNames = new ArrayList<>();
        Set<String> artistSet = sharedPreferences.getStringSet(ARTIST_KEY, null);
        if (artistSet != null) {
            artistNames = new ArrayList<>(artistSet);
        }

        listViewArtists = findViewById(R.id.artistWheel);

        // Create an ArrayAdapter to populate the ListView with the artist names
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, artistNames);

        // Set the ArrayAdapter as the adapter for the ListView
        listViewArtists.setAdapter(arrayAdapter);
        listViewArtists.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // Set single choice mode

        // Set item click listener for the ListView
        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Check the clicked item and uncheck the previously checked item
                if (checkedPosition != position) {
                    listViewArtists.setItemChecked(position, true);
                    checkedPosition = position;
                } else {
                    listViewArtists.setItemChecked(position, false);
                    checkedPosition = -1;
                }
            }
        });

        deleteArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete the checked artist from the list
                if (checkedPosition != -1) {
                    artistNames.remove(checkedPosition);
                    arrayAdapter.notifyDataSetChanged();
                    listViewArtists.setItemChecked(checkedPosition, false);
                    checkedPosition = -1;
                    Toast.makeText(SearchTist.this, "Artist deleted", Toast.LENGTH_SHORT).show();

                    // Save the updated artist list to SharedPreferences
                    saveArtistList(artistNames);
                } else {
                    Toast.makeText(SearchTist.this, "No artist selection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        addArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the artist name to the ListView
                if (artistName.getText().toString().isEmpty()) {
                    Toast.makeText(SearchTist.this, "No artist name", Toast.LENGTH_SHORT).show();
                } else {
                    String name = artistName.getText().toString();
                    addArtistToList(name);
                }
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedPosition!=-1) {
                    String name = artistNames.get(checkedPosition);
                    String stringWithoutGaps = name.replaceAll("\\s+", "");
                    String modifiedName = stringWithoutGaps.toLowerCase();
                    Intent intent = new Intent(SearchTist.this, CardView.class);
                    intent.putExtra("artistView",name);
                    intent.putExtra("artist", modifiedName);
                    startActivity(intent);
                }
            }
        });
    }

    public void addArtistToList(String name) {
        artistNames.add(name);
        arrayAdapter.notifyDataSetChanged();
        artistName.setText("");
        listViewArtists.clearChoices();
        checkedPosition = -1;

        // Save the artist list to SharedPreferences
        saveArtistList(artistNames);
    }
    private void saveArtistList(List<String> artistList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>(artistList);
        editor.putStringSet(ARTIST_KEY, set);
        editor.apply();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final NavigationView.OnNavigationItemSelectedListener navListener =
            new NavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.search_artists) {
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }
                    }
                    if (id == R.id.home) {
                        Intent artistSearch = new Intent(SearchTist.this, HomePage.class);
                        startActivity(artistSearch);
                        return true;
                    }
                    if (id == R.id.my_collection) {
                        Intent myCollection = new Intent(SearchTist.this, MyCollection.class);
                        closeDrawer();
                        startActivity(myCollection);
                        return true;
                    }
                    return false;
                }
            };
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

        }
    }
    private void closeDrawer() {

        drawerLayout.closeDrawer(navigationView);
    }





}
