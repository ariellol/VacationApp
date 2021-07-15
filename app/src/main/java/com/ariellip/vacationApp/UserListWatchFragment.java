package com.ariellip.vacationApp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UserListWatchFragment extends Fragment implements View.OnClickListener {

    ListView userListView;
    ArrayList<User> users;
    UserAdapter userAdapter;

    LinearLayout expendListView;

    DatabaseReference dbRef;

    Dialog filterDialog;
    Spinner filterSpinner;
    Spinner orderSpinner;
    Button orderUsersButton;

    public static final int ORDER_BY_EMAIL = 0;
    public static final int ORDER_BY_NAME = 1;
    public static final int ORDER_BY_MANAGER = 2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.user_list_watch_fragment,container,false);

        Context context = getActivity();
        users = new ArrayList<>();
        userListView = parent.findViewById(R.id.users_list_view);
        userAdapter = new UserAdapter(context,users);
        userListView.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();

        dbRef = FirebaseDatabase.getInstance().getReference();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("טוען...");
        progressDialog.show();

        dbRef.child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnap : snapshot.getChildren()){
                    users.add(postSnap.getValue(User.class));
                    Log.d("userPermission",users.get(users.size()-1).getIsManager()+"");
                }
                progressDialog.dismiss();
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("firebaseError",error.getMessage()+ " " + error.getDetails());
                progressDialog.dismiss();
            }
        });

        filterDialog = new Dialog(context);
        filterDialog.setContentView(R.layout.filter_users_dialog);
        orderSpinner = filterDialog.findViewById(R.id.orderSpinner);
        String[] orderArray = getResources().getStringArray(R.array.order_spinner);
        ArrayAdapter<String> orderAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1,orderArray);
        orderSpinner.setAdapter(orderAdapter);
        orderUsersButton = filterDialog.findViewById(R.id.filterButton);
        orderUsersButton.setOnClickListener(this);

        return parent;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if (v.getId() == orderUsersButton.getId()){
            orderUsers(orderSpinner.getSelectedItemPosition());
            filterDialog.dismiss();
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.manager_menu,menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.filterItem){
            filterDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void orderUsers(int orderType){
        if(orderType == ORDER_BY_EMAIL) {
            Collections.sort(users, new Comparator<User>() {
                @Override
                public int compare(User user1, User user2) {
                    return user1.getEmail().compareToIgnoreCase(user2.getEmail());
                }
            });
        }
        else if(orderType == ORDER_BY_NAME){
            Collections.sort(users, new Comparator<User>() {
                @Override
                public int compare(User user1, User user2) {
                    String user1Name = user1.getFirstName() + user1.getLastName();
                    String user2Name = user2.getFirstName() + user2.getLastName();
                    return user1Name.compareToIgnoreCase(user2Name);
                }
            });
        }
        else {
            Collections.sort(users, new Comparator<User>() {
                @Override
                public int compare(User o1, User o2) {
                    return Boolean.compare(o1.getIsManager(),o2.getIsManager());
                }
            });
        }
        userAdapter.notifyDataSetChanged();
    }
}
