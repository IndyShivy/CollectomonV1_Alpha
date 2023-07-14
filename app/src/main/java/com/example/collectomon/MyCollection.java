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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


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
    private CollectionAdapter collectionAdapter;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;
    Button deleteCards;
    private Spinner spinnerArtists;
    private CustomSpinnerAdapter spinnerAdapter;
    private List<String> artistList;

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
        collectionAdapter = new CollectionAdapter(new ArrayList<>(), MyCollection.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        listViewArtists = findViewById(R.id.listViewArtists);
        sharedPreferences = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        deleteCards = findViewById(R.id.deleteCardButton);
        // Retrieve the artist list from SharedPreferences
        Set<String> artistSet = sharedPreferences.getStringSet(ARTIST_KEY, null);
        artistList = new ArrayList<>(artistSet);

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
                collectionAdapter = new CollectionAdapter(cardItems, MyCollection.this);

                // Set the CardAdapter as the adapter for the RecyclerView
                recyclerView.setAdapter(collectionAdapter);
            }
        });

        deleteCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CardItem> selectedCardItems = collectionAdapter.getSelectedCardItems();
                databaseHelper.deleteCards(selectedCardItems);
                List<CardItem> updated = databaseHelper.getallCards();
                collectionAdapter = new CollectionAdapter(updated, MyCollection.this);
                recyclerView.setAdapter(collectionAdapter);
                collectionAdapter.notifyDataSetChanged();
            }
        });
        spinnerArtists = findViewById(R.id.spinnerArtists);
        artistList = new ArrayList<>(artistSet);
        spinnerAdapter = new CustomSpinnerAdapter();
        spinnerArtists.setAdapter(spinnerAdapter);


// Set an item click listener to show the dropdown when the spinner is clicked
        spinnerArtists.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    spinnerArtists.performClick();
                    return true;
                }
                return false;
            }
        });
        spinnerArtists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedArtist = artistList.get(position);
                List<CardItem> cardItems = databaseHelper.getCardsByArtist(selectedArtist);
                collectionAdapter = new CollectionAdapter(cardItems, MyCollection.this);
                recyclerView.setAdapter(collectionAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when nothing is selected
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
    public class CustomSpinnerAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public CustomSpinnerAdapter() {
            inflater = LayoutInflater.from(MyCollection.this);
        }

        @Override
        public int getCount() {
            return artistList.size();
        }

        @Override
        public String getItem(int position) {
            return artistList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.artistName = convertView.findViewById(R.id.artistName);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // Set data to the views
            String artist = artistList.get(position);
            viewHolder.artistName.setText(artist);

            return convertView;
        }


        private class ViewHolder {
            TextView artistName;
        }
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.artistName = convertView.findViewById(R.id.artistName);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // Set data to the views
            String artist = artistList.get(position);
            viewHolder.artistName.setText(artist);

            return convertView;
        }

    }

}
