package com.guy.class22b_and_1;

public class GameManager {

    private int score = 0;
    private int lives = 3;

    public GameManager() {
    }

    public int getScore() {
        return score;
    }

    public void addToScore() {
        score += 1;
    }

    public int getLives() {
        return lives;
    }

    public void reduceLives() {
        lives--;
    }

    public boolean isDead() {
        return lives <= 0;
    }
}
