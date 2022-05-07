package com.guy.class22b_and_1;

import android.media.MediaPlayer;

enum SOUNDS {
    MEAT,
    BATTLE,
    HIT,
    MAIN_MENU,
    VICTORY
}

public class GameManager {

    private int score = 0;
    private int lives = 1;
    private MediaPlayer meat;
    private MediaPlayer mainMenu;
    private MediaPlayer battle;
    private MediaPlayer victory;
    private MediaPlayer hit;

    public GameManager() {
        meat = MediaPlayer.create(Activity_Game.instance.getApplicationContext(), R.raw.eat);
        meat.setVolume(0.5F, 0.5F);

        mainMenu = MediaPlayer.create(Activity_Game.instance.getApplicationContext(), R.raw.main_menu);
        mainMenu.setVolume(0.5F, 0.5F);

        battle = MediaPlayer.create(Activity_Game.instance.getApplicationContext(), R.raw.battle_music);
        battle.setVolume(0.2F, 0.2F);

        victory = MediaPlayer.create(Activity_Game.instance.getApplicationContext(), R.raw.win);
        victory.setVolume(0.3F, 0.3F);

        hit = MediaPlayer.create(Activity_Game.instance.getApplicationContext(), R.raw.stab);
        hit.setVolume(0.5F, 0.5F);
    }

    // gets the sound depending on the enum
    public MediaPlayer getSound(SOUNDS sound) {
        switch (sound) {
            case MEAT:
                return meat;
            case HIT:
                return hit;
            case MAIN_MENU:
                return mainMenu;
            case VICTORY:
                return victory;
            case BATTLE:
                return battle;
        }

        return null;
    }

    // plays sound depending on the enum
    public void playSound(SOUNDS sound, boolean isStart) {
        switch (sound) {
            case MEAT:
                playSound(meat, isStart);
                break;
            case HIT:
                playSound(hit, isStart);
                break;
            case MAIN_MENU:
                playSound(mainMenu, isStart);
                break;
            case VICTORY:
                playSound(victory, isStart);
                break;
            case BATTLE:
                playSound(battle, isStart);
                break;
        }
    }

    private void playSound(MediaPlayer mp, boolean isStart) {
        if (isStart) {
            if (mp.isPlaying())
                mp.stop();

            mp.start();
        } else
            mp.stop();
    }

    public int getScore() {
        return score;
    }

    public int addToScore(int scoreToAdd) {
        score += scoreToAdd;

        if (score <= 0)
            score = 0;

        return score;
    }

    public int getLives() {
        return lives;
    }

    public void reduceLives() {
        lives--;
        playSound(SOUNDS.HIT, true);
    }

    public boolean isDead() {
        return lives <= 0;
    }
}
