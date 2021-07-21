package com.ariellip.vacationApp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class VacationAdapter extends ArrayAdapter<Vacation> {

    Context context;
    ArrayList<Vacation> vacations;
    StorageReference reference;


    public VacationAdapter(@NonNull Context context, ArrayList<Vacation> vacations) {
        super(context, 0,new ArrayList<Vacation>());
        this.context = context;
        this.vacations = vacations;
        updateVacations(vacations);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

        convertView = LayoutInflater.from(context).inflate(R.layout.package_list_view,parent,false);

        ImageView packageImage = convertView.findViewById(R.id.package_Image);
        TextView packageTitle = convertView.findViewById(R.id.package_title);
        TextView packageDescription = convertView.findViewById(R.id.package_description);
        TextView amountOfGuests = convertView.findViewById(R.id.package_guest_amount);
        TextView roomAmount = convertView.findViewById(R.id.package_room_number);
        TextView availabilty = convertView.findViewById(R.id.package_availabilty);
        TextView priceForWeekend = convertView.findViewById(R.id.priceForWeekend);

        Vacation vacation = vacations.get(position);
        reference = FirebaseStorage.getInstance().getReference().child("images/").child(vacation.getFirstImage());
        GlideApp.with(context).load(reference).into(packageImage);
        packageTitle.setText(vacation.getTitle());
        packageDescription.setText(vacation.getDescription());
        amountOfGuests.setText(String.valueOf(vacation.getAmountOfGuests()) + " אורחים");
        roomAmount.setText(String.valueOf(vacation.getAmountOfRooms()) + " חדרים" );
        if (vacation.isAvailable()) {
            availabilty.setTextColor(context.getResources().getColor(R.color.oceanGreen));
            availabilty.setText("פנוי כעת!");
        }
        else{
            availabilty.setTextColor(context.getResources().getColor(R.color.red));
            availabilty.setText("לא פנוי בזמן הקרוב.");
        }
        priceForWeekend.setText(String.valueOf(vacation.getPriceForWeekend()) + " שקלים");

        return convertView;
    }

    public void updateVacations(ArrayList<Vacation> vacations){
        Log.d("availableVacationsAdapter",vacations.toString());
        this.clear();
        this.addAll(vacations);

    }
}
