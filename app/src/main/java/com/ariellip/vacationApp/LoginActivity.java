package com.ariellip.vacationApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    TextView dontHaveUserTv;
    TextView forgotPasswordTv;
    EditText mailEt;
    EditText passwordEt;
    Button loginButton;

    FirebaseAuth userLoginAuth;
    Intent homeIntent;
    SharedPreferences userInfo;

    Dialog dialogForgotPassword;
    Button resetPasswordButton;
    EditText mailToSendPassword;

    CardView googleSignIn;

    GoogleSignInOptions gso;
    GoogleSignInClient client;

    public static final int RC_SIGN_IN = 0;

    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userLoginAuth = FirebaseAuth.getInstance();

        dontHaveUserTv = findViewById(R.id.noUserTv);
        forgotPasswordTv = findViewById(R.id.forgotPassowrd);
        mailEt = findViewById(R.id.mailEtLogin);
        passwordEt = findViewById(R.id.rePasswordEtLogin);
        loginButton = findViewById(R.id.loginButton);
        googleSignIn = findViewById(R.id.google_sign_in_button);
        googleSignIn.setOnClickListener(this);

        mailEt.setOnFocusChangeListener(this);
        passwordEt.setOnFocusChangeListener(this);
        dontHaveUserTv.setOnClickListener(this);
        forgotPasswordTv.setOnClickListener(this);
        loginButton.setOnClickListener(this);

        homeIntent = new Intent(LoginActivity.this,HomeActivity.class);
        if (userLoginAuth.getCurrentUser() != null){

            startActivity(homeIntent);
            finish();
        }

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, gso);

        dialogForgotPassword = new Dialog(this);
        dialogForgotPassword.setContentView(R.layout.dialog_forgot_password);
        resetPasswordButton = dialogForgotPassword.findViewById(R.id.resetPasswordButton);
        mailToSendPassword = dialogForgotPassword.findViewById(R.id.email_forgot_password);
        resetPasswordButton.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.noUserTv:
                Intent registerIntent = new Intent(this,RegisterActivity.class);
                startActivity(registerIntent);
                break;
            case R.id.forgotPassowrd:
                dialogForgotPassword.show();
                break;
            case R.id.google_sign_in_button:

            case R.id.resetPasswordButton:
                if (mailToSendPassword.getText().toString().equals("")){
                    mailToSendPassword.setError("חובה למלא שדה זה");
                    mailToSendPassword.setFocusable(true);

                }
                else{

                    FirebaseAuth.getInstance().sendPasswordResetEmail(mailToSendPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "מייל לשחזור הסיסמה נשלח", Toast.LENGTH_SHORT).show();
                                        dialogForgotPassword.dismiss();
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "כתובת מייל לא תקינה.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;
            case R.id.loginButton:
                if (mailEt.getText().toString().equals("")){
                    mailEt.setError("חובה למלא שדה זה");
                    mailEt.setFocusable(true);
                    return;
                }
                if (passwordEt.getText().toString().equals("")){
                    passwordEt.setError("חובה למלא שדה זה");
                    passwordEt.setFocusable(true);
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("מתחבר...");
                progressDialog.show();

                String mail = mailEt.getText().toString().trim();
                String password = passwordEt.getText().toString();

                userLoginAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            progressDialog.dismiss();
                            FirebaseUser user = userLoginAuth.getCurrentUser();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                            ref.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User loggingUser = snapshot.getValue(User.class);

                                    userInfo = getSharedPreferences(
                                            getResources().getString(R.string.shared_preferences_name),MODE_PRIVATE);
                                    SharedPreferences.Editor editor = userInfo.edit();
                                    editor.putString(getResources().getString(R.string.first_name),loggingUser.getFirstName());
                                    editor.putString(getResources().getString(R.string.last_name),loggingUser.getLastName());
                                    editor.putString(getResources().getString(R.string.phone_number),loggingUser.getPhoneNumber());
                                    editor.putString(getResources().getString(R.string.mail),loggingUser.getEmail());
                                    editor.putBoolean(getResources().getString(R.string.isManager),loggingUser.getIsManager());
                                    editor.apply();

                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("user",loggingUser);
                                    homeIntent.putExtra("user",bundle);
                                    startActivity(homeIntent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    if (error.getMessage().equals("auth/invalid-email"))
                                        Toast.makeText(LoginActivity.this, "כתובת מייל לא תקינה", Toast.LENGTH_SHORT).show();

                                    if (error.getMessage().equals("auth/invalid-password"))
                                        Toast.makeText(LoginActivity.this, "סיסמה לא תקינה", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus)
            v.setBackgroundResource(R.drawable.edit_text_selected);
        else
            v.setBackgroundResource(R.drawable.edit_text_background);
    }



}