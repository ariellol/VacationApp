package com.ariellip.vacationApp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.function.LongFunction;

public class CartFragment extends Fragment implements ItemDeleted{

    Button continueToPaymentButton;
    RecyclerView cartListView;
    ArrayList<Cart> carts;
    ArrayList<String> vacationsUid;
    ArrayList<String> takenDates;
    CartAdapter cartAdapter;
    ArrayList<Vacation> vacations;
    TextView totalMoneyTv;
    int totalMoney = 0;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_cart,container,false);

        cartListView = parent.findViewById(R.id.cart_list_view);
        totalMoneyTv = parent.findViewById(R.id.total_amount_of_money);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid()).child("cart");
        userCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                vacations = new ArrayList<>();
                vacationsUid = new ArrayList<>();
                carts = new ArrayList<>();
                takenDates = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    for (DataSnapshot dataSnapshot : ds.getChildren()){
                        carts.add(dataSnapshot.getValue(Cart.class));
                        Log.d("cart",carts.toString());
                        vacationsUid.add(carts.get(carts.size()-1).getVacations().get(0));
                        takenDates.add(carts.get(carts.size()-1).getTakenDate());
                    }
                }
                Log.d("vacationUid",vacationsUid.toString());
                DatabaseReference vacationsRef = FirebaseDatabase.getInstance().getReference("Vacations");
                vacationsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(int i =0; i<vacationsUid.size(); i++){
                            vacations.add(snapshot.child(vacationsUid.get(i)).getValue(Vacation.class));
                            if (vacations.get(vacations.size()-1).getUid().equals(vacationsUid.get(i))){
                                ArrayList<String> temp = new ArrayList<>();
                                temp.add(takenDates.get(i));
                                vacations.get(vacations.size()-1).setTakenDates(temp);
                            }
                            totalMoney += vacations.get(i).getPriceForWeekend();
                        }
                        Log.d("VacationsInCart",vacations.toString());
                        cartAdapter = new CartAdapter(getActivity(),vacations,parent,CartFragment.this::onItemDeleted);
                        cartListView.setHasFixedSize(true);
                        cartListView.setLayoutManager(new LinearLayoutManager(getContext()));
                        cartListView.setAdapter(cartAdapter);
                        setUpRecyclerView();
                        cartAdapter.notifyDataSetChanged();
                        totalMoneyTv.setText(totalMoney + " שקלים");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setHasOptionsMenu(true);
        return parent;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.attraction_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.attractionItem){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ExtrasFragment())
                    .addToBackStack(null).commit();
            ((Toolbar)getActivity().findViewById(R.id.app_bar)).setTitle("אטרקציות ותוספות");
        }
        return true;
    }

    private void setUpRecyclerView() {
        cartListView.setAdapter(cartAdapter);
        cartListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new SwipeToDeleteItem(cartAdapter,getActivity()));
        itemTouchHelper.attachToRecyclerView(cartListView);
    }

    @Override
    public void onItemDeleted(int totalMoney) {
        totalMoneyTv.setText(totalMoney + " שקלים חדשים");
    }
}
