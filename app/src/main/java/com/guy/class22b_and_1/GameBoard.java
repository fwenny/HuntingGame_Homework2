package com.guy.class22b_and_1;

import android.graphics.Point;
import android.widget.ImageView;
import android.widget.LinearLayout;

enum DIRECTION {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

public class GameBoard {
    private Point charLocation;
    private Point enemyLocation;
    private Point meatLocation;

    private Point moveDir;
    private LinearLayout mainMatrix;

    public GameBoard(LinearLayout matrix) {
        mainMatrix = matrix;
        moveDir = new Point();
        charLocation = new Point();
        enemyLocation = new Point();
        meatLocation = new Point();
    }

    public Point getMeatLocation() {
        return meatLocation;
    }

    public GameBoard setMeatLocation(int x, int y) {
        this.meatLocation.set(x, y);
        return this;
    }

    public Point getCharLocation() {
        return charLocation;
    }

    public GameBoard setCharLocation(int x, int y) {
        this.charLocation.set(x, y);
        return this;
    }

    public Point getEnemyLocation() {
        return enemyLocation;
    }

    public GameBoard setEnemyLocation(int x, int y) {
        this.enemyLocation.set(x, y);
        return this;
    }

    public GameBoard setMoveDir(int x, int y) {
        this.moveDir.set(x, y);
        return this;
    }

    // moves characters and returns true if there was a collision
    public boolean moveCharacters() {
        // hide images on current positions
        getImageFromLoc(charLocation).setImageResource(0);
        getImageFromLoc(enemyLocation).setImageResource(0);

        // save old positions for later
        Point temp = new Point(moveDir);
        Point oldCharLocation = new Point(charLocation);
        Point oldEnemyLocation = new Point(enemyLocation);

        // move player
        charLocation.offset(temp.x, temp.y);

        // check bounds
        if (charLocation.x < 0)
            charLocation.x = HelperMethods.COLS - 1;
        else if (charLocation.x >= HelperMethods.COLS)
            charLocation.x = 0;

        if (charLocation.y < 0)
            charLocation.y = HelperMethods.ROWS - 1;
        else if (charLocation.y >= HelperMethods.ROWS)
            charLocation.y = 0;

        // randomize movement of enemy
        int randomX = HelperMethods.RANDOM.nextInt(3) - 1;
        int randomY = HelperMethods.RANDOM.nextInt(3) - 1;

        // move enemy
        enemyLocation.offset(randomX, randomY);

        // check bounds
        if (enemyLocation.x < 0)
            enemyLocation.x = 1;
        else if (enemyLocation.x >= HelperMethods.COLS)
            enemyLocation.x = HelperMethods.COLS - 1;

        if (enemyLocation.y < 0)
            enemyLocation.y = 1;
        else if (enemyLocation.y >= HelperMethods.ROWS)
            enemyLocation.y = HelperMethods.ROWS - 1;

        // check whether player and enemy are standing in the same spot, OR they cross each other
        // during movement (bump into each other) using old position values
        if (charLocation.equals(enemyLocation) ||
                (oldCharLocation.equals(enemyLocation) && oldEnemyLocation.equals(charLocation))) {
            return true;
        } else {
            // if theres no collision, set images in new location
            getImageFromLoc(charLocation).setImageResource(R.drawable.iv_hunter);
            getImageFromLoc(enemyLocation).setImageResource(R.drawable.iv_dino);

            if (!meatLocation.equals(charLocation) && !meatLocation.equals(enemyLocation))
                getImageFromLoc(meatLocation).setImageResource(R.drawable.iv_meat);

            // rotate images based on movement direction
            if (temp.x < 0)
                getImageFromLoc(charLocation).setScaleX(-1f);
            else
                getImageFromLoc(charLocation).setScaleX(1f);

            if (randomX < 0)
                getImageFromLoc(enemyLocation).setScaleX(-1f);
            else
                getImageFromLoc(enemyLocation).setScaleX(1f);

            return false;
        }
    }

    // checks collision with meat
    public boolean checkMeat() {
        if (charLocation.equals(meatLocation)) {
            initMeat();

            return true;
        }

        return false;
    }

    // gets image from location
    public ImageView getImageFromLoc(Point loc) {
        return (ImageView) ((LinearLayout) mainMatrix.getChildAt(loc.y)).getChildAt(loc.x);
    }

    // initializes location of meat so it doesnt spawn on top of player / enemy
    public void initMeat() {
        setMeatLocation(HelperMethods.RANDOM.nextInt(HelperMethods.COLS), HelperMethods.RANDOM.nextInt(HelperMethods.ROWS));

        while (charLocation.equals(meatLocation) || enemyLocation.equals(meatLocation))
            setMeatLocation(HelperMethods.RANDOM.nextInt(HelperMethods.COLS), HelperMethods.RANDOM.nextInt(HelperMethods.ROWS));
    }
}
