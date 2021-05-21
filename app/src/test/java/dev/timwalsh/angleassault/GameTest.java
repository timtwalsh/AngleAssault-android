package dev.timwalsh.angleassault;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class GameTest {
    private GameActivity testGameActivity;

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra("gameSpeed", 10); // include the expected intent
        testGameActivity = Robolectric.buildActivity( GameActivity.class, intent ).create().resume().get();
    }

    // getQuestion returns a string
    @Test
    public void testGetQuestion() {
        AngleGame testGame = new AngleGame(testGameActivity.getBaseContext());
        String questionText;
        questionText = testGame.getQuestion();
        assertNotNull(questionText);
    }

    // newQuestion changes the question
    // and questions should never repeat
    @Test
    public void testNoRepeatQuestions() {
        AngleGame testGame = new AngleGame(testGameActivity.getBaseContext());
        String questionText;
        String questionTextTwo;
        int testCount = 0;
        while (testCount < 100) {
            questionText = testGame.getQuestion();
            assertNotNull(questionText);
            testGame.newQuestion();
            questionTextTwo = testGame.getQuestion();
            assertNotEquals(questionText, questionTextTwo);
            testCount++;
        }
    }

    // Test answer checking
    @Test
    public void testCheckAnswer() {
        AngleGame testGame = new AngleGame(testGameActivity.getBaseContext());
        testGame.question = 0; // Acute (0.1 - 89.9)
        assertTrue(testGame.checkAnswer(10.0f));
    }

    // Test score and getScore
    public void testGetScore() {
        AngleGame testGame = new AngleGame(testGameActivity.getBaseContext());
        assertEquals(0, testGame.getScore());
        testGame.winRound(10,10);
        assertEquals(1, testGame.getScore());
    }

    public void testWinRound() {
        AngleGame testGame = new AngleGame(testGameActivity.getBaseContext());
        testGame.winRound(10, 10); // 1+10-10=1
        assertEquals(1, testGame.getScore()); // 0+1 = 1
        testGame.winRound(5, 10); // 1+10-5=6
        assertEquals(7, testGame.getScore()); // 1+6 = 7
    }
}