package com.codecool.klondike;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Game extends Pane {

    private List<Card> deck = new ArrayList<>();

    private Pile stockPile;
    private Pile discardPile;
    private List<Pile> foundationPiles = FXCollections.observableArrayList();
    private List<Pile> tableauPiles = FXCollections.observableArrayList();
    private ArrayList<String> backgrounds = new ArrayList<>();
    private ArrayList<String> cardback = new ArrayList<>();

    private double dragStartX, dragStartY;
    private List<Card> draggedCards = FXCollections.observableArrayList();
    private List<Card> draggedBeyondCards = new ArrayList<>();

    private static double STOCK_GAP = 1;
    private static double FOUNDATION_GAP = 0;
    private static double TABLEAU_GAP = 30;

    private int negativeOrder = -1;
    private int positiveOrder = 1;
    private int switchBackground = 0;


    private EventHandler<MouseEvent> onMouseClickedHandler = e -> {
        Card card = (Card) e.getSource();
        if (e.getClickCount() == 2 && !card.isFaceDown() &&
                card.getContainingPile().getPileType() != Pile.PileType.STOCK &&
                    card.equals(card.getContainingPile().getTopCard())) {
            for (Pile foundationPile : foundationPiles) {
                Card topCard = foundationPile.getTopCard();
                if ((topCard != null && Card.isNextCard(topCard, card, 1) && card.getSuit() == topCard.getSuit()) ||
                        (card.getRank() == 1 && topCard == null)) {
                    card.moveToPile(foundationPile);
                }
            }
            flipTopCard();
            isGameWon();
        }
        else if (card == stockPile.getTopCard()) {
            if (card.getContainingPile().getPileType() == Pile.PileType.STOCK) {
                card.moveToPile(discardPile);
                card.flip();
                card.setMouseTransparent(false);
                System.out.println("Placed " + card + " to the waste.");
            }
        }
    };

    private EventHandler<MouseEvent> stockReverseCardsHandler = e -> {
        refillStockFromDiscard();
    };

    private EventHandler<MouseEvent> onMousePressedHandler = e -> {
        Card card = (Card) e.getSource();
        dragStartX = e.getSceneX();
        dragStartY = e.getSceneY();

        for (int i = 0; i < tableauPiles.size(); i++) {
            Pile pile = tableauPiles.get(i);
            List<Card> pileCards = pile.getCards();
            int sourceCardIndex = pileCards.indexOf(card);

            if (sourceCardIndex != -1) {
                draggedBeyondCards = pileCards.subList(sourceCardIndex, pileCards.size());
            }
        }
    };

    private EventHandler<MouseEvent> onMouseDraggedHandler = e -> {

        Card card = (Card) e.getSource();
        Pile activePile = card.getContainingPile();
        if (!card.isFaceDown()) {
            if (activePile.getPileType() == Pile.PileType.STOCK) {
                return;
            }
            if (activePile.getPileType() == Pile.PileType.DISCARD) {
                if (card != discardPile.getTopCard()) {
                    return;
                }
            }


            double offsetX = e.getSceneX() - dragStartX;
            double offsetY = e.getSceneY() - dragStartY;
            draggedCards.clear();

            if (activePile.getPileType() == Pile.PileType.TABLEAU) {
                draggedCards.addAll(draggedBeyondCards);
            } else {
                draggedCards.add(card);
            }

            for (Card cardIterator : draggedCards) {

                cardIterator.getDropShadow().setRadius(20);
                cardIterator.getDropShadow().setOffsetX(10);
                cardIterator.getDropShadow().setOffsetY(10);

                cardIterator.toFront();
                cardIterator.setTranslateX(offsetX);
                cardIterator.setTranslateY(offsetY);
            }
        }
    };

    private EventHandler<MouseEvent> onMouseReleasedHandler = e -> {
        if (draggedCards.isEmpty())
            return;
        Card card = (Card) e.getSource();
        Pile pile = getValidIntersectingPile(card, tableauPiles);
        Pile foundationPile = getValidFoundationPile(card, foundationPiles);
        //TODO Done
        if (pile != null) {
            handleValidMove(card, pile);
        } else if (foundationPile != null) {
            handleValidMove(card, foundationPile);
        } else {
            draggedCards.forEach(MouseUtil::slideBack);
            draggedCards.clear();
        }
    };


    public void isGameWon() {
        //TODO DONE
        int count = 0;
        for (int i = 0; i < 4; i++) {
            Pile foundationPile = foundationPiles.get(i);
            count += foundationPile.numOfCards();
        }
        if (count == 52) {
            Congratulation.display("Congratulation!!!", "WON", this);

        }
    }

    public Game() {
        deck = Card.createNewDeck();
        addKeyEventHandler();
        initPiles();
        dealCards();
    }

    public void addMouseEventHandlers(Card card) {
        card.setOnMousePressed(onMousePressedHandler);
        card.setOnMouseDragged(onMouseDraggedHandler);
        card.setOnMouseReleased(onMouseReleasedHandler);
        card.setOnMouseClicked(onMouseClickedHandler);
    }

    public void refillStockFromDiscard() {
        //TODO DONE
        ObservableList<Card> cards = discardPile.getCards();
        for (int i = cards.size() - 1; i > -1; i--) {
            Card card = cards.get(i);
            card.flip();
            card.moveToPile(stockPile);
        }
        System.out.println("Stock refilled from discard pile.");
    }

    public boolean isMoveValid(Card card, Pile destPile) {
        //TODO Done
        if (card.getRank() != 13 && destPile.numOfCards() == 0) {
            return false;
        }
        if (destPile.getTopCard() != null) {
            if (!Card.isOppositeColor(card, destPile.getTopCard()) ||
                    !Card.isNextCard(card, destPile.getTopCard(), positiveOrder)) {
                return false;
            }
        }
        return true;
    }


    private Pile getValidIntersectingPile(Card card, List<Pile> piles) {
        Pile result = null;
        for (Pile pile : piles) {
            if (!pile.equals(card.getContainingPile()) &&
                    isOverPile(card, pile) &&
                    isMoveValid(card, pile))
                result = pile;
        }
        return result;
    }

    private Pile getValidFoundationPile(Card card, List<Pile> piles) {
        Pile result = null;
        for (Pile pile : piles) {
            if (!pile.equals(card.getContainingPile()) &&
                    isOverPile(card, pile) &&
                    isFoundationMoveValid(card, pile))
                result = pile;
        }
        return result;
    }

    private boolean isFoundationMoveValid(Card card, Pile destPile) {
        if (destPile.getTopCard() != null) {
            if (Card.isNextCard(card, destPile.getTopCard(), negativeOrder) &&
                    Card.isSameSuit(card, destPile.getTopCard()) &&
                    draggedCards.size() == 1) {
                return true;
            }
        } else if (card.getRank() == 1) {
            return true;
        }
        return false;
    }

    private boolean isOverPile(Card card, Pile pile) {
        if (pile.isEmpty())
            return card.getBoundsInParent().intersects(pile.getBoundsInParent());
        else
            return card.getBoundsInParent().intersects(pile.getTopCard().getBoundsInParent());
    }

    private void handleValidMove(Card card, Pile destPile) {
        String msg = null;
        if (destPile.isEmpty()) {
            if (destPile.getPileType().equals(Pile.PileType.FOUNDATION))
                msg = String.format("Placed %s to the foundation.", card);
            if (destPile.getPileType().equals(Pile.PileType.TABLEAU))
                msg = String.format("Placed %s to a new pile.", card);
        } else {
            msg = String.format("Placed %s to %s.", card, destPile.getTopCard());
        }
        System.out.println(msg);
        MouseUtil.slideToDest(draggedCards, destPile, this);
        draggedCards.clear();
    }

    public void addButtonRestartHandler(Button button) {
        button.setOnAction(onButtonPressedHandler);
    }

    private EventHandler<ActionEvent> onButtonPressedHandler = e -> {
        Klondike newGame = new Klondike();
        Congratulation.close();
        Menu.close();
        newGame.start(Klondike.stage);
    };

    private void addBackrounds(ArrayList backgrounds) {
        backgrounds.add("/table/greenPattern.jpg");
        backgrounds.add("/table/green3.jpg");
        backgrounds.add("/table/green.png");
        backgrounds.add("card_images/diamonds1.png");
        backgrounds.add("card_images/hearts4.png");
    }

    public void addSwitchButtonHandler(Button button,int index) {
        button.setOnAction(onSwitchButtonPressedHandler);
    }

    private EventHandler<ActionEvent> onSwitchButtonPressedHandler = e -> {
        switchBackground++;
        if (switchBackground + 1 == backgrounds.size()) {
            switchBackground = 0;
        }
        setTableBackground(new Image(backgrounds.get(switchBackground)));

    };
    public int getSwitchBackground(){
        return switchBackground;
    }
    public void addKeyEventHandler(){
        setOnKeyPressed(ke -> {
            KeyCode keyCode = ke.getCode();
            if (keyCode.equals(KeyCode.S)) {
                Congratulation.display("asd","asd",this);
            }
            if (keyCode.equals(KeyCode.ESCAPE)){
                Menu.displayMenu(this);
            }
        });
    }
    public void addMenuEVentHandler(Button button){
        button.setOnAction(onMenuButtonPressed);
    }
    private EventHandler<ActionEvent> onMenuButtonPressed = e ->{
        Menu.displayMenu(this);
    };

    private void initPiles() {
        addBackrounds(backgrounds);

        Button btn = new Button("Menu");
        getChildren().add(btn);
        btn.setMinWidth(80);
        buttonStyle(btn);
        addMenuEVentHandler(btn);

        stockPile = new Pile(Pile.PileType.STOCK, "Stock", STOCK_GAP);
        stockPile.setBlurredBackground();
        stockPile.setLayoutX(95);
        stockPile.setLayoutY(20);
        stockPile.setOnMouseClicked(stockReverseCardsHandler);
        getChildren().add(stockPile);

        discardPile = new Pile(Pile.PileType.DISCARD, "Discard", STOCK_GAP);
        discardPile.setBlurredBackground();
        discardPile.setLayoutX(285);
        discardPile.setLayoutY(20);
        getChildren().add(discardPile);

        for (int i = 0; i < 4; i++) {
            Pile foundationPile = new Pile(Pile.PileType.FOUNDATION, "Foundation " + i, FOUNDATION_GAP);
            foundationPile.setBlurredBackground();
            foundationPile.setLayoutX(610 + i * 180);
            foundationPile.setLayoutY(20);
            foundationPiles.add(foundationPile);
            getChildren().add(foundationPile);
        }
        for (int i = 0; i < 7; i++) {
            Pile tableauPile = new Pile(Pile.PileType.TABLEAU, "Tableau " + i, TABLEAU_GAP);
            tableauPile.setBlurredBackground();
            tableauPile.setLayoutX(95 + i * 180);
            tableauPile.setLayoutY(275);
            tableauPiles.add(tableauPile);
            getChildren().add(tableauPile);
        }
    }
    public void buttonStyle(Button btn){
        btn.setStyle("-fx-background-color: \n" +
                "        #c3c4c4,\n" +
                "        linear-gradient(#d6d6d6 50%, white 100%),\n" +
                "        radial-gradient(center 50% -40%, radius 200%, #e6e6e6 45%, rgba(230,230,230,0) 50%);\n" +
                "    -fx-background-radius: 30;\n" +
                "    -fx-background-insets: 0,1,1;\n" +
                "    -fx-text-fill: black;\n" +
                "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 3, 0.0 , 0 , 1 );");
    }

    public void dealCards() {
        Collections.shuffle(deck);
        Iterator<Card> deckIterator = deck.iterator();
        //TODO DONE

        for (int i = 0; i < 7; i++) {
            Pile tableauPile = tableauPiles.get(i);
            for (int j = 0; j <= i; j++) {
                Card card = deckIterator.next();
                tableauPile.addCard(card);
                addMouseEventHandlers(card);
                getChildren().add(card);
            }
            tableauPile.getTopCard().flip();
        }

        deckIterator.forEachRemaining(card -> {
            stockPile.addCard(card);
            addMouseEventHandlers(card);
            getChildren().add(card);
        });

    }

    public void setTableBackground(Image tableBackground) {
        setBackground(new Background(new BackgroundImage(tableBackground,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

    public void flipTopCard() {
        for (int i = 0; i < 7; i++) {
            Pile tableauPile = tableauPiles.get(i);
            Card topCard = tableauPile.getTopCard();
            if (tableauPile.numOfCards() != 0) {
                if (topCard.isFaceDown()) {
                    topCard.flip();
                }
            }
        }
    }
}
