package com.example.collectomon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomePage extends AppCompatActivity {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    private List<String> artistNames;
    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";
    private SharedPreferences sharedPreferences;
    Button backup, restore;
    ImageButton addArtistButton,deleteArtistButton;
    CardDatabase db;
    Context context;
    private ListView listViewArtists;
    private ArrayAdapter<String> arrayAdapter;
    private int checkedPosition = -1;
    EditText addArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addArtistButton = findViewById(R.id.addArtistButton);
        deleteArtistButton = findViewById(R.id.deleteArtistButton);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(navListener);
        addArtist = findViewById(R.id.searchCard);
        backup = findViewById(R.id.backupButton);
        restore = findViewById(R.id.restoreButton);
        context = HomePage.this;
        db = new CardDatabase(context);
        sharedPreferences = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        artistNames = new ArrayList<>();
        artistNames.add("Yuka Morii");
        artistNames.add("Saya Tsuruta");

        Set<String> artistSet = sharedPreferences.getStringSet(ARTIST_KEY, null);
        if (artistSet != null) {
            artistNames = new ArrayList<>(artistSet);
        }
        saveArtistList(artistNames);

        listViewArtists = findViewById(R.id.listViewArtists);  // Find the ListView
        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item_artist, artistNames);
        listViewArtists.setAdapter(arrayAdapter);
        listViewArtists.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkedPosition = position;
            }
        });


        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.saveBackup();
            }
        });
        restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.restoreBackup();
            }
        });
        deleteArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedPosition != -1) {
                    artistNames.remove(checkedPosition);
                    arrayAdapter.notifyDataSetChanged();
                    listViewArtists.setItemChecked(checkedPosition, false);
                    checkedPosition = -1;
                    Toast.makeText(HomePage.this, "Artist deleted", Toast.LENGTH_SHORT).show();
                    saveArtistList(artistNames);
                } else {
                    Toast.makeText(HomePage.this, "No artist selection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        addArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addArtist.getText().toString().isEmpty()) {
                    Toast.makeText(HomePage.this, "No artist name", Toast.LENGTH_SHORT).show();
                } else {
                    String name = addArtist.getText().toString();
                    addArtistToList(name);
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with the backup operation
                db.saveBackup();
            } else {
                // Permission denied, handle the situation accordingly
                // For example, display a message to the user or disable backup functionality
            }
        }
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
                    if (id == R.id.home) {
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }
                    }
                    if (id == R.id.search_artists) {
                        Intent artistSearch = new Intent(HomePage.this, ArtistSearch.class);
                        closeDrawer();
                        startActivity(artistSearch);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;
                    }
                    if (id == R.id.my_collection) {
                        Intent myCollection = new Intent(HomePage.this, MyCollection.class);
                        closeDrawer();
                        startActivity(myCollection);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finishAffinity();
        }
    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(navigationView);
    }
    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().findItem(R.id.my_collection).setChecked(false);
    }
    public void addArtistToList(String name) {
        artistNames.add(name);
        arrayAdapter.notifyDataSetChanged();
        addArtist.setText("");
        listViewArtists.setItemChecked(artistNames.size() - 1, true);
        checkedPosition = artistNames.size() - 1;
        saveArtistList(artistNames);
    }

    private void saveArtistList(List<String> artistList) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>(artistList);
        editor.putStringSet(ARTIST_KEY, set);
        editor.apply();
    }
}
