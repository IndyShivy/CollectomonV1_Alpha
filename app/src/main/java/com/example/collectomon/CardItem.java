package com.example.collectomon;

public class CardItem {
    private String cardID;
    private String imageSrc;
    private String cardName;
    private String setDetails;
    private String cardDetails;
    private boolean selected;

    public CardItem(String cardID,String imageSrc, String cardName, String setDetails, String cardDetails) {
        this.imageSrc = imageSrc;
        this.cardName = cardName;
        this.setDetails = setDetails;
        this.cardDetails = cardDetails;
        this.selected = false;
        this.cardID = cardID;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public String getCardName() {
        return cardName;
    }

    public String getSetDetails() {
        return setDetails;
    }

    public String getCardDetails() {
        return cardDetails;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isChecked() {
        return selected;
    }

    public void setChecked(boolean checked) {
        this.selected=true;
    }

    public void setId(long cardId) {
    }

    public String getId() {
        return cardID;
    }
}
