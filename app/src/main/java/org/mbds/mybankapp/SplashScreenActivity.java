package org.mbds.mybankapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SplashScreenActivity extends Activity {

    private static final int AUTO_WAIT_DELAY_MILLIS = 1000;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        String sharedText = null;

        if (Objects.equals(action, "fr.mbds.bankapp.TRANSACTION") && type != null) {
            if ("text/plain".equals(type)) {
                //handleSendText(intent); // Handle text being sent
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (sharedText != null) {
                    sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.pref_pay), sharedText);
                    editor.apply();
                    Toast.makeText(getApplicationContext(), sharedText, Toast.LENGTH_LONG).show();
                }
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }


        final String finalSharedText = sharedText;

        mHideHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null) {
                    // Comptes is signed in
                    Intent iGoToMain = new Intent(SplashScreenActivity.this, MainActivity.class);
                    iGoToMain.putExtra("EXTRA_PAY_VALUE", finalSharedText);
                    startActivity(iGoToMain);
                } else {
                    // No user is signed in
                    Intent iGoToLogin = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    iGoToLogin.putExtra("EXTRA_PAY_VALUE", finalSharedText);
                    startActivity(iGoToLogin);
                }
                finish();
            }
        }, AUTO_WAIT_DELAY_MILLIS);
    }
}
