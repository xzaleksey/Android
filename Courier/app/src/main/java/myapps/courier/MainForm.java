package myapps.courier;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class MainForm extends AppCompatActivity implements View.OnClickListener {
    LinearLayout llMain;
    final int wrapContent = RelativeLayout.LayoutParams.WRAP_CONTENT;
    List<Order> orders = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //ищем лэйаут
        llMain = (LinearLayout) findViewById(R.id.llMain);
        DomFeedParser domFeedParser = new DomFeedParser(this);

        domFeedParser.parseXml2(orders);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, wrapContent);

        for (Order order : orders) {
            Button btnNew = new Button(this);
            btnNew.setId(order.index);
            btnNew.setTextColor(Color.WHITE);
            btnNew.setTextSize(16);
            btnNew.setText(order.index + " " + order.address + " " + order.time);
            btnNew.setOnClickListener(this);
            llMain.addView(btnNew, llParams);
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
        intent.putExtra(Order.class.getCanonicalName(), orders.get(index));
        startActivity(intent);
    }
}
