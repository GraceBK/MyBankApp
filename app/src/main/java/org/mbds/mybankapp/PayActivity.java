package org.mbds.mybankapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.mbds.mybankapp.models.Comptes;

import java.util.Objects;

import static android.nfc.NdefRecord.createMime;

public class PayActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    private SharedPreferences sharedPref;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("comptes");

    NfcAdapter mNfcAdapter;
    TextView textView;
    // EditText input;
    // Button save;
    // TextView tv_beam;

    // RelativeLayout relativeLayout;

    String money;

    private static final int MESSAGE_SENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
        money = sharedPref.getString(getString(R.string.pref_pay), "0");

        textView = findViewById(R.id.tv_beam);
        // relativeLayout = findViewById(R.id.relative_layout_add);
        /* input = findViewById(R.id.edt_pay);
        save = findViewById(R.id.btn_pay);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Objects.equals(input.getText().toString(), "")) {
                    sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.pref_pay), input.getText().toString());
                    editor.apply();
                    money = sharedPref.getString(getString(R.string.pref_pay), "0");
                }
                input.setText("");
            }
        });*/

        sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);

        textView.setText(sharedPref.getFloat(getString(R.string.pref_pay), 0) + "");

        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "Please enable NFC via Settings", Toast.LENGTH_LONG).show();
        }
        // Register callback
        mNfcAdapter.setNdefPushMessageCallback(this, this);

    }


    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = (money);

        sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(getString(R.string.pref_pay));
        editor.apply();

        Query query = mDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Comptes compte = dataSnapshot.getValue(Comptes.class);
                    assert compte != null;
                    compte.sub(Float.valueOf(money));
                    mDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("money").setValue(compte.money);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return new NdefMessage(
                new NdefRecord[] { createMime("pay/me", text.getBytes())
                });
    }

    /*@Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("Beam me up, Android!");
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMime(
                        "application/vnd.com.example.android.beam", text.getBytes())
                        **
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                        *
                        //,NdefRecord.createApplicationRecord("com.example.android.beam")
                });
        return msg;
    }*/

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent) {
        textView = findViewById(R.id.tv_beam);
        // relativeLayout.setVisibility(View.GONE);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        textView.setText(new String(msg.getRecords()[0].getPayload()));


        if (!textView.getText().toString().equals("")) {
            // TODO : call firebase et save

            Query query = mDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Comptes compte = dataSnapshot.getValue(Comptes.class);
                        assert compte != null;
                        compte.add(Float.valueOf(textView.getText().toString()));
                        mDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("money").setValue(compte.money);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Handler handlerWait = new Handler();
            Runnable runnableWait = new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(PayActivity.this, MainActivity.class));
                    finish();
                }
            };
            handlerWait.postDelayed(runnableWait, 1000);
        }
    }
}
