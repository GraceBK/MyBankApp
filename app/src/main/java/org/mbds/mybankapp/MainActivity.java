package org.mbds.mybankapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.mbds.mybankapp.models.Comptes;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "[HOME]";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("comptes");

    String PAY_VALUE = "";

    private SharedPreferences sharedPref;


    private LinearLayout btnAdd;
    private LinearLayout btnGoPay;
    private TextView tvUsername;
    private TextView tvMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUsername = findViewById(R.id.profile_username);
        tvMoney = findViewById(R.id.profile_solde);
        tvUsername.setText(Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());

        final float[] solde = new float[1];

        Query query = mDatabase.child(mAuth.getCurrentUser().getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Comptes compte = dataSnapshot.getValue(Comptes.class);
                    Log.i("-------", ""+compte);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (Objects.equals(snapshot.getKey(), "beforeMoney")) {
                            Log.i("-------****", ""+snapshot.getValue());
                        }
                        assert compte != null;
                        solde[0] = compte.money;
                        tvMoney.setText(compte.money+"");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnAdd = findViewById(R.id.left_btn);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LoadCreditActivity.class));
            }
        });

        btnGoPay = findViewById(R.id.btn_go_pay);
        btnGoPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PayActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_logout:
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
