package c.zju.jianshu.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import c.zju.jianshu.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TypefaceUtil.replaceFont(this, "fonts/default.otf");

        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,WindowManager.LayoutParams. FLAG_FULLSCREEN);

        boolean flag;
        SharedPreferences userSettings = getSharedPreferences("setting", 0);
        String mode = userSettings.getString("mode", null);
        final String username = userSettings.getString("username", null);
        final String password = userSettings.getString("password", null);
        if (mode == null) {
            userSettings.edit().putString("mode", "login").commit();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent mainIntent = new Intent(SplashActivity.this, SignupActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }, 2000);
        } else {
            if (mode.equals("login")) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent mainIntent = new Intent(SplashActivity.this, SignupActivity.class);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    }
                }, 2000);
            } else if (mode.equals("auto-login")) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                        mainIntent.putExtra("username", username);
                        mainIntent.putExtra("password", password);
                        SplashActivity.this.startActivity(mainIntent);
                        SplashActivity.this.finish();
                    }
                }, 2000);
            }
        }


    }
}
