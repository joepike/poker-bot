import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BotTest {
    Deck deck;
    @Before
    public void initialize() {
        deck = new Deck();
    }
    @Test
    public void botCanHoldTwoCards() {

        Bot bot = new Bot();
        Dealer dealer = new Dealer();
        dealer.dealCards(bot);

        Assert.assertEquals(2, bot.hand.size());
    }

    @Test
    public void calculatingStartingHandWeight() {
        Bot bot = new Bot();
        Card card1 = new Card(Rank.TWO, Suit.SPADE);
        Card card2 = new Card(Rank.THREE, Suit.CLUB);
        bot.hand.add(card1);
        bot.hand.add(card2);
        bot.assignCards(bot.hand);
        Assert.assertEquals(10, bot.weighHand());
    }
    @Test
    public void weighingTwoHighCards() {
        Bot bot = new Bot();
        Card card1 = new Card(Rank.ACE, Suit.SPADE);
        Card card2 = new Card(Rank.JACK, Suit.CLUB);
        bot.hand.add(card1);
        bot.hand.add(card2);
        bot.assignCards(bot.hand);
        Assert.assertEquals(35, bot.weighHand());
    }
    @Test
    public void comparingTwoConsecutiveCards() {
        Bot bot = new Bot();
        Card card1 = new Card(Rank.EIGHT, Suit.SPADE);
        Card card2 = new Card(Rank.NINE, Suit.CLUB);
        bot.hand.add(card1);
        bot.hand.add(card2);
        bot.assignCards(bot.hand);
        Assert.assertEquals(22, bot.weighHand());
    }
}