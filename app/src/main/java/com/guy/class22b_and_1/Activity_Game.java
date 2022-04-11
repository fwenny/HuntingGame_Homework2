package com.guy.class22b_and_1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

import java.util.Random;

public class Activity_Game extends AppCompatActivity {

    private Point charLocation;
    private Point enemyLocation;
    private Point moveDir;

    private Random rand = new Random(123456);
    private Handler handler = new Handler();
    private int delay = 1000;
    private int timeUntilStart = 4;
    private int rows = 10;
    private int cols = 10;

    private GameManager gameManager;

    // UI elements
    private LinearLayout game_LL_mainMatrix;
    private ImageView[] game_IMG_hearts;
    private EditText game_et_rows;
    private EditText game_et_cols;
    private Button game_btn_submit;
    private Button game_btn_left;
    private Button game_btn_right;
    private Button game_btn_up;
    private Button game_btn_down;
    private TextView game_tv_timer;
    private MaterialTextView game_LBL_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_main_menu);
        findViews();

        game_btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rows = Integer.parseInt(game_et_rows.getText().toString());
                cols = Integer.parseInt(game_et_cols.getText().toString());

                setContentView(R.layout.activity_game);
                init(true);
            }
        });
    }

    private void init(boolean firstTime) {
        if (firstTime) {
            findViews();
            createMatrix();
            setupMovementButtons();
            gameManager = new GameManager();
            moveDir = new Point();

            handler.postDelayed(new Runnable() {
                public void run() {

                    if (timeUntilStart != 1) {                        timeUntilStart--;
                        game_tv_timer.setText(getString(R.string.tv_go) + "\n" + timeUntilStart);
                        game_tv_timer.setVisibility(View.VISIBLE);
                    } else {
                        game_tv_timer.setVisibility(View.INVISIBLE);
                        updateUnitLoc();
                    }

                    if (!gameManager.isDead())
                        handler.postDelayed(this, delay);
                }
            }, delay);
        }

        updateUI();
        charLocation = new Point(rand.nextInt(cols), 0);
        enemyLocation = new Point(rand.nextInt(cols), rows - 1);

        setImageResource(getImageFromLoc(charLocation), R.drawable.iv_hunter);
        setImageResource(getImageFromLoc(enemyLocation), R.drawable.iv_dino);
    }

    private void setupMovementButtons() {
        game_btn_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveDir.set(-1, 0);
                game_btn_left.setTextScaleX(1);

                game_btn_right.setTextScaleX(0);
                game_btn_up.setTextScaleX(0);
                game_btn_down.setTextScaleX(0);
                ;
            }
        });

        game_btn_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveDir.set(1, 0);
                game_btn_right.setTextScaleX(1);

                game_btn_left.setTextScaleX(0);
                game_btn_up.setTextScaleX(0);
                game_btn_down.setTextScaleX(0);
            }
        });

        game_btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveDir.set(0, -1);
                game_btn_up.setTextScaleX(1);

                game_btn_right.setTextScaleX(0);
                game_btn_left.setTextScaleX(0);
                game_btn_down.setTextScaleX(0);
            }
        });

        game_btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveDir.set(0, 1);
                game_btn_down.setTextScaleX(1);

                game_btn_right.setTextScaleX(0);
                game_btn_up.setTextScaleX(0);
                game_btn_left.setTextScaleX(0);
            }
        });
    }

    private void setImageResource(ImageView img, int resource) {
        img.setImageResource(resource);
    }

    private ImageView getImageFromLoc(Point loc) {
        return (ImageView) ((LinearLayout) game_LL_mainMatrix.getChildAt(loc.y)).getChildAt(loc.x);
    }

    private void updateUnitLoc() {
        int score = Integer.parseInt(game_LBL_score.getText().toString());
        score++;
        game_LBL_score.setText(score + "");

        setImageResource(getImageFromLoc(charLocation), 0);
        setImageResource(getImageFromLoc(enemyLocation), 0);

        Point temp = new Point(moveDir);
        Point oldCharLocation = new Point(charLocation);
        Point oldEnemyLocation = new Point(enemyLocation);

        charLocation.offset(temp.x, temp.y);

        if (charLocation.x < 0)
            charLocation.x = cols - 1;
        else if (charLocation.x >= cols)
            charLocation.x = 0;

        if (charLocation.y < 0)
            charLocation.y = rows - 1;
        else if (charLocation.y >= rows)
            charLocation.y = 0;

        int randomX = rand.nextInt(3) - 1;
        int randomY = rand.nextInt(3) - 1;

        enemyLocation.offset(randomX, randomY);

        if (enemyLocation.x < 0)
            enemyLocation.x = 1;
        else if (enemyLocation.x >= cols)
            enemyLocation.x = cols - 1;

        if (enemyLocation.y < 0)
            enemyLocation.y = 1;
        else if (enemyLocation.y >= rows)
            enemyLocation.y = rows - 1;

        if (charLocation.equals(enemyLocation) || (oldCharLocation.equals(enemyLocation) && oldEnemyLocation.equals(charLocation))) {
            gameManager.reduceLives();

            if (gameManager.isDead()) {
                finishGame();
                return;
            } else {
                while (enemyLocation.equals(charLocation))
                    enemyLocation.set(rand.nextInt(cols), rand.nextInt(rows));

                timeUntilStart = 4;

                init(false);
            }
        } else {
            setImageResource(getImageFromLoc(charLocation), R.drawable.iv_hunter);
            setImageResource(getImageFromLoc(enemyLocation), R.drawable.iv_dino);

            if (temp.x < 0)
                getImageFromLoc(charLocation).setScaleX(-1f);
            else
                getImageFromLoc(charLocation).setScaleX(1f);

            if (randomX < 0)
                getImageFromLoc(enemyLocation).setScaleX(-1f);
            else
                getImageFromLoc(enemyLocation).setScaleX(1f);
        }
    }

    private void checkBounds(Point loc) {
        if (loc.x < 0)
            loc.x = cols - 1;
        else if (loc.x >= cols)
            loc.x = 0;

        if (loc.y < 0)
            loc.y = rows - 1;
        else if (loc.y >= rows)
            loc.y = 0;
    }

    private void createMatrix() {
        LinearLayout.LayoutParams linearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout.LayoutParams imageViewParams = new LinearLayout.LayoutParams(0, 100);
        imageViewParams.weight = 1;

        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(linearLayoutParams);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(15, 15, 15, 15);


            for (int colIndex = 0; colIndex < cols; colIndex++) {
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(imageViewParams);
//                iv.setImageResource(R.drawable.flag_au);
                iv.setBackgroundResource(R.drawable.iv_shape);

                layout.addView(iv);
            }

            game_LL_mainMatrix.addView(layout);
        }
    }

    private void updateUI() {
        for (int i = 0; i < game_IMG_hearts.length; i++) {
            game_IMG_hearts[i].setVisibility(gameManager.getLives() > i ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void finishGame() {
        handler.removeCallbacksAndMessages(null);
        Toast.makeText(this, game_LBL_score.getText() + " seconds!", Toast.LENGTH_LONG).show();
        finish();
    }

    private void findViews() {
        game_IMG_hearts = new ImageView[]{
                findViewById(R.id.game_IMG_heart1),
                findViewById(R.id.game_IMG_heart2),
                findViewById(R.id.game_IMG_heart3)
        };

        game_LL_mainMatrix = findViewById(R.id.game_LL_mainMatrix);

        game_btn_submit = findViewById(R.id.game_btn_submit);
        game_et_cols = findViewById(R.id.game_et_cols);
        game_et_rows = findViewById(R.id.game_et_rows);

        game_btn_left = findViewById(R.id.game_btn_left);
        game_btn_right = findViewById(R.id.game_btn_right);
        game_btn_up = findViewById(R.id.game_btn_up);
        game_btn_down = findViewById(R.id.game_btn_down);
        game_tv_timer = findViewById(R.id.game_tv_timer);
        game_LBL_score = findViewById(R.id.game_LBL_score);
    }
}