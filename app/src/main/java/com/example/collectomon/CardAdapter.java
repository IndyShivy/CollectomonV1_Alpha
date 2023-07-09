package com.example.collectomon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {

    private List<CardItem> cardItems;
    private Context context;
    private CardDatabase databaseHelper;


    public CardAdapter(List<CardItem> cardItems, Context context) {
        this.cardItems = cardItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemrow, parent, false);
        databaseHelper = new CardDatabase(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardItem cardItem = cardItems.get(position);
        // Load image using Picasso
        Picasso.get()
                .load(cardItem.getImageSrc())
                .resize(600, 600)
                .centerInside()
                .into(holder.imageView);

        holder.cardNameTextView.setText(cardItem.getCardName());
        holder.setDetailsTextView.setText(cardItem.getSetDetails());
        holder.cardDetailsTextView.setText(cardItem.getCardDetails());
        holder.checkbox.setOnCheckedChangeListener(null); // Clear any previous listener
        holder.checkbox.setChecked(cardItem.isChecked()); // Set the initial checked state

        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                cardItem.setChecked(isChecked); // Update the checked state of the card item

                // Update the checked status in the database
                databaseHelper.updateCardChecked(cardItem.getId(), isChecked);
                System.out.println(cardItem.getCardDetails()+cardItem.getCardName());
            }
        });
    }


    @Override
    public int getItemCount() {
        return cardItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView cardNameTextView;
        TextView setDetailsTextView;
        TextView cardDetailsTextView;
        CheckBox checkbox;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            cardNameTextView = itemView.findViewById(R.id.cardNameTextView);
            setDetailsTextView = itemView.findViewById(R.id.setDetailsTextView);
            cardDetailsTextView = itemView.findViewById(R.id.cardDetailsTextView);
            checkbox = itemView.findViewById(R.id.checkbox);
        }
    }
}
