package com.ariellip.vacationApp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class personalAreaFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    TextView logoutButton;
    FirebaseUser currentUser;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseRef;
    EditText firstNameEt,lastNameEt,phoneEt;
    TextView mailTv;
    TextView updateMailTv;
    String uid;
    User user;
    Button openDialogPasswordChange;

    Dialog dialogUpdatePassword;
    EditText newPasswordEt, newRePasswordEt, currentPassword, mailToChangePassword;
    Button updatePassword;

    SharedPreferences userInfo;

    Toolbar toolbar;

    Dialog dialogUpdateMail;
    EditText currentMail;
    EditText passwordToChangeMailEt;
    EditText newMailEt;
    Button updateMailButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.personal_area_fragment,container,false);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseRef = FirebaseDatabase.getInstance().getReference();

        openDialogPasswordChange = parent.findViewById(R.id.changePasswordButton);
        firstNameEt = parent.findViewById(R.id.edit_firstName);
        lastNameEt = parent.findViewById(R.id.edit_lastName);
        phoneEt = parent.findViewById(R.id.edit_phone);
        mailTv = parent.findViewById(R.id.edit_mail);
        updateMailTv = parent.findViewById(R.id.updateMailTv);
        logoutButton = parent.findViewById(R.id.logOutButton);
        logoutButton.setOnClickListener(this);
        openDialogPasswordChange.setOnClickListener(this);
        updateMailTv.setOnClickListener(this);
        firstNameEt.setOnFocusChangeListener(this);
        lastNameEt.setOnFocusChangeListener(this);
        phoneEt.setOnFocusChangeListener(this);

        dialogUpdatePassword = new Dialog(getActivity());
        dialogUpdatePassword.setContentView(R.layout.change_password_dialog);
        newPasswordEt = dialogUpdatePassword.findViewById(R.id.updatePasswordEt);
        newRePasswordEt = dialogUpdatePassword.findViewById(R.id.updateRePasswordEt);
        updatePassword = dialogUpdatePassword.findViewById(R.id.changePasswordInDialog);
        currentPassword = dialogUpdatePassword.findViewById(R.id.currentPassword);
        mailToChangePassword = dialogUpdatePassword.findViewById(R.id.mailToChangePassword);
        dialogUpdatePassword.setCancelable(true);
        updatePassword.setOnClickListener(this);

        dialogUpdateMail = new Dialog(getActivity());
        dialogUpdateMail.setContentView(R.layout.dialog_update_mail);
        currentMail = dialogUpdateMail.findViewById(R.id.previousMailEt);
        passwordToChangeMailEt = dialogUpdateMail.findViewById(R.id.changeMailPasswordEt);
        newMailEt = dialogUpdateMail.findViewById(R.id.newMailEt);
        updateMailButton = dialogUpdateMail.findViewById(R.id.updateMailButton);
        updateMailButton.setOnClickListener(this);

        userInfo = getActivity().getSharedPreferences(
                getResources().getString(R.string.shared_preferences_name), Context.MODE_PRIVATE);

        mailTv.setText(userInfo.getString(getResources().getString(R.string.mail),""));
        firstNameEt.setText(userInfo.getString(getResources().getString(R.string.first_name),""));
        lastNameEt.setText(userInfo.getString(getResources().getString(R.string.last_name),""));
        phoneEt.setText(userInfo.getString(getResources().getString(R.string.phone_number),""));

        uid = currentUser.getUid();
        databaseRef.child("Users").child(uid).addValueEventListener(
        new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class); }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        toolbar = getActivity().findViewById(R.id.app_bar);
        setHasOptionsMenu(true);

        return parent;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == openDialogPasswordChange.getId())
            dialogUpdatePassword.show();

        else if(v.getId() == updateMailTv.getId()){
            dialogUpdateMail.show();
        }

        else if(v.getId() == updateMailButton.getId()){
            if (currentMail.getText().toString().equals("")){
                currentMail.setError("חובה למלא שדה זה");
                currentMail.setFocusable(true);
                return;
            }
            if (passwordToChangeMailEt.getText().toString().equals("")){
                passwordToChangeMailEt.setFocusable(true);
                passwordToChangeMailEt.setError("חובה למלא שדה זה");
                return;
            }
            if(newMailEt.getText().toString().equals("")){
                newMailEt.setError("חובה למלא שדה זה");
                newMailEt.setFocusable(true);
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            AuthCredential credential = EmailAuthProvider.getCredential(
                    currentMail.getText().toString(), passwordToChangeMailEt.getText().toString()); // Current Login Credentials

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    user.updateEmail(newMailEt.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                databaseReference.child("Users/"+user.getUid()).child("email").setValue(newMailEt.getText().toString());
                                Toast.makeText(getActivity(), "אימייל שונה בהצלחה!", Toast.LENGTH_SHORT).show();
                                dialogUpdateMail.dismiss();
                                SharedPreferences.Editor editor = userInfo.edit();
                                editor.putString(getResources().getString(R.string.mail),newMailEt.getText().toString());
                                mailTv.setText(newMailEt.getText().toString());
                                FirebaseAuth.getInstance().signOut();
                                Intent logoutIntent = new Intent(getActivity(),LoginActivity.class);
                                startActivity(logoutIntent);
                            }
                        }
                    });
                }
            });
        }

        else if(v.getId() == updatePassword.getId()){

            if (mailToChangePassword.getText().toString().equals("")){
                mailToChangePassword.setFocusable(true);
                mailToChangePassword.setError("חובה למלא שדה זה");
                return;
            }
            if (currentPassword.getText().toString().equals("")){
                currentPassword.setError("חובה למלא שדה זה");
                currentPassword.setFocusable(true);
                return;
            }
            if (newPasswordEt.getText().toString().equals("")){
                newPasswordEt.setFocusable(true);
                newPasswordEt.setError("חובה למלא שדה זה");
                return;
            }
            if (newRePasswordEt.getText().toString().equals("")){
                newRePasswordEt.setError("חובה למלא שדה זה");
                newRePasswordEt.setFocusable(true);
                return;
            }
            if (!newRePasswordEt.getText().toString().equals(newPasswordEt.getText().toString())){
                newRePasswordEt.setError("שדות אלה חייבים להיות תואמים");
                newPasswordEt.setError("שדות אלה חייבים להיות תואמים");
                return;
            }

            AuthCredential credential = EmailAuthProvider
                    .getCredential(mailToChangePassword.getText().toString().trim(),currentPassword.getText().toString());

            currentUser.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                currentUser.updatePassword(newPasswordEt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "סיסמה נשמרה בהצלחה", Toast.LENGTH_SHORT).show();
                                            dialogUpdatePassword.dismiss();
                                            mailTv.setText(newMailEt.getText().toString());
                                            FirebaseAuth.getInstance().signOut();
                                            Intent logoutIntent = new Intent(getActivity(),LoginActivity.class);
                                            startActivity(logoutIntent);
                                        } else {
                                            Toast.makeText(getActivity(), "שינוי סיסמה לא תקין", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getActivity(), "שינוי סיסמה לא תקין", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else if (v.getId() == logoutButton.getId()){
            FirebaseAuth.getInstance().signOut();
            Intent logoutIntent = new Intent(getActivity(),LoginActivity.class);
            startActivity(logoutIntent);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus)
            v.setBackgroundResource(R.drawable.edit_text_selected);
        else
            v.setBackgroundResource(R.drawable.edit_text_background);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.save_changes_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String firstName = firstNameEt.getText().toString().trim();
        String lastName = lastNameEt.getText().toString().trim();
        String phoneNumber = phoneEt.getText().toString();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users/"+uid).child(getResources().getString(R.string.first_name)).setValue(firstName);
        databaseReference.child("Users/"+uid).child(getResources().getString(R.string.last_name)).setValue(lastName);
        databaseReference.child("Users/"+uid).child(getResources().getString(R.string.phone_number)).setValue(phoneNumber);

        SharedPreferences.Editor editor = userInfo.edit();
        editor.putString(getResources().getString(R.string.first_name),firstName);
        editor.putString(getResources().getString(R.string.last_name),lastName);
        editor.putString(getResources().getString(R.string.phone_number),phoneNumber);
        editor.apply();
        Toast.makeText(getActivity(), "שינויים נשמרו בהצלחה!", Toast.LENGTH_SHORT).show();
        getActivity().onBackPressed();
        return super.onOptionsItemSelected(item);

    }
}
