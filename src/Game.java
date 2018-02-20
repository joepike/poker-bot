import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Game {
    private JPanel main;
    private JButton Play;
    private JPanel room;
    private JLabel bot1rank;
    private JLabel bot1suit;
    private JLabel bot2rank;
    private JLabel bot2suit;
    private JPanel bot1;
    private JPanel bot2;
    private JButton dealFlopButton;
    private JPanel board;
    private JPanel flop1;
    private JPanel flop2;
    private JPanel flop3;
    private JPanel turn;
    private JPanel river;
    private JLabel flop1suit;
    private JLabel flop1rank;
    private JLabel flop2suit;
    private JLabel flop2rank;
    private JLabel flop3suit;
    private JLabel flop3rank;
    private JLabel riversuit;
    private JLabel riverrank;
    private JLabel turnsuit;
    private JLabel turnrank;
    private JPanel player1;
    private JPanel player2;
    private JLabel player1rank;
    private JLabel player1suit;
    private JLabel player2suit;
    private JLabel player2rank;
    private JButton smallRaise;
    private JButton call;
    private JButton checkFold;
    private JLabel botStatus;
    private JLabel playerStatus;
    private JButton resetButton;
    private JLabel botChips;
    private JLabel playerChips;
    private JLabel potText;
    private JButton bigRaise;
    private JButton nextRoundButton;
    private JLabel winningHand;
    private JLabel winningPlayer;
    public int pot;
    public String gamestate;

    public static void main(String args[]) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new Game().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public Game() {
        Dealer dealer = new Dealer();
        Bot bot = new Bot();
        Player player = new Player();
        pot = 0;
        gamestate = "preflop";
        dealer.generateRankSymbols();
        dealer.generateSuitSymbols();
        call.setVisible(false);
        checkFold.setVisible(false);
        smallRaise.setVisible(false);
        bigRaise.setVisible(false);
        botChips.setText(Integer.toString(bot.chips));
        playerChips.setText(Integer.toString(player.chips));
        nextRoundButton.setVisible(false);

        Play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dealHands(bot, player, dealer);
                bot.weighHoldEm();
                displayButtons(bot);
                Play.setVisible(false);
                blinds(bot, player);
            }
        });

        checkFold.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bot.chips += pot;
                botStatus.setText(bot.status);
                reset(bot, dealer, player);
            }
        });

        call.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gamestate == "preflop") {
                    if (bot.status == "Check/Fold" || bot.status == "Call") {
                        displayFlop(dealer, bot, player);
                    } else if (bot.status == "Raise") {
                        displayFlop(dealer, bot, player);
                        bot.chips -= 20;
                        player.chips -= 20;
                        pot += 40;
                    }
                } else if (gamestate == "flop"){
                    if (bot.status == "Check/Fold" || bot.status == "Call"){
                        displayTurn(dealer, bot, player);
                    } else if (bot.status == "Small Raise"){
                        smallChipRaise(bot, player);
                        displayTurn(dealer, bot, player);
                    } else if (bot.status == "Large Raise"){
                        bigChipRaise(bot, player);
                        displayTurn(dealer, bot, player);
                    } else if (bot.status == "All in"){
                        allIn(bot, player);
                        displayTurn(dealer, bot, player);
                        displayRiver(dealer, bot, player);
                        endRound(bot, dealer, player);
                    }
                } else if (gamestate == "turn"){
                    if (bot.status == "Check/Fold" || bot.status == "Call"){
                        displayRiver(dealer, bot, player);
                    } else if (bot.status == "Small Raise"){
                        smallChipRaise(bot, player);
                        displayRiver(dealer, bot, player);
                    } else if (bot.status == "Large Raise"){
                        smallChipRaise(bot, player);
                        displayRiver(dealer, bot, player);
                    } else if (bot.status == "All in"){
                        allIn(bot, player);
                        displayRiver(dealer, bot, player);
                        endRound(bot, dealer, player);
                    }
                } else {
                    if (bot.status == "Check/Fold" || bot.status == "Call") {
                        endRound(bot, dealer, player);
                    } else if (bot.status == "Small Raise"){
                        smallChipRaise(bot, player);
                        endRound(bot, dealer, player);
                    } else if (bot.status == "Large Raise"){
                        smallChipRaise(bot, player);
                        endRound(bot, dealer, player);
                    } else if (bot.status == "All in"){
                        allIn(bot, player);
                        endRound(bot, dealer, player);
                    }
                }
            }
        });

        smallRaise.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gamestate == "preflop") {
                    if (bot.status == "Call" || bot.status == "Raise") {
                        botStatus.setText(bot.status);
                        smallChipRaise(bot, player);
                        displayFlop(dealer, bot, player);
                    } else {
                        botFold(player, bot, dealer);
                    }
                } else if (gamestate == "flop"){
                    if (bot.status == "Call" || bot.status == "Small Raise") {
                        botStatus.setText(bot.status);
                        smallChipRaise(bot, player);
                        displayTurn(dealer, bot, player);
                    } else {
                        botFold(player, bot, dealer);
                    }
                } else if (gamestate == "turn"){
                    if (bot.status == "Call" || bot.status == "Small Raise" ) {
                        botStatus.setText(bot.status);
                        smallChipRaise(bot, player);
                        displayRiver(dealer, bot, player);
                    } else {
                        botFold(player, bot, dealer);
                    }
                } else {
                    if (bot.status == "Call" || bot.status == "Small Raise" ) {
                        botStatus.setText(bot.status);
                        smallChipRaise(bot, player);
                        endRound(bot, dealer, player);
                    } else {
                        botFold(player, bot, dealer);
                    }
                }
            }
        });

        bigRaise.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gamestate == "flop"){
                    if (bot.status == "Call" || bot.status == "Large Raise"){
                        botStatus.setText(bot.status);
                        bigChipRaise(bot, player);
                        bigChipRaise(bot, player);
                        displayTurn(dealer, bot, player);
                    } else {
                        botFold(player, bot, dealer);
                    }
                } else if (gamestate == "turn"){
                    if (bot.status == "Call" || bot.status == "Large Raise") {
                        botStatus.setText(bot.status);
                        smallChipRaise(bot, player);
                        displayRiver(dealer, bot, player);
                    } else {
                        botFold(player, bot, dealer);
                    }
                } else {
                    if (bot.status == "Call" || bot.status == "Large Raise" ) {
                        botStatus.setText(bot.status);
                        smallChipRaise(bot, player);
                        endRound(bot, dealer, player);
                    } else {
                        botFold(player, bot, dealer);
                    }
                }
            }
        });

        nextRoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset(bot, dealer, player);
                nextRoundButton.setVisible(false);
            }
        });
    }



    private void blinds(Bot bot, Player player) {
        bot.chips -= 10;
        player.chips -= 10;
        pot += 20;
        potText.setText(Integer.toString(pot));
    }

//    private void showStack(Bot bot, Player player) {
//        botChips.setText(Integer.toString(bot.chips));
//        playerChips.setText(Integer.toString(player.chips));
//    }

    public void botAnalyse(Bot bot) {
        bot.getHandWeight();
        bot.respondToHand();
        displayButtons(bot);
        System.out.println(bot.status);
        System.out.println("    ******************:PLAYABLECARDS******************:       ");
        for (int i = 0; i < bot.evaluator.getHand().getPlayableCards().size(); i++) {
            System.out.print(bot.evaluator.getHand().getPlayableCards().get(i).suit.name());
            System.out.println(bot.evaluator.getHand().getPlayableCards().get(i).rank.name());
        }
        System.out.println(bot.evaluator.getHand().getPlayableCards());
        System.out.println("    ******************:BESTFIVECARDS******************   ");
        for (int i = 0; i < bot.evaluator.getHand().getBestFiveCards().size(); i++) {
            System.out.print(bot.evaluator.getHand().getBestFiveCards().get(i).suit.name());
            System.out.println(bot.evaluator.getHand().getBestFiveCards().get(i).rank.name());
        }
        System.out.println(bot.status);
        System.out.println(bot.handWeight);
        System.out.println(bot.evaluator.typeOfBestHand());
    }

    private void displayButtons(Bot bot){
        if (bot.status == "Call") {
            smallRaise.setVisible(true);
            bigRaise.setVisible(true);
            bigRaise.setText("Big Raise");
            smallRaise.setText("Small Raise");
            call.setVisible(true);
            call.setText("Check");
            botStatus.setText("Check");
        } else if (bot.status == "Check/Fold") {
            botStatus.setText("Check");
            smallRaise.setVisible(true);
            smallRaise.setText("Small Raise");
            call.setVisible(true);
            call.setText("Check");
        } else if (bot.status == "All in"){
            call.setVisible(true);
            call.setText("Call");
            smallRaise.setVisible(false);
            bigRaise.setVisible(false);
            checkFold.setVisible(true);
            checkFold.setText("Fold");
            botStatus.setText(bot.status);
        } else if (bot.status == "Large Raise"){
            call.setVisible(true);
            call.setText("Call");
            checkFold.setVisible(true);
            checkFold.setText("Fold");
            bigRaise.setVisible(true);
            bigRaise.setText("Big Re-raise");
            botStatus.setText(bot.status);
        } else {
            call.setVisible(true);
            call.setText("Call");
            checkFold.setVisible(true);
            checkFold.setText("Fold");
            botStatus.setText(bot.status);
            smallRaise.setVisible(true);
            smallRaise.setText("Small Re-raise");
        }
    }

    private void botFold(Player player, Bot bot, Dealer dealer){
        player.chips += pot;
        botStatus.setText(bot.status);
        reset(bot, dealer, player);
    }

    private void smallChipRaise(Bot bot, Player player){
        bot.chips -= 20;
        player.chips -= 20;
        pot += 40;
    }

    private void bigChipRaise(Bot bot, Player player){
        bot.chips -= 40;
        player.chips -= 40;
        pot += 80;
    }

    private void allIn(Bot bot, Player player){
        pot += (bot.chips + player.chips);
        bot.chips = 0;
        player.chips = 0;
    }

    private void dealHands(Bot bot, Player player, Dealer dealer){
        dealer.dealHand(bot, player);
        String key1rankPlayer = player.getHand().getHoldEm().get(0).rank.name();
        String key2rankPlayer = player.getHand().getHoldEm().get(1).rank.name();
        String key1suitPlayer = player.getHand().getHoldEm().get(0).suit.name();
        String key2suitPlayer = player.getHand().getHoldEm().get(1).suit.name();
        colourCards(player1rank, key1suitPlayer);
        colourCards(player2rank, key2suitPlayer);
        colourCards(player1suit, key1suitPlayer);
        colourCards(player2suit, key2suitPlayer);
        player1rank.setText(dealer.rankSymbol.get(key1rankPlayer).toString());
        player1suit.setText(dealer.suitSymbol.get(key1suitPlayer).toString());
        player2rank.setText(dealer.rankSymbol.get(key2rankPlayer).toString());
        player2suit.setText(dealer.suitSymbol.get(key2suitPlayer).toString());

    }

    private void displayFlop(Dealer dealer, Bot bot, Player player) {
        String key3rank = bot.getHand().getPlayableCards().get(2).rank.name();
        String key4rank = bot.getHand().getPlayableCards().get(3).rank.name();
        String key5rank = bot.getHand().getPlayableCards().get(4).rank.name();
        String key3suit = bot.getHand().getPlayableCards().get(2).suit.name();
        String key4suit = bot.getHand().getPlayableCards().get(3).suit.name();
        String key5suit = bot.getHand().getPlayableCards().get(4).suit.name();
        flop1rank.setText(dealer.rankSymbol.get(key3rank).toString());
        flop1suit.setText(dealer.suitSymbol.get(key3suit).toString());
        flop2rank.setText(dealer.rankSymbol.get(key4rank).toString());
        flop2suit.setText(dealer.suitSymbol.get(key4suit).toString());
        flop3rank.setText(dealer.rankSymbol.get(key5rank).toString());
        flop3suit.setText(dealer.suitSymbol.get(key5suit).toString());
        colourCards(flop1rank, key3suit);
        colourCards(flop1suit, key3suit);
        colourCards(flop2rank, key4suit);
        colourCards(flop2suit, key4suit);
        colourCards(flop3rank, key5suit);
        colourCards(flop3suit, key5suit);
        updatePot(bot, player);
        gamestate = "flop";
        botAnalyse(bot);
    }

    private void displayTurn(Dealer dealer, Bot bot, Player player){
        dealer.dealTurn(bot, player);
        String turnRankInt = dealer.board.get(3).rank.name();
        String turnSuitInt = dealer.board.get(3).suit.name();
        colourCards(turnrank, turnSuitInt);
        colourCards(turnsuit, turnSuitInt);
        turnrank.setText(dealer.rankSymbol.get(turnRankInt).toString());
        turnsuit.setText(dealer.suitSymbol.get(turnSuitInt).toString());
        turnsuit.setBackground(Color.white);
        gamestate = "turn";
        updatePot(bot, player);
        botAnalyse(bot);
    }

    private void displayRiver(Dealer dealer, Bot bot, Player player){
        dealer.dealRiver(bot, player);
        String riverRankInt = dealer.board.get(4).rank.name();
        String riverSuitInt = dealer.board.get(4).suit.name();
        colourCards(riverrank, riverSuitInt);
        colourCards(riversuit, riverSuitInt);
        riverrank.setText(dealer.rankSymbol.get(riverRankInt).toString());
        riversuit.setText(dealer.suitSymbol.get(riverSuitInt).toString());
        turnsuit.setBackground(Color.white);
        gamestate = "river";
        updatePot(bot, player);
        botAnalyse(bot);
    }

    private void endRound(Bot bot, Dealer dealer, Player player){
        Comparison compareWinner = new Comparison(bot, player);
        if (compareWinner.compareHands() == "Bot 1 wins!"){
            bot.chips += pot;
            winningPlayer.setText("Big Brain wins with");
            winningHand.setText(bot.evaluator.typeOfBestHand());
        } else if (compareWinner.compareHands() == "Bot 2 wins!"){
            player.chips += pot;
            winningPlayer.setText("Player wins with");
            winningHand.setText(player.evaluator.typeOfBestHand());
        } else {
            bot.chips += pot/2;
            player.chips += pot/2;
        }
        updatePot(bot, player);
        String key1rankBot = bot.getHand().getHoldEm().get(0).rank.name();
        String key2rankBot = bot.getHand().getHoldEm().get(1).rank.name();
        String key1suitBot = bot.getHand().getHoldEm().get(0).suit.name();
        String key2suitBot = bot.getHand().getHoldEm().get(1).suit.name();
        colourCards(bot1rank, key1suitBot);
        colourCards(bot1suit, key1suitBot);
        colourCards(bot2rank, key2suitBot);
        colourCards(bot2suit, key2suitBot);
        bot1rank.setText(dealer.rankSymbol.get(key1rankBot).toString());
        bot1suit.setText(dealer.suitSymbol.get(key1suitBot).toString());
        bot2rank.setText(dealer.rankSymbol.get(key2rankBot).toString());
        bot2suit.setText(dealer.suitSymbol.get(key2suitBot).toString());
        bot1.setBackground(Color.white);
        bot2.setBackground(Color.white);
        nextRoundButton.setVisible(true);
    }

    public void reset(Bot bot, Dealer dealer, Player player) {
        bot1rank.setText("");
        bot2rank.setText("");
        player1rank.setText("");
        player2rank.setText("");
        bot1suit.setText("");
        bot2suit.setText("");
        player1suit.setText("");
        player2suit.setText("");
        flop1rank.setText("");
        flop2rank.setText("");
        flop3rank.setText("");
        flop1suit.setText("");
        flop2suit.setText("");
        flop3suit.setText("");
        turnsuit.setText("");
        turnrank.setText("");
        riverrank.setText("");
        riversuit.setText("");
        gamestate = "preflop";
        Play.setVisible(true);
        checkFold.setText("Check/Fold");
        call.setText("Call");
        checkFold.setVisible(false);
        smallRaise.setVisible(false);
        bigRaise.setVisible(false);
        call.setVisible(false);
        bot.status = "";
        bot.evaluator = new Evaluator();
        botStatus.setText("");
        bot.setHand(null);
        bot.handWeight = 0;
        dealer.deck = new Deck().createDeck();
        dealer.board = new ArrayList<Card>();
        player.setHand(null);
        pot = 0;
        winningHand.setText("");
        winningPlayer.setText("");
        updatePot(bot, player);
        bot1.setBackground(new Color(62,101,98));
        bot2.setBackground(new Color(62,101,98));
    }

    private void updatePot(Bot bot, Player player){
        potText.setText((Integer.toString(pot)));
        botChips.setText((Integer.toString(bot.chips)));
        playerChips.setText((Integer.toString(player.chips)));
    }

    private void colourCards(JLabel label, String labelText){
        if (labelText == "HEART" || labelText == "DIAMOND"){
            label.setForeground(Color.red);
        } else {
            label.setForeground(Color.black);
        }
    }
}