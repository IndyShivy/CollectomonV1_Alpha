package com.example.collectomon;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Spinner;
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

public class ArtistSearch extends AppCompatActivity {
    private ListView listViewArtists;
    private List<String> artistNames;
    private ArrayAdapter<String> arrayAdapter;
    public Button addArtist, deleteArtist, continueButton;
    EditText artistName;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";
    private SharedPreferences sharedPreferences;
    private int checkedPosition = -1;
    Spinner spinnerArtists;
    CustomSpinnerAdapter spinnerAdapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_search);
        context = ArtistSearch.this;
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

        //addArtist = findViewById(R.id.addArtistButton);
        //deleteArtist = findViewById(R.id.deleteButton);
        continueButton = findViewById(R.id.continueButton);
        //artistName = findViewById(R.id.addArtist);
        sharedPreferences = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        artistNames = new ArrayList<>();
        Set<String> artistSet = sharedPreferences.getStringSet(ARTIST_KEY, null);
        if (artistSet != null) {
            artistNames = new ArrayList<>(artistSet);
        }

        listViewArtists = findViewById(R.id.artistWheel);
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, artistNames);
        listViewArtists.setAdapter(arrayAdapter);
        listViewArtists.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (checkedPosition != position) {
                    listViewArtists.setItemChecked(position, true);
                    checkedPosition = position;
                } else {
                    listViewArtists.setItemChecked(position, false);
                    checkedPosition = -1;
                }
            }
        });

        listViewArtists.post(new Runnable() {
            @Override
            public void run() {
                listViewArtists.setItemChecked(0, true);
                checkedPosition = 0;
            }
        });
        spinnerArtists = findViewById(R.id.spinnerArtists);
        spinnerAdapter = new CustomSpinnerAdapter(context, artistNames);
        spinnerArtists.setAdapter(spinnerAdapter);


        /*
        deleteArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedPosition != -1) {
                    artistNames.remove(checkedPosition);
                    arrayAdapter.notifyDataSetChanged();
                    listViewArtists.setItemChecked(checkedPosition, false);
                    checkedPosition = -1;
                    Toast.makeText(ArtistSearch.this, "Artist deleted", Toast.LENGTH_SHORT).show();
                    saveArtistList(artistNames);
                } else {
                    Toast.makeText(ArtistSearch.this, "No artist selection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (artistName.getText().toString().isEmpty()) {
                    Toast.makeText(ArtistSearch.this, "No artist name", Toast.LENGTH_SHORT).show();
                } else {
                    String name = artistName.getText().toString();
                    addArtistToList(name);
                }
            }
        });

         */

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedPosition != -1) {
                    String name = artistNames.get(checkedPosition);
                    // String name = spinnerArtists.getSelectedItem().toString();
                    String stringWithoutGaps = name.replaceAll("\\s+", "");
                    String modifiedName = stringWithoutGaps.toLowerCase();
                    Intent intent = new Intent(ArtistSearch.this, CardView.class);
                    intent.putExtra("artistView", name);
                    intent.putExtra("artist", modifiedName);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        if (drawerToggle.onOptionsItemSelected(item)) {
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
                        Intent artistSearch = new Intent(ArtistSearch.this, HomePage.class);
                        closeDrawer();
                        startActivity(artistSearch);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        return true;
                    }
                    if (id == R.id.my_collection) {
                        Intent myCollection = new Intent(ArtistSearch.this, MyCollection.class);
                        closeDrawer();
                        startActivity(myCollection);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return false;
                }
            };

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent homeIntent = new Intent(ArtistSearch.this, HomePage.class);
            startActivity(homeIntent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }


    private void closeDrawer() {
        drawerLayout.closeDrawer(navigationView);
    }
}