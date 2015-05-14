package myapps.courier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


public class OrderForm extends AppCompatActivity implements View.OnClickListener {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        Order order = (Order) getIntent().getParcelableExtra(Order.class.getCanonicalName());
        TextView orderName = (TextView)findViewById(R.id.orderName);
        orderName.setText(order.name);
    }

    @Override
    public void onClick(View v) {

    }
}
