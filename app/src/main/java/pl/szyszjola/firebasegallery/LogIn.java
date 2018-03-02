package pl.szyszjola.firebasegallery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class LogIn extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 3;
    private static final String TAG = "Application";
    private static final String LOGIN = "LOGIN";
    private static final String KEY_ZALOGOWANY = "ZALOGOWANY";
    private static final String KEY_ZAPAMIETANY = "ZAPAMIETANY";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText tv_userName;
    private Boolean zalogowany, zapamietany;
    TextView tvPowitaly;
    SharedPreferences loginPreferences;
    SharedPreferences.Editor editor;
    Button signOutButton, buttonPrzejdz;
    SignInButton signInButton;
    TextView textView;
    ProgressBar spinner;
    CheckBox checkbox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        // Set the dimensions of the sign-in button.
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        textView = (TextView) signInButton.getChildAt(0);
        buttonPrzejdz = findViewById(R.id.buttonPrzejdz);
        spinner = findViewById(R.id.spinner);
        checkbox = findViewById(R.id.checkboxZapamietaj);

        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(this);
        tvPowitaly = findViewById(R.id.tvPowitalny);
        tv_userName = findViewById(R.id.user_name);

        mAuth = FirebaseAuth.getInstance();
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginPreferences = getSharedPreferences(LOGIN, MODE_PRIVATE);
        editor = loginPreferences.edit();

        zalogowany = loginPreferences.getBoolean(KEY_ZALOGOWANY, false);
        zapamietany = loginPreferences.getBoolean(KEY_ZAPAMIETANY, false);

        buttonPrzejdz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkbox.isChecked())
                {
                    editor.putBoolean(KEY_ZAPAMIETANY,checkbox.isChecked());
                }
                przejdzDoProgramu();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (zalogowany) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);

            if(zapamietany)
            {
                przejdzDoProgramu();
            }

        } else if (!zalogowany) {
            signIn();
        }
    }

    private void przejdzDoProgramu()
    {
        Intent intent = new Intent(this, RequestPermissionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    private void signIn() {
        if (textView.getText().toString().equals(getResources().getString(R.string.przelacz))) {
            signOut();
        }
        spinner.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(FirebaseUser a) {
        spinner.setVisibility(View.INVISIBLE);
        signInButton.setVisibility(View.VISIBLE);
        if (a != null) {
            buttonPrzejdz.setVisibility(View.VISIBLE);
            tv_userName.setVisibility(View.VISIBLE);
            tv_userName.setText(a.getEmail());
            tvPowitaly.setVisibility(View.VISIBLE);
            tvPowitaly.setText(R.string.zalogowany);
            textView.setText(R.string.przelacz);
        } else {
            tvPowitaly.setVisibility(View.INVISIBLE);
            tv_userName.setText("");
            tv_userName.setVisibility(View.INVISIBLE);
            textView.setText(R.string.zaloguj);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN && resultCode != RESULT_CANCELED) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        } else {
            spinner.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            zalogowany = true;
                            editor.putBoolean(KEY_ZALOGOWANY, zalogowany);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }


    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                buttonPrzejdz.setVisibility(View.INVISIBLE);
                zalogowany = false;
                editor.putBoolean(KEY_ZALOGOWANY, zalogowany);
                updateUI(null);
            }
        });
    }
}
