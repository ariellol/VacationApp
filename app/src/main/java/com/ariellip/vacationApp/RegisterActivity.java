package com.ariellip.vacationApp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    EditText firstNameEt, lastNameEt, phoneNumberEt, emailEt, passwordEt, rePasswordEt;
    Button registerButton;
    FirebaseAuth newUserAuth;
    DatabaseReference databaseRef;
    SharedPreferences userInfo;
    boolean userExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        newUserAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        firstNameEt = findViewById(R.id.firstNameEt);
        lastNameEt = findViewById(R.id.lastNameEt);
        phoneNumberEt = findViewById(R.id.phoneNumberEt);
        emailEt = findViewById(R.id.mailEt);
        passwordEt = findViewById(R.id.passowrdEt);
        rePasswordEt = findViewById(R.id.rePasswordEt);
        registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        firstNameEt.setOnFocusChangeListener(this);
        lastNameEt.setOnFocusChangeListener(this);
        phoneNumberEt.setOnFocusChangeListener(this);
        emailEt.setOnFocusChangeListener(this);
        passwordEt.setOnFocusChangeListener(this);
        rePasswordEt.setOnFocusChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == registerButton.getId()) {
            if (validateInput()) {

                String firstName = firstNameEt.getText().toString().trim();
                String lastName = lastNameEt.getText().toString().trim();
                String email = emailEt.getText().toString().trim();
                String phoneNumber = phoneNumberEt.getText().toString().trim();
                String password = passwordEt.getText().toString();
                User newUser = new User(firstName, lastName, email, phoneNumber, false);

                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("מתחבר...");
                progressDialog.show();


                if (userExists == true){
                    Toast.makeText(this, "מייל זה קיים במערכת", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    return;
                }

                newUserAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressDialog.dismiss();
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            String uid = reference.getKey();

                           reference.setValue(newUser)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                reference.child("uid").setValue(uid);
                                                userInfo = getSharedPreferences(
                                                        getResources().getString(R.string.shared_preferences_name),MODE_PRIVATE);
                                                SharedPreferences.Editor editor = userInfo.edit();
                                                editor.putString(getResources().getString(R.string.first_name),firstName);
                                                editor.putString(getResources().getString(R.string.last_name),lastName);
                                                editor.putString(getResources().getString(R.string.phone_number),phoneNumber);
                                                editor.putString(getResources().getString(R.string.mail),email);
                                                editor.apply();

                                                Intent homeIntent = new Intent(RegisterActivity.this,HomeActivity.class);

                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("user",newUser);
                                                homeIntent.putExtra("user",bundle);
                                                startActivity(homeIntent);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(RegisterActivity.this, "משהו השתבש..", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "משהו השתבש..", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        }
    }

    private boolean validateInput() {
        if (firstNameEt.getText().toString().equals("")) {
            firstNameEt.setFocusable(true);
            firstNameEt.setError("חובה למלא שדה זה");
            return false;
        } else if (lastNameEt.getText().toString().equals("")) {
            lastNameEt.setFocusable(true);
            lastNameEt.setError("חובה למלא שדה זה");
            return false;
        } else if (emailEt.getText().toString().equals("")) {
            emailEt.setFocusable(true);
            emailEt.setError("חובה למלא שדה זה");
            return false;
        } else if (phoneNumberEt.getText().toString().equals("")) {
            phoneNumberEt.setFocusable(true);
            phoneNumberEt.setError("חובה למלא שדה זה");
            return false;
        } else if (passwordEt.getText().toString().equals("")) {
            passwordEt.setFocusable(true);
            passwordEt.setError("חובה למלא שדה זה");
            return false;
        }
        if (rePasswordEt.getText().toString().equals("")) {
            rePasswordEt.setFocusable(true);
            rePasswordEt.setError("חובה למלא שדה זה");
            return false;
        }
        if (!passwordEt.getText().toString().equals(rePasswordEt.getText().toString())) {
            passwordEt.setFocusable(true);
            rePasswordEt.setError("שדות הסיסמה חייבות להיות תואמות");
            rePasswordEt.setFocusable(true);
            passwordEt.setError("שדות הסיסמה חייבות להיות תואמות");
            return false;
        }
        return true;
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus)
            v.setBackgroundResource(R.drawable.edit_text_selected);
        else
            v.setBackgroundResource(R.drawable.edit_text_background);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void isUserExists(String email){
        databaseRef.child("Users")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            if (ds.child("email").getValue().toString().equals(email)){
                                userExists = true;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}