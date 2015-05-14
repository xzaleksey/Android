package myapps.courier;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button btn_enter = (Button) findViewById(R.id.btn_enter);
        btn_enter.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0,1,0,"выход");
        MenuItem item = menu.getItem(0);
        SpannableString s = new SpannableString("Выход");
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        item.setTitle(s);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == 1) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enter:
                EditText eLogin = (EditText) findViewById(R.id.login);
                String login = eLogin.getText().toString();
                EditText ePassword = (EditText) findViewById(R.id.login);
//                Toast.makeText(this, eLogin.getText().toString() + " " + ePassword.getText().toString(), Toast.LENGTH_LONG).show();
                String password = eLogin.getText().toString();
                if (eLogin.getText().toString().trim().equals("admin") && ePassword.getText().toString().trim().equals("admin")) {
                    Toast.makeText(this, "Авторизация успешна!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainForm.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Неправильный логин или пароль!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
