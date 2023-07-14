package com.example.collectomon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CardView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private List<CardItem> cardItems;
    private CardDatabase databaseHelper;
    private Button addCards,myCards;
    Toolbar toolbar;
    CheckBox checkBox;
    ArrayList<CardItem> selectedCardItemsList = new ArrayList<>();
    String artistName;
    private EditText searchEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        Context context = CardView.this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Card List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(textWatcher);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        cardItems = new ArrayList<>();
        cardAdapter = new CardAdapter(cardItems, context);
        recyclerView.setAdapter(cardAdapter);
        myCards = findViewById(R.id.myCardsButton);
        addCards = findViewById(R.id.addCardsButton);
        databaseHelper = new CardDatabase(context);
        Intent intent = getIntent();
        String modifiedName = intent.getStringExtra("artist");
        artistName = intent.getStringExtra("artistView");
        String theLink = "https://www.serebii.net/card/dex/artist/"+modifiedName+".shtml";


        myCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardView.this, MyCollection.class);
                startActivity(intent);
            }
        });
        addCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<CardItem> selectedCardItems = cardAdapter.getSelectedCardItems();
                cardAdapter.notifyDataSetChanged();
                databaseHelper.addCards(selectedCardItems);
            }
        });



        String finalTheLink = theLink;
        System.out.println(finalTheLink);
        Thread webScrapingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect(finalTheLink).get();
                    Element tableElement = doc.select("table.dextable").first();
                    Elements rowElements = tableElement.select("tr");

                    for (int i = 1; i < rowElements.size(); i++) {
                        Element row = rowElements.get(i);
                        Elements columnElements = row.select("td");

                        // Check if the row has at least three columns and the first column has an image
                        if (columnElements.size() >= 3 && columnElements.get(0).selectFirst("a img") != null) {
                            Element imageLink = columnElements.get(0).selectFirst("a");
                            String imageSrc = (imageLink != null) ? imageLink.selectFirst("img").attr("src") : "";
                            String imageSrc1 = "https://www.serebii.net/" + imageSrc;
                            Element cardNameElement = columnElements.get(1).selectFirst("font");
                            String cardName = (cardNameElement != null) ? cardNameElement.text() : "";

                            Element setLink = columnElements.get(2).selectFirst("a");
                            String setDetails = (setLink != null) ? setLink.text() : "";

                            String cardDetails = columnElements.get(2).ownText();
                            String cardId = artistName+cardName+setDetails+cardDetails;
                            CardItem cardItem = new CardItem(artistName,cardId, imageSrc1, cardName, setDetails, cardDetails);
                            cardItems.add(cardItem);
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cardAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        webScrapingThread.start();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    };
    private void filterCardItems(String searchText) {
        List<CardItem> filteredList = new ArrayList<>();

        for (CardItem cardItem : cardItems) {
            if (cardItem.getCardName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(cardItem);
            }
        }

        cardAdapter.filterList(filteredList);
    }



}



