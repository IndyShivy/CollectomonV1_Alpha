package com.example.collectomon;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TableLayout tableLayout = findViewById(R.id.tableLayout);

        Thread webScrapingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://www.serebii.net/card/dex/artist/yukamorii.shtml").get();
                    Element tableElement = doc.select("table.dextable").first();
                    Elements rowElements = tableElement.select("tr");

                    for (int i = 1; i < rowElements.size(); i++) {
                        Element row = rowElements.get(i);
                        Elements columnElements = row.select("td");

                        if (columnElements.size() >= 3) {
                            Element imageLink = columnElements.get(0).selectFirst("a");
                            String imageSrc = (imageLink != null) ? imageLink.selectFirst("img").attr("src") : "";
                            String imageSrc1 = "https://www.serebii.net/"+imageSrc;
                            Element cardNameElement = columnElements.get(1).selectFirst("font");
                            String cardName = (cardNameElement != null) ? cardNameElement.text() : "";

                            Element setLink = columnElements.get(2).selectFirst("a");
                            String setDetails = (setLink != null) ? setLink.text() : "";

                            String cardDetails = columnElements.get(2).ownText();

                            // Create a new table row
                            final TableRow tableRow = new TableRow(MainActivity.this);

                            // Create and set the image view
                            ImageView imageView = new ImageView(MainActivity.this);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Picasso.get().load(imageSrc1)
                                            .resize(200, 200)
                                            .centerInside()
                                            .into(imageView);
                                    tableRow.addView(imageView);
                                }
                            });

                            // Create and set the text view for card name
                            final TextView cardNameTextView = new TextView(MainActivity.this);
                            cardNameTextView.setText(cardName);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tableRow.addView(cardNameTextView);
                                }
                            });

                            // Create and set the text view for set details
                            final TextView setDetailsTextView = new TextView(MainActivity.this);
                            setDetailsTextView.setText(setDetails);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tableRow.addView(setDetailsTextView);
                                }
                            });

                            // Create and set the text view for card details
                            final TextView cardDetailsTextView = new TextView(MainActivity.this);
                            cardDetailsTextView.setText(cardDetails);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tableRow.addView(cardDetailsTextView);
                                }
                            });

                            // Create a CheckBox for card ownership
                            final CheckBox checkBox = new CheckBox(MainActivity.this);
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    // Handle checkbox state change
                                    // You can use the 'tableRow' variable to identify the corresponding card
                                    if (isChecked) {
                                        // Card is owned
                                    } else {
                                        // Card is not owned
                                    }
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tableRow.addView(checkBox);
                                }
                            });

                            // Add the table row to the table layout on the UI thread
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tableLayout.addView(tableRow);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        webScrapingThread.start();
    }
}



/*
                            imageView.setLayoutParams(new TableRow.LayoutParams(600, 600)); // Adjust the width and height as needed
                            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            imageView.setPadding(8, 8, 8, 8); // Adjust the padding as needed

                             */


