package com.ariellip.vacationApp;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> implements View.OnClickListener {

    Context context;
    ArrayList<User> users;
    CheckBox isManagerCheckBox;
    Dialog changeManagerStateDialog;
    TextView isManagerMessageDialog;

    CompoundButton currentCheckbox;
    User currentUser;
    Button cancel;
    Button changeState;

    public UserAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.context = context;
        this.users = users;

        changeManagerStateDialog = new Dialog(context);
        changeManagerStateDialog.setContentView(R.layout.dialog_is_mamager);
        isManagerMessageDialog = changeManagerStateDialog.findViewById(R.id.are_you_sure_manager);
        cancel = changeManagerStateDialog.findViewById(R.id.cancel_manager_state);
        changeState = changeManagerStateDialog.findViewById(R.id.change_manager_state);


    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        UserViewHolder userHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.user_in_list_view, parent, false);
            userHolder = createViewHolderFrom(convertView);
            convertView.setTag(userHolder);
        } else {
            userHolder = (UserViewHolder) convertView.getTag();
        }
        User user = users.get(position);

        userHolder.fullNameTv.setText(user.getFirstName() + " " + user.getLastName());
        userHolder.mailTv.setText(user.getEmail());
        userHolder.phoneNumberTv.setText(user.getPhoneNumber());
        if (user.getIsManager()){
            userHolder.isManagerCheckBox.setChecked(true);
        }
        else{
            userHolder.isManagerCheckBox.setChecked(false);
        }


        cancel.setOnClickListener(this);
        changeState.setOnClickListener(this);

        userHolder.isManagerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentCheckbox = buttonView;
                currentUser = users.get(position);
                if (isChecked) {
                    isManagerMessageDialog.setText("האם אתה בטוח שאתה רוצה שמשתמש זה יהיה מנהל?");

                }
                else {
                    isManagerMessageDialog.setText("האם אתה בטוח שאתה רוצה להסיר את הרשאת המנהל ממשתמש זה?");
                }

                changeManagerStateDialog.show();
            }
        });

        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_manager_state) {
            if (currentCheckbox.isChecked()) {
                currentCheckbox.setChecked(false);
            }
            else {
                currentCheckbox.setChecked(true);
            }

        }
        else if (v.getId() == R.id.change_manager_state) {
            boolean isManagerChecked = currentCheckbox.isChecked();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("Users").child(currentUser.getUid()).child("isManager").setValue(isManagerChecked);
            this.clear();
            this.notifyDataSetChanged();
        }
        changeManagerStateDialog.dismiss();
    }

    private UserViewHolder createViewHolderFrom(View convertView) {
        TextView fullNameTv = convertView.findViewById(R.id.full_name_in_adapter);
        TextView mailTv = convertView.findViewById(R.id.mail_in_adapter);
        TextView phoneNumberTv = convertView.findViewById(R.id.phone_in_adapter);
        CheckBox checkBox = convertView.findViewById(R.id.isManagerCheckBox);
        return new UserViewHolder(fullNameTv, mailTv, phoneNumberTv, checkBox);
    }

    private static class UserViewHolder {
        TextView fullNameTv;
        TextView mailTv;
        TextView phoneNumberTv;
        CheckBox isManagerCheckBox;

        public UserViewHolder(TextView fullNameTv, TextView mailTv, TextView phoneNumberTv, CheckBox isManagerCheckBox) {
            this.fullNameTv = fullNameTv;
            this.mailTv = mailTv;
            this.phoneNumberTv = phoneNumberTv;
            this.isManagerCheckBox = isManagerCheckBox;
        }
    }
}
