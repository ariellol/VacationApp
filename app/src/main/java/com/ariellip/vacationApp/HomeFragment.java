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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.LongFunction;


public class HomeFragment extends Fragment implements View.OnClickListener, datePicked, FilterPick {

    ListView vacationListView;
    ArrayList<Vacation> vacations;
    VacationAdapter vacationAdapter;

    FirebaseUser currentUser;
    StorageReference storageReference;
    String uid;

    Button openDialogPickDateButton;
    Button openFilterDialogButton;
    Button searchFilteredVacations;

    String startDate;
    String endDate;

    int guests;
    int rooms;

    ArrayList<Vacation> availableVacations;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.home_fragment, container, false);


        vacationListView = parent.findViewById(R.id.packagesListView);

        openDialogPickDateButton = parent.findViewById(R.id.open_date_picker_button);
        openDialogPickDateButton.setOnClickListener(this);
        openFilterDialogButton = parent.findViewById(R.id.open_room_guest_picker);
        openFilterDialogButton.setOnClickListener(this);
        searchFilteredVacations = parent.findViewById(R.id.filterSearchButton);
        searchFilteredVacations.setOnClickListener(this);

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("??????????...");
        progressDialog.show();


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        storageReference = FirebaseStorage.getInstance().getReference();


        vacations = new ArrayList<>();


        DatabaseReference vacationRef = FirebaseDatabase.getInstance().getReference("Vacations");
        vacationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnap : snapshot.getChildren()) {
                    Vacation vacation = postSnap.getValue(Vacation.class);
                    vacations.add(vacation);
                }
                DatabaseReference takenDatesRef = FirebaseDatabase.getInstance().getReference();
                takenDatesRef.child("takenDates").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> takenDates = new ArrayList<>();
                        for (int i = 0; i< vacations.size(); i ++){
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.getKey().equals(vacations.get(i).getUid())) {
                                    for (DataSnapshot snap : dataSnapshot.getChildren()){
                                        takenDates.add(snap.child("takenDate").getValue(String.class));
                                    }
                                }
                            }
                            vacations.get(i).setTakenDates(takenDates);
                        }
                        if (getActivity()!=null) {
                            vacationAdapter = new VacationAdapter(getActivity(), vacations);
                            vacationListView.setAdapter(vacationAdapter);
                            filterAllVacations();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                vacationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (!(vacations.size() <=position)) {
                            Vacation vacation = vacations.get(position);
                            Bundle vacationBundle = new Bundle();
                            vacationBundle.putSerializable("currentVacation", vacation);
                            vacationBundle.putString("startDate", startDate);
                            vacationBundle.putString("endDate", endDate);
                            VacationPackageFragment vacationFragment = new VacationPackageFragment();
                            vacationFragment.setArguments(vacationBundle);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, vacationFragment)
                                    .addToBackStack(null).commit();
                        }
                    }
                });
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                startDate = dateFormat.format(pickDatesBottomSheet.getNearestWeekStart().getTime());
                endDate = dateFormat.format(pickDatesBottomSheet.getNearestWeekEnd().getTime());
                openDialogPickDateButton.setText(endDate + " - " + startDate);
                guests = 1;
                rooms = 1;
                openFilterDialogButton.setText(rooms + " ?????????? " + guests + " ????????????");

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();

        userRef.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(User.class).getIsManager()) {
                    setHasOptionsMenu(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference entryTicketRef = FirebaseDatabase.getInstance().getReference();
        EntryTicket entryTicket = new EntryTicket(null,"???????? ??????????",
                "???????? ?????????? ?????????? ?????????? ?????????? ???????????????? ???? ???? ????????, ??????'???? ???? ???????? ???? ??????????!",100,200,null);
        entryTicketRef.child("entryTicket").setValue(entryTicket);
        return parent;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.open_date_picker_button) {
            pickDatesBottomSheet bottomSheet = new pickDatesBottomSheet(this, startDate, endDate);
            bottomSheet.show(getActivity().getSupportFragmentManager(), "pickDate");
        } else if (v.getId() == R.id.open_room_guest_picker) {
            FilterPackagesBottomSheet bottomSheet = new FilterPackagesBottomSheet(this, this.rooms, this.guests);
            bottomSheet.show(getActivity().getSupportFragmentManager(), "filter");
        } else if (v.getId() == R.id.filterSearchButton) {
            filterAllVacations();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.admin_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.managerPageButton) {

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ManagerFragment())
                    .addToBackStack(null).commit();
        }
        return true;
    }

    @Override
    public void onDatePicked(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        openDialogPickDateButton.setText(endDate + " - " + startDate);
    }

    @Override
    public void onRoomsAndGuestsPicked(int rooms, int guests) {
        this.rooms = rooms;
        this.guests = guests;
        openFilterDialogButton.setText(rooms + " ?????????? " + guests + " ????????????");
    }

    private void filterAllVacations(){

        availableVacations = new ArrayList<>();
        availableVacations.addAll(vacations);
        Log.d("allVacations",availableVacations.toString());
        for (Vacation vacation : availableVacations) {
            if (vacation.getTakenDates() == null || vacation.getTakenDates().size() == 0) {}
            else {
                DatabaseReference takenDateRef = FirebaseDatabase.getInstance().getReference("takenDates");
                takenDateRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        outerloop:
                        for (DataSnapshot ds : snapshot.getChildren()){
                            if (ds.getKey().equals(vacation.getUid())){
                                for (DataSnapshot snap : ds.getChildren()){
                                    if (snap.child("takenDate").getValue(String.class).equals(startDate)){
                                        String addedToCartTime = snap.child("addedToCartTime").getValue(String.class);
                                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");
                                        String currentDate = format.format(Calendar.getInstance().getTime());
                                        if (!isTimeDiffOver(currentDate,addedToCartTime)){
                                            Log.d("deletingVacation",vacation.toString());
                                            availableVacations.remove(vacation);
                                            vacationAdapter.updateVacations(availableVacations);
                                            vacationAdapter.notifyDataSetChanged();
                                            break outerloop;
                                        }
                                    }
                                }
                            }
                        }
                        vacationAdapter.updateVacations(availableVacations);
                        vacationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }

    }

    private boolean isTimeDiffOver(String currentTime, String takenTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");

        Date currentDate = null;
        Date takenDate = null;

        try {
            currentDate = formatter.parse(currentTime);
            takenDate = formatter.parse(takenTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long duration = currentDate.getTime() - takenDate.getTime();

        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        Log.d("diffInMinutes",diffInMinutes+"");
        return diffInMinutes > 30;
    }

}

