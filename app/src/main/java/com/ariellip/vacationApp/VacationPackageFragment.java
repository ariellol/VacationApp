package com.ariellip.vacationApp;

import android.app.Dialog;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class VacationPackageFragment extends Fragment implements View.OnClickListener{

    ViewPager imageViewPager;
    ImagePagerAdapter pagerAdapter;
    Vacation currentVacation;
    ArrayList<String> imagesUrl;

    TextView titleTv;
    TextView descriptionTv;
    TextView roomAmountTv;
    TextView guestAmountTv;
    TextView priceTv;
    TextView checkInTv;
    TextView checkOutTv;
    TextView apartmentSizeTv;
    Button addToCartButton;

    Dialog attracationDialog;
    Button moveToAttractions;
    Button moveToCart;

    String startDate;
    String endDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.vacation_package_fragment,container,false);
        if (getArguments().getSerializable("currentVacation") != null){
            currentVacation = (Vacation) getArguments().getSerializable("currentVacation");
            startDate = getArguments().getString("startDate");
            endDate = getArguments().getString("endDate");
        }

        imageViewPager = parent.findViewById(R.id.imageViewPager);
        imagesUrl = new ArrayList<>();
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("images/");
        for (int i =0; i<currentVacation.getImages().size();i++){
            imagesUrl.add(reference.child(currentVacation.getImages().get(i)).toString());
            Log.d("imageUrl",currentVacation.getImages().get(i));
        }
        pagerAdapter = new ImagePagerAdapter(getActivity(),imagesUrl);
        imageViewPager.setAdapter(pagerAdapter);
        pagerAdapter.notifyDataSetChanged();

        titleTv = parent.findViewById(R.id.package_title);
        descriptionTv = parent.findViewById(R.id.package_description);
        roomAmountTv = parent.findViewById(R.id.room_amount_package);
        guestAmountTv = parent.findViewById(R.id.guest_amount_package);
        apartmentSizeTv = parent.findViewById(R.id.apartment_size_package);
        priceTv = parent.findViewById(R.id.price_package);
        checkInTv = parent.findViewById(R.id.checkInTv);
        checkOutTv = parent.findViewById(R.id.checkOutTv);
        addToCartButton = parent.findViewById(R.id.add_to_cart);
        addToCartButton.setOnClickListener(this);

        titleTv.setText(currentVacation.getTitle());
        descriptionTv.setText(currentVacation.getDescription());
        roomAmountTv.setText(currentVacation.getAmountOfRooms()+ " חדרים");
        guestAmountTv.setText( "עד " + currentVacation.getAmountOfGuests()+" אורחים");
        apartmentSizeTv.setText(currentVacation.getApartmentSize() + " מטר רבוע");
        priceTv.setText(currentVacation.getPriceForWeekend()+ " שקלים חדשים ");
        checkInTv.setText(checkInTv.getText().toString() + " " +startDate);
        checkOutTv.setText(checkOutTv.getText().toString() + " " + endDate);

        attracationDialog = new Dialog(getActivity());
        attracationDialog.setContentView(R.layout.dialog_ask_move_to_attractions);
        attracationDialog.setCancelable(false);
        moveToAttractions = attracationDialog.findViewById(R.id.move_to_attraction);
        moveToCart = attracationDialog.findViewById(R.id.move_to_cart);
        moveToCart.setOnClickListener(this);
        moveToAttractions.setOnClickListener(this);
        return parent;

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == addToCartButton.getId()){
            attracationDialog.show();
            ArrayList<String> newVacation =new ArrayList<>();
            newVacation.add(currentVacation.getUid());
            Cart cart = new Cart(newVacation,startDate);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference userCartRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            userCartRef = userCartRef.child("cart");
            String cartUid = userCartRef.push().getKey();
            cart.setCartUid(cartUid);
            userCartRef.child(cartUid).child(currentVacation.getUid()).setValue(cart);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("takenDates").child(currentVacation.getUid()).child(cartUid);
            reference.setValue(cart);
        }
        else if(v.getId() == moveToAttractions.getId()){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ExtrasFragment())
                    .addToBackStack(null).commit();
            ((Toolbar)getActivity().findViewById(R.id.app_bar)).setTitle("תוספות ואטרקציות");
            attracationDialog.dismiss();
        }
        else if(v.getId() == moveToCart.getId()){

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CartFragment())
                    .addToBackStack(null).commit();
            ((Toolbar)getActivity().findViewById(R.id.app_bar)).setTitle("עגלה");
            attracationDialog.dismiss();
        }
    }
}

