package com.ariellip.vacationApp;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class VacationPackageFragment extends Fragment {

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
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.vacation_package_fragment,container,false);
        if (getArguments().getSerializable("currentVacation") != null){
            currentVacation = (Vacation) getArguments().getSerializable("currentVacation");
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

        titleTv.setText(currentVacation.getTitle());
        descriptionTv.setText(currentVacation.getDescription());
        roomAmountTv.setText(currentVacation.getAmountOfRooms()+ " חדרים");
        guestAmountTv.setText( "עד " + currentVacation.getAmountOfGuests()+" אורחים");
        apartmentSizeTv.setText(currentVacation.getApartmentSize() + " מטר רבוע");
        priceTv.setText(currentVacation.getPriceForWeekend()+ " שקלים חדשים ");
        return parent;

    }

}
