package com.guy.class22b_and_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;

import java.util.Calendar;
import java.util.Dictionary;

public class Activity_Game extends AppCompatActivity {

    // helper properties
    private int timeUntilStart = 4;

    // helper classes
    private GameManager gameManager;
    private GameBoard gameBoard;
    private SensorsManager sensorsManger;

    private SharedPreferences prefs;
    private Dictionary<String, String> records;

    // UI elements
    private LinearLayout game_LL_mainMatrix;
    private ImageView[] game_IMG_hearts;
    private Button game_btn_submit;
    private Button game_btn_records;
    private Button game_btn_submit_sensors;
    private Button game_btn_left;
    private Button game_btn_right;
    private Button game_btn_up;
    private Button game_btn_down;
    private TextView game_tv_timer;
    private MaterialTextView game_LBL_score;
    private Button game_btn_exit;
    private TextView game_tv_records;
    private TextView game_tv_records_label;
    private Button game_btn_back;
    private TextView game_tv_title;

    // main activity instance
    public static Activity_Game instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // save instance for later use
        instance = this;

        prefs = this.getSharedPreferences("com.my.app", Context.MODE_PRIVATE);

        gameManager = new GameManager();

        if (!gameManager.getSound(SOUNDS.MAIN_MENU).isPlaying())
            gameManager.playSound(SOUNDS.MAIN_MENU, true);

        setContentView(R.layout.game_main_menu);
        findViews();
        setupClickEvents();
    }

    private void setupClickEvents() {
        // start button to switch to game screen
        game_btn_submit.setOnClickListener(view -> {
            // switch to game screen and init values
            gameManager.playSound(SOUNDS.MAIN_MENU, false);

            setContentView(R.layout.activity_game);
            init(true);
        });

        game_btn_submit_sensors.setOnClickListener(view -> {
            // init sensors, then switch to game screen and init values
            sensorsManger = new SensorsManager();
            sensorsManger.init();

            game_btn_submit.callOnClick();
        });

        game_btn_records.setOnClickListener(view -> {
            game_tv_records_label.setVisibility(View.VISIBLE);
            game_tv_records.setVisibility(View.VISIBLE);
            game_btn_back.setVisibility(View.VISIBLE);
            game_tv_title.setVisibility(View.GONE);
            game_btn_submit.setVisibility(View.GONE);
            game_btn_submit_sensors.setVisibility(View.GONE);
            game_btn_records.setVisibility(View.GONE);

            String recordsKey = "com.my.app.records";
            String recordsString = prefs.getString(recordsKey, "");

            game_tv_records.setText(recordsString);

            game_btn_back.setOnClickListener(view2 -> {
                game_tv_records_label.setVisibility(View.GONE);
                game_tv_records.setVisibility(View.GONE);
                game_btn_back.setVisibility(View.GONE);
                game_tv_title.setVisibility(View.VISIBLE);
                game_btn_submit.setVisibility(View.VISIBLE);
                game_btn_submit_sensors.setVisibility(View.VISIBLE);
                game_btn_records.setVisibility(View.VISIBLE);
            });
        });
    }

    // initializes the board on game start, or restarts the position of characters on a new round
    private void init(boolean firstTime) {
        if (firstTime) {
            findViews();

            game_btn_exit.setOnClickListener(view -> {
                gameManager.playSound(SOUNDS.VICTORY, false);
                finish();
            });

            setupMovementButtons();
            createMatrix();
            gameBoard = new GameBoard(game_LL_mainMatrix);

            // timer to check when to hide the text and start the movement
            HelperMethods.TIMER.postDelayed(new Runnable() {
                public void run() {

                    if (timeUntilStart != 1) {
                        timeUntilStart--;
                        game_tv_timer.setText(getString(R.string.tv_go) + "\n" + timeUntilStart);
                        game_tv_timer.setVisibility(View.VISIBLE);
                    } else {
                        if (!gameManager.getSound(SOUNDS.BATTLE).isPlaying())
                            gameManager.playSound(SOUNDS.BATTLE, true);

                        game_tv_timer.setVisibility(View.INVISIBLE);
                        updateUnitLoc();
                    }

                    // keep timer running on delay as long as enemy isn't dead
                    if (!gameManager.isDead())
                        HelperMethods.TIMER.postDelayed(this, HelperMethods.DELAY);
                }
            }, HelperMethods.DELAY);
        }

        updateUI();

        // remove existing meat before round reset
        gameBoard.getImageFromLoc(gameBoard.getMeatLocation()).setImageResource(0);

        gameBoard.setCharLocation(HelperMethods.RANDOM.nextInt(HelperMethods.COLS), 0);
        gameBoard.setEnemyLocation(HelperMethods.RANDOM.nextInt(HelperMethods.COLS), HelperMethods.ROWS - 1);
        gameBoard.initMeat();

        gameBoard.getImageFromLoc(gameBoard.getCharLocation()).setImageResource(R.drawable.iv_hunter);
        gameBoard.getImageFromLoc(gameBoard.getEnemyLocation()).setImageResource(R.drawable.iv_dino);
        gameBoard.getImageFromLoc(gameBoard.getMeatLocation()).setImageResource(R.drawable.iv_meat);
    }

    // only show button content of the default move direction
    // disable buttons if sensor gameplay is enabled
    private void setupMovementButtons() {
        if (sensorsManger != null) {
            game_btn_left.setEnabled(false);
            game_btn_right.setEnabled(false);
            game_btn_up.setEnabled(false);
            game_btn_down.setEnabled(false);
        }

        game_btn_left.setOnClickListener(view -> changeMoveDirection(DIRECTION.LEFT));

        game_btn_right.setOnClickListener(view -> changeMoveDirection(DIRECTION.RIGHT));

        game_btn_up.setOnClickListener(view -> changeMoveDirection(DIRECTION.UP));

        game_btn_down.setOnClickListener(view -> changeMoveDirection(DIRECTION.DOWN));
    }

    // handle movement of player and enemy
    private void updateUnitLoc() {
        // increase time by one
        game_LBL_score.setText(gameManager.addToScore(1) + "");

        // if there was a collision
        if (gameBoard.moveCharacters()) {
            // reduce enemy life
            gameManager.reduceLives();

            // check if enemy is dead and finish game
            if (gameManager.isDead()) {
                finishGame();
            } else {
                // randomize enemy location for next round
                while (gameBoard.getEnemyLocation().equals(gameBoard.getCharLocation()))
                    gameBoard.getEnemyLocation().set(HelperMethods.RANDOM.nextInt(HelperMethods.COLS), HelperMethods.RANDOM.nextInt(HelperMethods.ROWS));

                // reset round timer and call init to reset positions
                timeUntilStart = 4;

                init(false);
            }
        }

        // if player collected meat
        if (gameBoard.checkMeat()) {
            game_LBL_score.setText(gameManager.addToScore(HelperMethods.MEAT_VALUE) + "");
            gameManager.playSound(SOUNDS.MEAT, true);
        }
    }

    // updates lives in UI
    private void updateUI() {
        for (int i = 0; i < game_IMG_hearts.length; i++) {
            game_IMG_hearts[i].setVisibility(gameManager.getLives() > i ? View.VISIBLE : View.INVISIBLE);
        }
    }

    // stop timer, show toast with score and end app
    private void finishGame() {
        HelperMethods.TIMER.removeCallbacksAndMessages(null);
        game_tv_timer.setText(getString(R.string.toast_you_win));
        game_tv_timer.setVisibility(View.VISIBLE);
        Toast.makeText(this, gameManager.getScore() + " seconds!", Toast.LENGTH_LONG).show();
//        finish();
        gameManager.playSound(SOUNDS.BATTLE, false);
        gameManager.playSound(SOUNDS.VICTORY, true);
        game_LL_mainMatrix.setVisibility(View.GONE);
        game_btn_exit.setVisibility(View.VISIBLE);

        String recordsKey = "com.my.app.records";
        String recordsString = prefs.getString(recordsKey, "");

        Calendar cal = Calendar.getInstance();

        String month = String.format("%02d", cal.get(Calendar.MONTH));
        String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
        String year = String.format("%02d", cal.get(Calendar.YEAR));

        recordsString += "\n" + day + ":" + month + ":" + year + " " + gameManager.getScore();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(recordsKey, recordsString);
        editor.apply();
    }

    // dynamically create rows and cols of the game board
    public void createMatrix() {
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(0, 100);
        imageViewParams.weight = 1;

        for (int rowIndex = 0; rowIndex < HelperMethods.ROWS; rowIndex++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(linearLayoutParams);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(15, 15, 15, 15);

            for (int colIndex = 0; colIndex < HelperMethods.COLS; colIndex++) {
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(imageViewParams);
                iv.setBackgroundResource(R.drawable.iv_shape);

                layout.addView(iv);
            }

            game_LL_mainMatrix.addView(layout);
        }
    }

    // gets references to all the UI objects
    private void findViews() {
        game_IMG_hearts = new ImageView[]{
                findViewById(R.id.game_IMG_heart1),
                findViewById(R.id.game_IMG_heart2),
                findViewById(R.id.game_IMG_heart3)
        };

        game_LL_mainMatrix = findViewById(R.id.game_LL_mainMatrix);


        game_btn_left = findViewById(R.id.game_btn_left);
        game_btn_right = findViewById(R.id.game_btn_right);
        game_btn_up = findViewById(R.id.game_btn_up);
        game_btn_down = findViewById(R.id.game_btn_down);
        game_tv_timer = findViewById(R.id.game_tv_timer);
        game_LBL_score = findViewById(R.id.game_LBL_score);
        game_btn_exit = findViewById(R.id.game_btn_exit);
        game_tv_records_label = findViewById(R.id.game_tv_records_label);

        game_btn_submit = findViewById(R.id.game_btn_submit);
        game_btn_submit_sensors = findViewById(R.id.game_btn_submit_sensors);
        game_btn_records = findViewById(R.id.game_btn_records);
        game_btn_back = findViewById(R.id.game_btn_back);
        game_tv_records = findViewById(R.id.game_tv_records);
        game_tv_title = findViewById(R.id.game_tv_title);
    }

    // puts dot on the active button and sets move direction depending on the enum
    public void changeMoveDirection(DIRECTION dir) {
        switch (dir) {
            case UP:
                gameBoard.setMoveDir(0, -1);
                game_btn_up.setTextScaleX(1);

                game_btn_right.setTextScaleX(0);
                game_btn_left.setTextScaleX(0);
                game_btn_down.setTextScaleX(0);
                break;
            case DOWN:
                gameBoard.setMoveDir(0, 1);
                game_btn_down.setTextScaleX(1);

                game_btn_right.setTextScaleX(0);
                game_btn_up.setTextScaleX(0);
                game_btn_left.setTextScaleX(0);
                break;
            case LEFT:
                gameBoard.setMoveDir(-1, 0);
                game_btn_left.setTextScaleX(1);

                game_btn_right.setTextScaleX(0);
                game_btn_up.setTextScaleX(0);
                game_btn_down.setTextScaleX(0);
                break;
            case RIGHT:
                gameBoard.setMoveDir(1, 0);
                game_btn_right.setTextScaleX(1);

                game_btn_left.setTextScaleX(0);
                game_btn_up.setTextScaleX(0);
                game_btn_down.setTextScaleX(0);
                break;
        }
    }
}