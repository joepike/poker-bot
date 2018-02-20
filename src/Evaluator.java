import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;

public class Evaluator {

    public Hashtable<String, ArrayList<Card>> allAvailableHands;
    private Hand hand;
    private int highestCardOrdinalForStraight;

    public Evaluator(){
        this.hand = null;
        this.highestCardOrdinalForStraight = 0;
    }

    /////////////////////////
    // Getters and setters //
    /////////////////////////

    public Hand getHand() {
        return this.hand;
    }

    public void setHand(Hand newHand) {
        this.hand = newHand;
    }

    //////////////////////////////////////
    // CHECKING HAND HAS WINNING TRICKS //
    //////////////////////////////////////

    public Card highCard(){
        Collections.sort(hand.getPlayableCards(), Card.CardRankComparator);
        return hand.getPlayableCards().get(0);
    }

    private boolean highCardHand(Hand hand){ ;
        return true;
    }

    public boolean pair(Hand hand){
        hand.groupByRank(hand.getPlayableCards());
        for (int i = hand.groupedByRank.size()-1; i >= 0; --i) {
            String key = Rank.values()[i].name();
            if (hand.groupedByRank.get(key).size() == 2) {
                return true;
            }
        }
        return false;
    }

    public boolean twoPair(Hand hand) {
        hand.groupByRank(hand.getPlayableCards());
        int pairCount = 0;
        for (int i = hand.groupedByRank.size()-1; i >= 0; --i) {
            String key = Rank.values()[i].name();
            if (hand.groupedByRank.get(key).size() == 2) {
                pairCount += 1;
            }
        }
        return pairCount >= 2;
    }

    public boolean threeOfAKind(Hand hand){
        hand.groupByRank(hand.getPlayableCards());
        for (int i = hand.groupedByRank.size()-1; i >= 0; --i) {
            String key = Rank.values()[i].name();
            if (hand.groupedByRank.get(key).size() == 3) {
                return true;
            }
        }
        return false;
    }

    public boolean straight(Hand hand){
        for (int n = 0; n < 3; n++) {
            hand.sortHand();
            int counter = 1;
            for (int i = 1; i < hand.sortedHighToLow.size() - 1; i++) {
                if(hand.sortedHighToLow.get(n).rank.ordinal() - counter == hand.sortedHighToLow.get(i).rank.ordinal()) {
                    counter += 1;
                }
            }
            if (counter >= 5) {
                this.highestCardOrdinalForStraight = hand.sortedHighToLow.get(n).rank.ordinal();
                return true;
            }
        }
        return false;
    }

    public boolean aceLowStraight(Hand hand) {
        hand.sortHand();
        int highestCardOrdinal = 0;
        for(int n = 0; n < hand.sortedHighToLow.size(); n++) {
            if(hand.sortedHighToLow.get(n).rank.ordinal() == 3){
                highestCardOrdinal = hand.sortedHighToLow.get(n).rank.ordinal();
            }
        }

        if(hand.sortedHighToLow.get(0).rank == Rank.ACE) {
            int counter = 1;
            for (int i = 0; i < hand.sortedHighToLow.size(); i++) {
                if (highestCardOrdinal - counter == hand.sortedHighToLow.get(i).rank.ordinal()) {
                    counter += 1;
                }
            }
            this.highestCardOrdinalForStraight = 3;
            return counter == 4;
        }
        else {
            return false;
        }
    }

    public boolean flush(Hand hand){
        hand.groupBySuit(hand.getPlayableCards());

        for (int i = 0; i < hand.groupedBySuit.size(); i++){
            String key = Suit.values()[i].name();
            if (hand.groupedBySuit.get(key).size() >= 5){
                return true;
            }
        }
        return false;
    }

    public boolean straightFlush(Hand hand){
        hand.groupBySuit(hand.getPlayableCards());

        for (int i = 0; i < hand.groupedBySuit.size(); i++){
            String key = Suit.values()[i].name();
            ArrayList<Card> suit = hand.groupedBySuit.get(key);
            if (suit.size() >= 5 ) {

                int highestCardOrdinal = suit.get(0).rank.ordinal();
                int counter = 1;
                for (int n = 1; n < suit.size(); n++) {
                    if(highestCardOrdinal - counter == suit.get(n).rank.ordinal()) {
                        counter += 1;
                    }
                }
                if (counter >= 5) {
                    return true;
                }

            }
        }
        return false;
    }

    public boolean royalFlush(Hand hand){
        hand.groupBySuit(hand.getPlayableCards());
        int counter = 0;
        for (int i = 0; i < hand.groupedBySuit.size(); i++){
            String key = Suit.values()[i].name();
            if (hand.groupedBySuit.get(key).size() >= 5 && hand.groupedBySuit.get(key).get(0).rank.name() == "ACE" && hand.groupedBySuit.get(key).get(1).rank.name() == "KING"){
                int highestOrdinal = hand.groupedBySuit.get(key).get(0).rank.ordinal();
                for(int j = 1; j < hand.groupedBySuit.get(key).size(); j++) {
                    if(highestOrdinal - j == hand.groupedBySuit.get(key).get(j).rank.ordinal()) {
                        counter += 1;
                    }
                }
                return counter >= 4;
            }
        }
        return false;
    }

    public boolean fullHouse(Hand hand) {
        hand.groupByRank(hand.getPlayableCards());
        if (threeOfAKind(hand) && pair(hand)) {
            return true;
        }
        return false;
    }

    public boolean fourOfAKind(Hand hand){
        hand.groupByRank(hand.getPlayableCards());
        for (int i = hand.groupedByRank.size()-1; i >= 0; --i) {
            String key = Rank.values()[i].name();
            if (hand.groupedByRank.get(key).size() == 4) {
                return true;
            }
        }
        return false;
    }

    ///////////////////////////////////
    // CATEGORISE ALL AVAILABLE HANDS//
    ///////////////////////////////////

    public void createAllAvailableHandsHashTable(){
        allAvailableHands = new Hashtable<>();
        for (int i = 0; i < WinningHands.values().length; i++) {
            String key = WinningHands.values()[i].name();
            allAvailableHands.put(key, new ArrayList<Card>());
        }
    }

    public void categoriseAvailableHands() {
        createAllAvailableHandsHashTable();

        if (pair(this.hand) && !twoPair(this.hand)) {
            addPairToAllAvailableHands();
        }
        if (twoPair(this.hand)) {
            addTwoPairToAllAvailableHands();
        }
        if (threeOfAKind(this.hand)) {
            addThreeOfAKindToAllAvailableHands();
        }
        if (fullHouse(this.hand)) {
            addFullHouseToAllAvailableHands();
        }
        if (straight(this.hand) || aceLowStraight(this.hand)) {
            addStraightToAllAvailableHands();
        }
        if (fourOfAKind(this.hand)) {
            addFourOfAKindToAllAvailableHands();
        }
        if (flush(this.hand)) {
            addFlushToAllAvailableHands();
        }
        if (straightFlush(this.hand)) {
            addStraightFlushToAllAvailableHands();
        }
        if (royalFlush(this.hand)) {
            addRoyalFlushToAllAvailableHands();
        }
        if (highCardHand(this.hand)) {
            addHighCardToAllAvailableHands();
        }
    }

    ///////////////////////////////////////////////
    // Adding types of hand to allAvailableHands //
    ///////////////////////////////////////////////

    private void addHighCardToAllAvailableHands() {
        int sizeOfPlayableCards = this.hand.sortedHighToLow.size();
        for (int i = 0; i < sizeOfPlayableCards; i++) {
            Card card = this.hand.sortedHighToLow.get(i);
            String typeOfHand = WinningHands.HIGHCARD.toString();
            allAvailableHands.get(typeOfHand).add(card);
        }
    }

    private void addPairToAllAvailableHands() {
        int sizeOfPlayableCards = this.hand.getPlayableCards().size();
        for (int i = 0; i < sizeOfPlayableCards; i++) {
            Card card = this.hand.getPlayableCards().get(i);
            String typeOfHand = WinningHands.PAIR.toString();
            allAvailableHands.get(typeOfHand).add(card);
        }
    }

    private void addTwoPairToAllAvailableHands() {
        int sizeOfPlayableCards = this.hand.getPlayableCards().size();
        for (int i = 0; i < sizeOfPlayableCards; i++) {
            Card card = this.hand.getPlayableCards().get(i);
            String typeOfHand = WinningHands.TWOPAIR.toString();
            allAvailableHands.get(typeOfHand).add(card);
        }
    }

    private void addThreeOfAKindToAllAvailableHands() {
        int sizeOfPlayableCards = this.hand.getPlayableCards().size();
        for (int i = 0; i < sizeOfPlayableCards; i++) {
            Card card = this.hand.getPlayableCards().get(i);
            String typeOfHand = WinningHands.THREEOFAKIND.toString();
            allAvailableHands.get(typeOfHand).add(card);
        }
    }

    private void addFullHouseToAllAvailableHands() {
        int sizeOfPlayableCards = this.hand.getPlayableCards().size();
        for (int i = 0; i < sizeOfPlayableCards; i++) {
            Card card = this.hand.getPlayableCards().get(i);
            String typeOfHand = WinningHands.FULLHOUSE.toString();
            allAvailableHands.get(typeOfHand).add(card);
        }
    }

    private void addStraightToAllAvailableHands() {
        int sizeOfPlayableCards = this.hand.getPlayableCards().size();
        for (int i = 0; i < sizeOfPlayableCards; i++) {
            Card card = this.hand.getPlayableCards().get(i);
            String typeOfHand = WinningHands.STRAIGHT.toString();
            allAvailableHands.get(typeOfHand).add(card);
        }
    }

    private void addFourOfAKindToAllAvailableHands() {
        int sizeOfPlayableCards = this.hand.getPlayableCards().size();
        for (int i = 0; i < sizeOfPlayableCards; i++) {
            Card card = this.hand.getPlayableCards().get(i);
            String typeOfHand = WinningHands.FOUROFAKIND.toString();
            allAvailableHands.get(typeOfHand).add(card);
        }
    }

    private void addFlushToAllAvailableHands() {
        int sizeOfPlayableCards = this.hand.getPlayableCards().size();
        for (int i = 0; i < sizeOfPlayableCards; i++) {
            Card card = this.hand.getPlayableCards().get(i);
            String typeOfHand = WinningHands.FLUSH.toString();
            allAvailableHands.get(typeOfHand).add(card);
        }
    }

    private void addStraightFlushToAllAvailableHands() {
        int sizeOfPlayableCards = this.hand.getPlayableCards().size();
        for (int i = 0; i < sizeOfPlayableCards; i++) {
            Card card = this.hand.getPlayableCards().get(i);
            String typeOfHand = WinningHands.STRAIGHTFLUSH.toString();
            allAvailableHands.get(typeOfHand).add(card);
        }
    }

    private void addRoyalFlushToAllAvailableHands() {
        int sizeOfPlayableCards = this.hand.getPlayableCards().size();
        for (int i = 0; i < sizeOfPlayableCards; i++) {
            Card card = this.hand.getPlayableCards().get(i);
            String typeOfHand = WinningHands.ROYALFLUSH.toString();
            allAvailableHands.get(typeOfHand).add(card);
        }
    }

    public String typeOfBestHand(){
        for (int i = this.allAvailableHands.size() - 1; i >= 0  ; --i) {
            int indexOfEnum = WinningHands.values()[i].ordinal();
            String key = WinningHands.values()[indexOfEnum].name();
            if (!this.allAvailableHands.get(key).isEmpty()){
                return key;
            }
        }
        return "you not have a high card you silly";
    }

    ////////////////////////////
    // SELECT BEST FIVE CARDS //
    ////////////////////////////

    public void selectBestFiveCards(){
        clearBestFiveCards();
        if (typeOfBestHand() == "ROYALFLUSH"){
            royalFlushOrFlushShrink(this.hand);
        } else if (typeOfBestHand() == "STRAIGHTFLUSH") {
            royalFlushOrFlushShrink(this.hand);
        } else if (typeOfBestHand() == "FOUROFAKIND") {
            fourOfAKindShrink(this.hand);
        } else if (typeOfBestHand() == "FULLHOUSE") {
            fullHouseShrink(this.hand);
        } else if (typeOfBestHand() == "FLUSH") {
            royalFlushOrFlushShrink(this.hand);
        } else if (typeOfBestHand() == "STRAIGHT") {
            straightShrink(this.hand);
        } else if (typeOfBestHand() == "THREEOFAKIND") {
            threeOfAKindShrink(this.hand);
        } else if (typeOfBestHand() == "TWOPAIR") {
            pairShrink(this.hand);
        } else if (typeOfBestHand() == "PAIR") {
            pairShrink(this.hand);
        } else if (typeOfBestHand() == "HIGHCARD") {
            highCardShrink(this.hand);
        }
    }

    private void clearBestFiveCards() {
        if (this.hand.getBestFiveCards().size() != 0){
            this.hand.getBestFiveCards().clear();
        }
    }

    private void highCardShrink(Hand hand) {
        for (int j = 0; j < hand.sortedHighToLow.size(); j++) {
            Card card = hand.sortedHighToLow.get(j);
            if (!hand.getBestFiveCards().contains(card) && hand.getBestFiveCards().size() < 5) {
                hand.getBestFiveCards().add(card);
            }
        }
    }

    private void pairShrink(Hand hand) {
        for (int i = hand.groupedByRank.size()-1; i >= 0; --i) {
            String key = Rank.values()[i].name();
            int numberOfSameRank = hand.groupedByRank.get(key).size();
            if (numberOfSameRank == 2) {
                for (int j = 0; j < numberOfSameRank; j++) {
                    Card card = hand.groupedByRank.get(key).get(j);
                    hand.getBestFiveCards().add(card);
                }
            }
        }
        highCardShrink(hand);
    }

    private void threeOfAKindShrink(Hand hand){
        // extract into a differnet method
        for (int i = hand.groupedByRank.size()-1; i >= 0; --i) {
            String key = Rank.values()[i].name();
            int numberOfSameRank = hand.groupedByRank.get(key).size();
            if (numberOfSameRank == 3) {
                for (int j = 0; j < numberOfSameRank; j++) {
                    Card card = hand.groupedByRank.get(key).get(j);
                    hand.getBestFiveCards().add(card);
                }
            }
        }
        highCardShrink(hand);
    }

    private void straightShrink(Hand hand) {
        for (int j = 0; j < 4; j++) {
            if (hand.sortedHighToLow.get(j).rank == Rank.values()[highestCardOrdinalForStraight]) {
                hand.getBestFiveCards().add(hand.sortedHighToLow.get(j));
                for (int i = 1; i < 7; i++) {
                    for (int n = 1; n < 5; n++) {
                        if (highestCardOrdinalForStraight - n == hand.sortedHighToLow.get(i).rank.ordinal()) {
                            hand.getBestFiveCards().add(hand.sortedHighToLow.get(i));
                        }
                    }
                }
                if (highestCardOrdinalForStraight == 3) {
                    hand.getBestFiveCards().add(hand.sortedHighToLow.get(0));
                }
            }
        }
    }

    private void fullHouseShrink(Hand hand){
        for (int i = hand.groupedByRank.size()-1; i >= 0; --i){
            String key = Rank.values()[i].name();
            int numberOfSameRank = hand.groupedByRank.get(key).size();
            if (numberOfSameRank == 3) {
                for (int j = 0; j < numberOfSameRank; j++) {
                    Card card = hand.groupedByRank.get(key).get(j);
                    hand.getBestFiveCards().add(card);
                }
            } else if (numberOfSameRank == 2) {
                for (int j = 0; j < numberOfSameRank; j++) {
                    Card card = hand.groupedByRank.get(key).get(j);
                    hand.getBestFiveCards().add(card);
                }
            }
        }
    }

    private void fourOfAKindShrink(Hand hand){
        for (int i = hand.groupedByRank.size()-1; i >= 0; --i){
            String key = Rank.values()[i].name();
            int numberOfSameRank = hand.groupedByRank.get(key).size();
            if (numberOfSameRank == 4) {
                for (int j = 0; j < numberOfSameRank; j++) {
                    Card card = hand.groupedByRank.get(key).get(j);
                    hand.getBestFiveCards().add(card);
                }
            }
        }
        highCardShrink(hand);
    }

    private void royalFlushOrFlushShrink(Hand hand) {
        for (int i = 0; i < hand.groupedBySuit.size(); i++) {
            String key = Suit.values()[i].name();
            if (hand.groupedBySuit.get(key).size() >= 5) {
                for (int j = 0; j < 5; j++) {
                    Card card = hand.groupedBySuit.get(key).get(j);
                    hand.getBestFiveCards().add(card);
                }
            }
        }
    }

}
