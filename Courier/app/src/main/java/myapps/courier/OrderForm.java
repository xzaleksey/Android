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
        TextView orderAddress = (TextView)findViewById(R.id.orderAddress);
        TextView orderPhone = (TextView)findViewById(R.id.orderPhone);
        TextView orderTime = (TextView)findViewById(R.id.orderTime);
        TextView orderProduct = (TextView)findViewById(R.id.orderProduct);
        TextView orderComments = (TextView)findViewById(R.id.orderComments);

        orderAddress.setText(order.address);
        orderName.setText(order.name);
        orderPhone.setText("Телефон: " +order.phone);
        orderTime.setText(order.time);
        orderProduct.setText(order.product);
        orderComments.setText(order.comments);


    }

    @Override
    public void onClick(View v) {

    }
}
