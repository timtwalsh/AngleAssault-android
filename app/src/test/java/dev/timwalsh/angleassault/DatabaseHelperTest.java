package dev.timwalsh.angleassault;

import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class DatabaseHelperTest {
    private GameActivity testGameActivity;

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra("gameSpeed", 10); // include the expected intent
        testGameActivity = Robolectric.buildActivity(GameActivity.class, intent).create().resume().get();
    }

    @Test
    public void testLoadDatabase() {
        testGameActivity.loadData();
        assertEquals("angle_assault", testGameActivity.database.getDatabaseName());
    }

    @Test
    public void testAddHighScore() {
        testGameActivity.loadData();
        HighScoreItem score = new HighScoreItem();
        score.setName("Test");
        score.setScore(1);
        testGameActivity.database.addScore(score);
        assertEquals("Test", testGameActivity.database.getAllScores().get(0).getName());
    }

    @Test
    public void testGetScoresDesc() {
        testGameActivity.loadData();
        // Generate and add 15 high scores
        for (int i = 0; i < 15; i++) {
            char c = (char) (i % 26 + 'a');
            testGameActivity.database.addScore(new HighScoreItem(String.valueOf(c), i));
        }
        List<HighScoreItem> allScores = testGameActivity.database.getAllScores();
        assertEquals(15, allScores.size());
        assertEquals(0, allScores.get(0).getScore());
        List<HighScoreItem> allScoresDesc = testGameActivity.database.getHighScoresDesc();
        assertEquals(15, allScoresDesc.size());
        assertEquals(14, allScoresDesc.get(0).getScore()); // 0 (Top) score should be 14
    }
}
