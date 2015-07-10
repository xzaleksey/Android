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

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.AnalyticsConfig;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;
import com.amazonaws.regions.Regions;


public class Login extends AppCompatActivity implements View.OnClickListener {
    private static MobileAnalyticsManager analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(
                this.getApplicationContext(),
                "COGNITO_IDENTITY_POOL",
                Regions.US_EAST_1
        );
        try {
            AnalyticsConfig config = new AnalyticsConfig();
            config.withAllowsWANDelivery(true);
            analytics = MobileAnalyticsManager.getOrCreateInstance(
                    this.getApplicationContext(),
                    "324db7e4dd16494ba9a2219528ac0b33", //Amazon Mobile Analytics App ID
                    Regions.US_EAST_1,
                    cognitoProvider,
                    config);
        } catch (InitializationException ex) {
            //Failed to initialize Amazon Mobile Analytics
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button btn_enter = (Button) findViewById(R.id.btn_enter);
        Button btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_enter.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(0, 1, 0, "выход");
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
            this.finish();
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
            case R.id.btn_exit:
                this.finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (analytics != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (analytics != null) {
            analytics.getSessionClient().resumeSession();
        }
    }
    @Override
    public void onBackPressed() {
        this.finish();
    }
}
