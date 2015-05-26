package myapps.courier;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainForm extends AppCompatActivity implements View.OnClickListener {
    final int wrapContent = RelativeLayout.LayoutParams.WRAP_CONTENT;
    LinearLayout llMain;
    List<Order> orders = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
        try {
            Order neworder = getIntent().getParcelableExtra(Order.class.getCanonicalName());
            orders.set(neworder.index - 1, neworder);
            DateFormat dateFormat = new SimpleDateFormat("HH");
            for (Order order : orders) {
                if (order.completed.length() > 0) {
                    Button btn = (Button) findViewById(order.index);
                    btn.setBackgroundColor(Color.WHITE);
                    btn.setEnabled(false);
                }
            }
        } catch (Exception e) {

        }

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //ищем лэйаут
        llMain = (LinearLayout) findViewById(R.id.llMain);
        DomFeedParser domFeedParser = new DomFeedParser(this);

        domFeedParser.parseXml2(orders);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, wrapContent);
        llParams.bottomMargin = 5;
        DateFormat dateFormat = new SimpleDateFormat("HH");

        int hours = Integer.parseInt(dateFormat.format(new Date()));
        for (Order order : orders) {
            if (order.completed.equals("")) {


                Button btnNew = new Button(this);
                int endHour = Integer.parseInt(order.time.substring(9, 11));
                if (hours >= endHour)
                    btnNew.setBackgroundColor(Color.rgb(168, 0, 0));
                else if (hours + 2 >= endHour) {
                    btnNew.setBackgroundColor(Color.RED);
                } else if (hours + 4 >= endHour) {
                    btnNew.setBackgroundColor(Color.YELLOW);
                } else if (hours + 6 >= endHour) {
                    btnNew.setBackgroundColor(Color.GREEN);
                } else {
                    btnNew.setBackgroundColor(Color.WHITE);
                }
                btnNew.setShadowLayer(9, 1, 1, Color.rgb(68, 68, 68));

                btnNew.setPadding(5, 5, 5, 5);
                btnNew.setId(order.index);
                btnNew.setTextColor(Color.BLACK);
                btnNew.setTextSize(16);
                btnNew.setText(order.index + " " + order.address + " " + order.time);
                btnNew.setOnClickListener(this);
                llMain.addView(btnNew, llParams);
            }
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, OrderForm.class);
        int index = v.getId();
//        intent.putExtra("index", index);
//        intent.putExtra("address", orders.get(index).address);
//        intent.putExtra("comments", orders.get(index).comments);
//        intent.putExtra("address", orders.get(index).address);
//        intent.putExtra("address", orders.get(index).address);
//        intent.putExtra("address", orders.get(index).address);
        intent.putExtra(Order.class.getCanonicalName(), orders.get(index - 1));
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
