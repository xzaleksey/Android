package myapps.courier;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class OrderForm extends AppCompatActivity implements View.OnClickListener {
    Button btnMap;
    TextView orderAddress;
    Button btnSms;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        Order order = getIntent().getParcelableExtra(Order.class.getCanonicalName());
        TextView orderName = (TextView) findViewById(R.id.orderName);
        orderAddress = (TextView) findViewById(R.id.orderAddress);
        TextView orderPhone = (TextView) findViewById(R.id.orderPhone);
        TextView orderTime = (TextView) findViewById(R.id.orderTime);
        TextView orderProduct = (TextView) findViewById(R.id.orderProduct);
        TextView orderComments = (TextView) findViewById(R.id.orderComments);

        orderAddress.setText(order.address);
        orderName.setText(order.name);
        orderPhone.setText("Телефон: " + order.phone);
        orderTime.setText(order.time);
        orderProduct.setText("Состав заказа: " + order.product);
        orderComments.setText(order.comments);
        btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(this);
        btnSms = (Button) findViewById(R.id.btnSms);
        btnSms.setOnClickListener(this);

    }

    void output(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMap:
                String address = orderAddress.getText().toString();
                address = address.replace(" ", "+");
                Intent geoIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address)); // Prepare intent
                startActivity(geoIntent); // Initiate lookup
                break;
            case R.id.btnSms:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Отправить сообщение");
                // alert.setMessage("Message");


                final EditText input = new EditText(this);
                input.setTextColor(Color.WHITE);
                alert.setView(input);

                alert.setPositiveButton("Отправить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        output("Отправлено!");
                        // Do something with value!
                    }
                });

                alert.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        output("Отмена");
                    }
                });
                alert.show();
                break;
        }
    }
}
