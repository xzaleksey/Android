package com.valyakinaleksey.simple2048;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout relativeLayout;
    private GestureDetector gestureDetector;
    private static Game game;
    private static final int[] IDS = new int[]{R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4, R.id.tv5, R.id.tv6, R.id.tv7, R.id.tv8, R.id.tv9, R.id.tv10, R.id.tv11, R.id.tv12, R.id.tv13, R.id.tv14, R.id.tv15, R.id.tv16};
    private TextView[] textViews = new TextView[16];
    private TextView tvResult;
    static Map<CharSequence, Integer> COLORS = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = (RelativeLayout) findViewById(R.id.rlMain);
        tvResult = (TextView) findViewById(R.id.tvResult);
        initializeField(getParam());
        if (null == game) {
            game = new Game();

            if (COLORS.size() == 0) {
                String[] strings = getResources().getStringArray(R.array.values);
                int[] colors = getResources().getIntArray(R.array.colors);
                for (int i = 0; i < strings.length; i++) {
                    COLORS.put(strings[i], colors[i]);
                }
                createNewCell();
            }


        } else {
            restoreValues();
        }
        game.setUpdater(new Updater(textViews));
        initializeOnTouchListener();
    }

    private void restoreValues() {
        for (int i = 0; i < IDS.length; i++) {
            textViews[i].setText(game.getCellValue(i));
            setBgColor(textViews[i], textViews[i].getText());
        }
        tvResult.setText(game.getResult());
    }

    private void createNewCell() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(1000);
        TextView textView = textViews[game.newCell()];
        textView.setText("2");
        setBgColor(textView, "2");
        textView.startAnimation(alphaAnimation);
    }

    public class Updater {// не работает(
        TextView[] textViews;

        public Updater(TextView[] textViews) {
            this.textViews = textViews;
        }

        public void updateMoved(ArrayList<Integer> indexes) { //не работает обновление на экране сразу происходит
            for (Integer id : indexes) {
                CharSequence value = game.getCellValue(id);
                textViews[id].setText(value);


                if (COLORS.containsKey(value)) {
                    setBgColor(textViews[id], value);
                }
                textViews[id].requestLayout();
            }
            try {
                Thread.sleep(20);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    private void setBgColor(TextView textView, CharSequence value) {
        Drawable background = textView.getBackground();
        background.setColorFilter(COLORS.get(value), PorterDuff.Mode.SRC_IN);
    }

    // Задает ширину и высоту TextView
    private void initializeField(int param) {
        for (int i = 0; i < IDS.length; i++) {
            textViews[i] = (TextView) findViewById(IDS[i]);
            textViews[i].setWidth(param);
            textViews[i].setHeight(param);
        }
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.llResult);
        linearLayout.setMinimumWidth(param);
        linearLayout.setMinimumHeight(param*2);
        linearLayout.requestLayout();
    }

    //Получает параметр Ширины и высоты TextView с учетом рахмера экрана
    private int getParam() {
        int param;
        int mActionBarSize = (int) this.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize}).getDimension(0, 0);
        DisplayMetrics metricsB = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metricsB);
//        showToast("" + mActionBarSize);
        int height = metricsB.heightPixels - mActionBarSize - 68;
        int width = metricsB.widthPixels;
        param = height > width ? width : height;
        return (int) (param * 0.2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Для рестарта
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.restart) {
            game.resetGame();
            restoreValues();
            createNewCell();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Инициализирует обработку жестов
    private void initializeOnTouchListener() {
        gestureDetector = new GestureDetector(this, new MyGestureListener());
        View.OnTouchListener gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        relativeLayout.setOnTouchListener(gestureListener);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float x1 = e1.getX(), x2 = e2.getX(), y1 = e1.getY(), y2 = e2.getY();
            float difX = Math.abs(x2 - x1), difY = Math.abs(y2 - y1);
            boolean movesMade;
            if (difX > difY) {
                if (x2 - x1 > 0) {
                    movesMade = game.moveRight();
                } else
                    movesMade = game.moveLeft();
            } else {
                if (y2 - y1 > 0) {
                    movesMade = game.moveDown();

                } else
                    movesMade = game.moveUp();
            }
            if (movesMade) {
//                restoreValues();

                    tvResult.setText(game.getResult());
                if (game.getFreePositionsCount()>0)
                    createNewCell();
                   if (game.checkEndGame()){
                       tvResult.setText("Проиграл");
                   }

            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private void showToast(CharSequence charSequence) {
        Toast.makeText(this, charSequence, Toast.LENGTH_SHORT).show();
    }
}
