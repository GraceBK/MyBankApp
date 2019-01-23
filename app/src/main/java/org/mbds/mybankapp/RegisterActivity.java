package org.mbds.mybankapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.mbds.mybankapp.fragments.RegisterFragment;
import org.mbds.mybankapp.models.Comptes;

import java.util.Objects;

public class RegisterActivity extends FragmentActivity implements RegisterFragment.OnFragmentInteractionListener {

    private static final String TAG = "[REGISTER]";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseCarte;

    FrameLayout frameLayout;

    RegisterFragment phoneFragment = RegisterFragment.newInstance(1, "suivant", "");
    RegisterFragment passwordFragment = RegisterFragment.newInstance(2, "suivant", "");
    RegisterFragment nameFragment = RegisterFragment.newInstance(3, "suivant", "");
    RegisterFragment signUpFragment = RegisterFragment.newInstance(4, "Créer un compte", "");

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference("comptes");
        mDatabaseCarte = FirebaseDatabase.getInstance().getReference("cartes");
        // [END initialize_database_ref]

        sharedPref = getSharedPreferences(getString(R.string.pref_user), Context.MODE_PRIVATE);

        frameLayout = findViewById(R.id.register_fl);

        if (savedInstanceState == null) {
            showFragment(phoneFragment);
        }
    }

    private void createAccount() {
        String mail = sharedPref.getString(getString(R.string.pref_user_email), "");
        String password = sharedPref.getString(getString(R.string.pref_user_pass), "");
        final String firstName = sharedPref.getString(getString(R.string.pref_user_first_name), "");
        final String lastName = sharedPref.getString(getString(R.string.pref_user_last_name), "");

        Log.d(TAG, "{\nmail : "+ mail +"\npassword : "+ password +"\nfirstName : "+ firstName +"\nlastName : "+ lastName +"\n}");

        if (!Objects.equals(mail, "") && !Objects.equals(password, "") && !Objects.equals(firstName, "") && !Objects.equals(lastName, "")) {
            // [START create_user_with_email]
            assert mail != null;
            assert password != null;
            mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.w(TAG, "createUserWithEmail:success");
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(firstName + " " + lastName)
                                .build();

                        final FirebaseUser user = mAuth.getCurrentUser();


                        assert user != null;
                        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Comptes profile update",
                                            Toast.LENGTH_SHORT).show();
                                    // Creating new compte node, which returns the unique key value
                                    // new user node would be /comptes/$compteid/
                                    String userId = mDatabase.push().getKey();

                                    Comptes comptes = new Comptes(user.getUid(), userId, user.getDisplayName(), user.getEmail(), 0, 0);

                                    Log.w(TAG, ""+user.getDisplayName() +"  "+ user.getEmail());
                                    Log.w(TAG, "--> "+user.getUid()+"  "+ userId);


                                    assert userId != null;
                                    mDatabase.child(user.getUid()).setValue(comptes);


                                }
                            }
                        });

                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    } else {
                        Log.e(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        // TODO Update updateUI(null);
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                }
            });
            // [END create_user_with_email]
        }


    }

    @Override
    public void clickMailToPassword(Boolean b) {
        if (b) {
            showFragment(passwordFragment);
        }
    }

    @Override
    public void clickPasswordToName(Boolean b) {
        if (b) {
            showFragment(nameFragment);
        }
    }

    @Override
    public void clickNameToSignUp(Boolean b) {
        if (b) {
            showFragment(signUpFragment);
        }
    }

    @Override
    public void clickSignUp(Boolean b) {
        if (b) {
            Toast.makeText(getApplicationContext(), "WAIT...", Toast.LENGTH_LONG).show();

            createAccount();
        }
    }

    @Override
    public void setData(int idFrag, String value) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPref.edit();
        if (idFrag == 1) {
            editor.putString(getString(R.string.pref_user_email), value);
            editor.apply();
        } else if (idFrag == 2) {
            editor.putString(getString(R.string.pref_user_pass), value);
            editor.apply();
        }
    }

    @Override
    public void setData2(String value1, String value2) {
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.pref_user_first_name), value1);
        editor.putString(getString(R.string.pref_user_last_name), value2);
        editor.apply();
    }

    public void showFragment(RegisterFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(frameLayout.getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
