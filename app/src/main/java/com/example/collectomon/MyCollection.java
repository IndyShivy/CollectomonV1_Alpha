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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyCollection extends AppCompatActivity {
    private ArrayAdapter<String> arrayAdapter;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_FILE_NAME = "MyPrefsFile";
    private static final String ARTIST_KEY = "artist";
    private CardDatabase databaseHelper;
    private Context context;
    private RecyclerView recyclerView;
    private CollectionAdapter collectionAdapter;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private Button deleteCards;
    private Spinner spinnerArtists;
    private CustomSpinnerAdapter spinnerAdapter;
    private List<String> artistList;
    private Toolbar toolbar;
    private ListView listViewArtists;
    private EditText searchEditText;
    private List<CardItem> cardItems;

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
        cardItems = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        collectionAdapter = new CollectionAdapter(new ArrayList<>(), MyCollection.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        listViewArtists = findViewById(R.id.listViewArtists);
        sharedPreferences = getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        deleteCards = findViewById(R.id.deleteCardButton);
        Set<String> artistSet = sharedPreferences.getStringSet(ARTIST_KEY, null);
        artistList = new ArrayList<>(artistSet);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, artistList);
        listViewArtists.setAdapter(arrayAdapter);
        searchEditText = findViewById(R.id.searchEditText1);
        searchEditText.addTextChangedListener(textWatcher);

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedArtist = arrayAdapter.getItem(position);
                List<CardItem> cardItems = databaseHelper.getCardsByArtist(selectedArtist);
                collectionAdapter = new CollectionAdapter(cardItems, MyCollection.this);
                recyclerView.setAdapter(collectionAdapter);
            }
        });

        deleteCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CardItem> selectedCardItems = collectionAdapter.getSelectedCardItems();
                databaseHelper.deleteCards(selectedCardItems);
                String selectedArtist = spinnerArtists.getSelectedItem().toString();
                List<CardItem> updated = databaseHelper.getCardsByArtist(selectedArtist);
                collectionAdapter = new CollectionAdapter(updated, MyCollection.this);
                recyclerView.setAdapter(collectionAdapter);
                collectionAdapter.notifyDataSetChanged();
            }
        });


        spinnerArtists = findViewById(R.id.spinnerArtists);
        spinnerAdapter = new CustomSpinnerAdapter(context, artistList);
        spinnerArtists.setAdapter(spinnerAdapter);

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
            }
        });
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
                    if (id == R.id.my_collection) {
                        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            drawerLayout.closeDrawer(GravityCompat.START);
                        }
                    }
                    if (id == R.id.home) {
                        Intent artistSearch = new Intent(MyCollection.this, HomePage.class);
                        closeDrawer();
                        startActivity(artistSearch);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        return true;
                    }
                    if (id == R.id.search_artists) {
                        Intent myCollection = new Intent(MyCollection.this, ArtistSearch.class);
                        closeDrawer();
                        startActivity(myCollection);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        return true;
                    }
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return false;
                }
            };

    private void closeDrawer() {
        drawerLayout.closeDrawer(navigationView);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            filterCardItems(s.toString());
        }

        private void filterCardItems(String searchText) {
            List<CardItem> filteredList = new ArrayList<>();

            for (CardItem cardItem : databaseHelper.getAllCards()) {
                if (cardItem.getCardName().toLowerCase().startsWith(searchText.toLowerCase())) {
                    filteredList.add(cardItem);
                }
            }

            collectionAdapter.filterList(filteredList);  // Use the adapter's filterList method to update the RecyclerView
        }
    };

}
