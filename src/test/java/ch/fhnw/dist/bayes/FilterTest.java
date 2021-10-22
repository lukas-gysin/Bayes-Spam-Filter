package ch.fhnw.dist.bayes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterTest {

    /**
     * The word `ham` appears 12 times in all ham-mails
     */
    public static final double WORD_HAM_HAM = 12;
    /**
     * The word `hello` appears 1 times in all ham-mails
     */
    public static final double WORD_HAM_HELLO = 1;
    public static final double WORDS_HAM = WORD_HAM_HAM+WORD_HAM_HELLO;
    /**
     * The word `spam` appears 1 times in all spam-mails
     */
    public static final double WORD_SPAM_SPAM = 18;
    /**
     * The word `viagra` appears 1 times in all spam-mails
     */
    public static final double WORD_SPAM_VIAGRA = 1;
    public static final double WORDS_SPAM = WORD_SPAM_SPAM+WORD_SPAM_VIAGRA;

    public static final int ALPHA = 1;

    private Filter cut;

    @BeforeEach
    public void initEach(){
        cut = new Filter(ALPHA);
        cut.train("src/test/resources/ham-test.zip", "src/test/resources/spam-test.zip");
    }

    @Test
    public void hamFrequency_ham(){
        assertEquals(WORD_HAM_HAM/WORDS_HAM, cut.hamFrequency("ham"));
    }

    @Test
    public void hamFrequency_hello(){
        assertEquals(WORD_HAM_HELLO/WORDS_HAM, cut.hamFrequency("hello"));
    }

    @Test
    public void hamFrequency_spam(){
        assertEquals(ALPHA/WORDS_HAM, cut.hamFrequency("spam"));
    }

    @Test
    public void hamFrequency_unknown(){
        assertEquals(ALPHA/WORDS_HAM, cut.hamFrequency("test"));
    }

    @Test
    public void hamFrequency_viagra(){
        assertEquals(ALPHA/WORDS_HAM, cut.hamFrequency("viagra"));
    }

    @Test
    public void spamFrequency_ham(){
        assertEquals(ALPHA/WORDS_SPAM, cut.spamFrequency("ham"));
    }

    @Test
    public void spmFrequency_hello(){
        assertEquals(ALPHA/WORDS_SPAM, cut.spamFrequency("hello"));
    }

    @Test
    public void spmFrequency_spam(){
        assertEquals(WORD_SPAM_SPAM/WORDS_SPAM, cut.spamFrequency("spam"));
    }

    @Test
    public void spmFrequency_unknown(){
        assertEquals(ALPHA/WORDS_SPAM, cut.spamFrequency("test"));
    }

    @Test
    public void spamFrequency_viagra(){
        assertEquals(WORD_SPAM_VIAGRA/WORDS_SPAM, cut.spamFrequency("viagra"));
    }
}
