package com.example.collectomon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyCollection extends AppCompatActivity {
    private ListView listViewArtists;
    private ArrayAdapter<String> arrayAdapter;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";
    private CardDatabase databaseHelper;
    Toolbar toolbar;
    Context context;
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collection);

        context = MyCollection.this;
        databaseHelper = new CardDatabase(context);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Collection");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(navListener);

        recyclerView = findViewById(R.id.recyclerView);
        cardAdapter = new CardAdapter(new ArrayList<>(), MyCollection.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        listViewArtists = findViewById(R.id.listViewArtists);
        sharedPreferences = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);

        // Retrieve the artist list from SharedPreferences
        Set<String> artistSet = sharedPreferences.getStringSet(ARTIST_KEY, null);
        List<String> artistList = new ArrayList<>(artistSet);

        // Create an ArrayAdapter to populate the ListView with the artist names
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, artistList);

        // Set the ArrayAdapter as the adapter for the ListView
        listViewArtists.setAdapter(arrayAdapter);

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected artist name
                String selectedArtist = arrayAdapter.getItem(position);

                // Retrieve the corresponding card data from the CardDatabase
                List<CardItem> cardItems = databaseHelper.getCardsByArtist(selectedArtist);

                // Create a new CardAdapter with the retrieved card data
                cardAdapter = new CardAdapter(cardItems, MyCollection.this);

                // Set the CardAdapter as the adapter for the RecyclerView
                recyclerView.setAdapter(cardAdapter);
            }
        });
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
                    if (id == R.id.my_collection) {
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }
                    }
                    if (id == R.id.home) {
                        Intent artistSearch = new Intent(MyCollection.this, HomePage.class);
                        startActivity(artistSearch);
                        return true;
                    }
                    if (id == R.id.search_artists) {
                        Intent myCollection = new Intent(MyCollection.this, SearchTist.class);
                        closeDrawer();
                        startActivity(myCollection);
                        return true;
                    }
                    return false;
                }
            };
    /*
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

        }
    }*/
    private void closeDrawer() {
        drawerLayout.closeDrawer(navigationView);
    }

}
