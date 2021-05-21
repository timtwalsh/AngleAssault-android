package dev.timwalsh.angleassault;

import android.content.Context;

import java.util.Random;

public class AngleGame {
    // Game
    public int question;
    public int score = 0;

    // Data
    public String[] questions;
    public double[][] answers = {{0.10, 89.90}, {88.00, 92.00}, {90.10, 179.90}, {178.00, 182.00}, {180.10, 360.00}, {178.00, 182.00}, {88.00, 92.00}, {268.00, 272.00}};
    Random rand = new Random();

    AngleGame(Context appContext) {
        // pulled from res for localisation purposes
        questions = new String[]{
                appContext.getResources().getString(R.string.question_acute),
                appContext.getResources().getString(R.string.question_right),
                appContext.getResources().getString(R.string.question_obtuse),
                appContext.getResources().getString(R.string.question_straight),
                appContext.getResources().getString(R.string.question_reflex),
                appContext.getResources().getString(R.string.question_pi_radians),
                appContext.getResources().getString(R.string.question_half_pi_radians),
                appContext.getResources().getString(R.string.question_three_pi_on_two_radians)
        };
    }

    public void newQuestion() {
        int oldQuestion = question;
        question = rand.nextInt(questions.length);
        while (question == oldQuestion) // Ensure new question is selected each time
            question = rand.nextInt(questions.length);
    }

    public String getQuestion() {
        return questions[question];
    }

    public void resetGame() {
        score = 0;
    }

    public boolean checkAnswer(double sensorAngle) {
        double answer_min = answers[question][0];
        double answer_max = answers[question][1];
        return sensorAngle >= answer_min && sensorAngle <= answer_max;
    }

    public void winRound(float roundTime, float maxGameSpeed) {
        int newPoints = 1 + (int) Math.floor(maxGameSpeed - roundTime); // eg. 10-gameSpeed
        score += newPoints;
    }

    public int getScore() {
        return score;
    }

}