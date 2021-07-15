package com.ariellip.vacationApp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class VacationListManager extends Fragment implements AdapterView.OnItemClickListener {

    ListView vacationListView;
    ArrayList<Vacation> vacations;
    VacationAdapter vacationAdapter;

    DatabaseReference dbRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.vacation_list_manager,container,false);

        vacations = new ArrayList<>();
        vacationListView = parent.findViewById(R.id.vacationListViewInManager);
        vacationAdapter = new VacationAdapter(getActivity(), vacations);
        vacationListView.setAdapter(vacationAdapter);

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

        setHasOptionsMenu(true);
        vacationListView.setOnItemClickListener(this);

        return parent;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_vacation_item){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddVacationFragment())
                    .addToBackStack(null).commit();
            ((Toolbar)getActivity().findViewById(R.id.app_bar)).setTitle("הוספת חבילת נופש");
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Vacation vacation = vacations.get(position);
        Bundle vacationBundle = new Bundle();
        vacationBundle.putSerializable("currentVacation",vacation);
        EditVacationFragment editVacationFragment = new EditVacationFragment();
        editVacationFragment.setArguments(vacationBundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,editVacationFragment)
                .addToBackStack(null).commit();
    }
}
