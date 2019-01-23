package org.mbds.mybankapp;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import org.mbds.mybankapp.models.Comptes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoadCreditActivity extends AppCompatActivity {

    private static final String TAG = "[ADD CREDIT]";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("comptes");
    private DatabaseReference mDatabaseCarte = FirebaseDatabase.getInstance().getReference("cartes");
    private FirebaseFunctions mFunctions = FirebaseFunctions.getInstance();

    private LinearLayout linearLayout;
    private LinearLayout linearLayout2;
    private TextView msg;
    private Button btn;

    private String money;
    private String idTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_credit);

        linearLayout = findViewById(R.id.help_img);
        //linearLayout.setVisibility(View.GONE);
        linearLayout2 = findViewById(R.id.help_img_validate);
        //linearLayout2.setVisibility(View.VISIBLE);
        msg = findViewById(R.id.see_creadit);
        btn = findViewById(R.id.valide_add);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!msg.getText().toString().equals("")) {
                    // TODO : call firebase et save

                    Query query = mDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Comptes compte = dataSnapshot.getValue(Comptes.class);
                                assert compte != null;
                                compte.add(Float.valueOf(money));
                                mDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("money").setValue(compte.money);
                                mDatabaseCarte.child("carte"+idTag).setValue(money);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    startActivity(new Intent(LoadCreditActivity.this, MainActivity.class));
                    finish();
                }
            }
        });

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            // TODO Change image
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
                // Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                NdefRecord record = messages[0].getRecords()[0];
                byte[] payload = record.getPayload();
                String text = new String(payload);
                //Log.w(TAG, text);

                String delimiter = "\\[\\|\\]\\s*";

                String[] params = text.split(delimiter);


                //mDatabaseCarte.orderByKey().equalTo()


                if (params.length > 1) {
                    money = params[1];
                    idTag = params[0];

                    Log.w(TAG, text);

                    mDatabaseCarte.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.e("--------", snapshot.getKey()+"");
                                if (Objects.equals(snapshot.getKey(), "carte" + idTag)) {
                                    // TODO : tag deja utilisé
                                    Toast.makeText(getApplicationContext(), "TAG non valide", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(LoadCreditActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    linearLayout.setVisibility(View.GONE);
                                    linearLayout2.setVisibility(View.VISIBLE);
                                    msg.setText("Ajout de " + money + " £");
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    linearLayout.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "TAG non valide", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LoadCreditActivity.this, MainActivity.class));
        finish();
    }
}
