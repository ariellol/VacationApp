package com.ariellip.vacationApp;

import android.app.ProgressDialog;
import android.icu.util.HebrewCalendar;
import android.icu.util.ULocale;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;


public class HomeFragment extends Fragment implements View.OnClickListener {

    ListView vacationListView;
    ArrayList<Vacation> vacations;
    VacationAdapter vacationAdapter;

    DatabaseReference dbRef;
    FirebaseUser currentUser;
    StorageReference storageReference;
    String uid;

    Button openDialogPickDateButton;
    Button openFilterDialogButton;

    HebrewCalendar calendar;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.home_fragment, container, false);

        dbRef = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        vacations = new ArrayList<>();
        vacationListView = parent.findViewById(R.id.packagesListView);
        vacationAdapter = new VacationAdapter(getActivity(), vacations);
        vacationListView.setAdapter(vacationAdapter);

        openDialogPickDateButton = parent.findViewById(R.id.open_date_picker_button);
        openDialogPickDateButton.setOnClickListener(this);
        openFilterDialogButton = parent.findViewById(R.id.open_room_guest_picker);
        openFilterDialogButton.setOnClickListener(this);

        calendar = new HebrewCalendar();

        dbRef = FirebaseDatabase.getInstance().getReference();
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("מתחבר...");
        progressDialog.show();
        dbRef.child("Vacations").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    vacations.add(postSnap.getValue(Vacation.class));
                }
                progressDialog.dismiss();
                vacationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dbRef.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(User.class).getIsManager()){
                    setHasOptionsMenu(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        vacationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vacation vacation = vacations.get(position);
                Bundle vacationBundle = new Bundle();
                vacationBundle.putSerializable("currentVacation",vacation);
                VacationPackageFragment vacationFragment = new VacationPackageFragment();
                vacationFragment.setArguments(vacationBundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,vacationFragment)
                        .addToBackStack(null).commit();
            }
        });
        return parent;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.open_date_picker_button){
            pickDatesBottomSheet bottomSheet = new pickDatesBottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(),"pickDate");
        }
        else if (v.getId() == R.id.open_room_guest_picker){
            FilterPackagesBottomSheet bottomSheet = new FilterPackagesBottomSheet();
            bottomSheet.show(getActivity().getSupportFragmentManager(),"filter");
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.admin_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.managerPageButton){

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ManagerFragment())
                    .addToBackStack(null).commit();
        }
        return true;
    }
}
