package com.example.collectomon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchTist extends AppCompatActivity {
    private ListView listViewArtists;
    private List<String> artistNames;
    private ArrayAdapter<String> arrayAdapter;
    public Button addArtist, deleteArtist,continueButton;
    EditText artistName;
    private int checkedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_tist);
        addArtist = findViewById(R.id.addArtistButton);
        deleteArtist = findViewById(R.id.deleteButton);
        continueButton = findViewById(R.id.continuButton);
        artistName = findViewById(R.id.addArtist);

        artistNames = new ArrayList<>();
        artistNames.add("Yuka Morii");
        artistNames.add("Saya Tsuruta");

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
                }
            }
        });

        addArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add the artist name to the ListView
                String name = artistName.getText().toString();
                addArtistToList(name);
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
                    intent.putExtra("artist", modifiedName);
                    startActivity(intent);
                }
            }
        });
    }

    public void addArtistToList(String name) {
        artistNames.add(name); // Add the name to the artistNames list
        arrayAdapter.notifyDataSetChanged(); // Notify the adapter of the data change
        artistName.setText(""); // Clear the input field after adding the name
        listViewArtists.clearChoices(); // Uncheck all items
        checkedPosition = -1; // Reset the checked position
    }
}
