package com.example.collectomon;

public class CardItem {
    private String artistName;
    private String cardID;
    private String imageSrc;
    private String cardName;
    private String setDetails;
    private String cardDetails;
    private boolean isChecked;


    public CardItem(String artistName,String cardID,String imageSrc, String cardName, String setDetails, String cardDetails) {
        this.imageSrc = imageSrc;
        this.cardName = cardName;
        this.setDetails = setDetails;
        this.cardDetails = cardDetails;
        this.isChecked = false;
        this.cardID = cardID;
        this.artistName = artistName;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked=checked;
    }
    public String getId() {
        return cardID;
    }

    public String toString(){
        return cardID+" "+imageSrc+" "+cardName+" "+setDetails+" "+ cardDetails;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getCardId() {
        return cardID;
    }

    public String getImageUrl() {
        return imageSrc;
    }
}
