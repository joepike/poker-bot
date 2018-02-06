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

        deck.createDeck();
        deck.dealCards();

        Assert.assertEquals(2, bot.hand.size());
    }
}