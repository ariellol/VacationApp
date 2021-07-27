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
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ManagerFragment extends Fragment implements View.OnClickListener {

    Button buttonWatchUserList;
    Button buttonWatchVacationList;
    Button showExtrasButton;
    Button editEntryTicket;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View parent = inflater.inflate(R.layout.fragment_manager,container,false);
        Context context = getActivity();


        Toolbar toolbar = getActivity().findViewById(R.id.app_bar);
        toolbar.setTitle("דף מנהלים");
        setHasOptionsMenu(true);

        buttonWatchUserList = parent.findViewById(R.id.watch_users_button);
        buttonWatchUserList.setOnClickListener(this);

        buttonWatchVacationList = parent.findViewById(R.id.show_all_vacations);
        buttonWatchVacationList.setOnClickListener(this);

        showExtrasButton = parent.findViewById(R.id.show_extras_button);
        showExtrasButton.setOnClickListener(this);

        editEntryTicket = parent.findViewById(R.id.edit_entry_ticket);
        editEntryTicket.setOnClickListener(this);

        return parent;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        if(v.getId() == buttonWatchUserList.getId()){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UserListWatchFragment())
                    .addToBackStack(null).commit();
            ((Toolbar)getActivity().findViewById(R.id.app_bar)).setTitle("צפייה במשתמשים");
        }
        else if(v.getId() == R.id.show_all_vacations){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VacationListManager())
                    .addToBackStack(null).commit();
            ((Toolbar)getActivity().findViewById(R.id.app_bar)).setTitle("צפייה ועריכת חבילות");
        }

        else if(v.getId() == R.id.show_extras_button){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ExtrasListManager())
                    .addToBackStack(null).commit();
            ((Toolbar)getActivity().findViewById(R.id.app_bar)).setTitle("צפייה ועריכת אטרקציות");
        }
        else if(v.getId() == R.id.edit_entry_ticket){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EditTicketFragment())
                    .addToBackStack(null).commit();
            ((Toolbar)getActivity().findViewById(R.id.app_bar)).setTitle("עריכת צמיד כניסה למתחם");
        }
    }

}





