package com.codecool.klondike;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.*;

public class Card extends ImageView {

    private Suit suit;
    private Rank rank;
    private boolean faceDown;
    private Image backFace;
    private Image frontFace;
    private Pile containingPile;
    private DropShadow dropShadow;

    static Image cardBackImage;
    private static final Map<String, Image> cardFaceImages = new HashMap<>();
    public static final int WIDTH = 150;
    public static final int HEIGHT = 215;

    public Card(int suit, int rank, boolean faceDown) {
        this.suit = Suit.values()[suit - 1];
        this.rank = Rank.values()[rank - 1];
        this.faceDown = faceDown;
        this.dropShadow = new DropShadow(2, Color.gray(0, 0.75));
        backFace = cardBackImage;
        frontFace = cardFaceImages.get(getShortName());
        setImage(faceDown ? backFace : frontFace);
        setEffect(dropShadow);
    }

    public int getSuit() {
        return suit.value;
    }
    public String getColor() {
        return suit.color;
    }

    public int getRank() {
        return rank.value;
    }

    public boolean isFaceDown() {
        return faceDown;
    }

    public String getShortName() {
        return "S" + suit.value + "R" + rank.value;
    }

    public DropShadow getDropShadow() {
        return dropShadow;
    }

    public Pile getContainingPile() {
        return containingPile;
    }

    public void setContainingPile(Pile containingPile) {
        this.containingPile = containingPile;
    }

    public void moveToPile(Pile destPile) {
        this.getContainingPile().getCards().remove(this);
        destPile.addCard(this);
    }

    public void flip() {
        faceDown = !faceDown;
        setImage(faceDown ? backFace : frontFace);
    }

    @Override
    public String toString() {
        return "The " + "Rank" + rank.value + " of " + "Suit" + suit.value;
    }

    public static boolean isOppositeColor(Card card1, Card card2) {
        //TODO
        if (!card1.getColor().equals(card2.getColor())){
            return true;
        }
        return false;
    }
    public static boolean isNextCard(Card card1,Card card2,int order){
        if (card1.getRank()+order == card2.getRank()){
            return true;
        }
        return false;
    }
    public static boolean isSameSuit(Card card1, Card card2) {
        return card1.getSuit() == card2.getSuit();
    }

    public static List<Card> createNewDeck() {
        List<Card> result = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(new Card(suit.value, rank.value, true));
            }
        }
        return result;
    }

    public static void loadCardImages() {
        cardBackImage = new Image("card_images/card_back.png");
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                String cardName = suit.suitName + rank.value;
                String cardId = "S" + suit.value + "R" + rank.value;
                String imageFileName = "card_images/" + cardName + ".png";
                cardFaceImages.put(cardId, new Image(imageFileName));
            }
        }
    }

    private enum Suit {

        HEARTS("hearts", 1,"red"),
        SPADES("spades", 2,"black"),
        DIAMONDS("diamonds", 3,"red"),
        CLUBS("clubs", 4,"black");

        String suitName;
        String color;
        int value;

        Suit(String suitName, int value,String color) {
            this.suitName = suitName;
            this.value = value;
            this.color = color;
        }
    }

    private enum Rank {

        ACE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8),
        NINE(9),
        TEN(10),
        JACK(11),
        QUEEN(12),
        KING(13);

        int value;

        Rank(int value) {
            this.value = value;
        }
    }
}