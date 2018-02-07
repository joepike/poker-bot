import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class Evaluator {

    public ArrayList<Card> hand;

//    public Hashtable<String, ArrayList<Card>> groupByRank(ArrayList<Card> hand) {
//        public Hashtable<String, ArrayList<Card>> cardsByRank = new Hashtable<String, ArrayList<Card>>();
//
//        return cardsByRank;
//    }


    public Card highCard(){
        Collections.sort(hand, Card.CardRankComparator);
        return hand.get(0);
    }

    public boolean pair(){

       int comp = 0;
       for (int i = 0; i < hand.size(); i++) {
           comp = Rank.valueOf(hand.get(i).rank.name()).ordinal();
           for (int j = i + 1; j < hand.size(); j++) {
               if(comp == Rank.valueOf(hand.get(j).rank.name()).ordinal()) {
                   return true;
               }
           }
       }
       return false;
    }

    public boolean highestPair(){

//        // descending sort
//        for (int i = 0; i < hand.size(); i++) {
//            if(Rank.valueOf(hand.get(i).rank.name()).ordinal() < Rank.valueOf(hand.get(i + 1).rank.name()).ordinal()) {
//                temp = Rank.valueOf(hand.get(i).rank.name());
//            }
//        }

        int comp = 0;
        for (int i = 0; i < hand.size(); i++) {
            comp = Rank.valueOf(hand.get(i).rank.name()).ordinal();
            for (int j = i + 1; j < hand.size(); j++) {
                if(comp == Rank.valueOf(hand.get(j).rank.name()).ordinal()) {
                    return true;
                }
            }
        }
        return false;
    }

}