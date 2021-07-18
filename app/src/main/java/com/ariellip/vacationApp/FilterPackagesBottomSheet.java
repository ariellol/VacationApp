package com.ariellip.vacationApp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterPackagesBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

    Button addRoom,removeRoom,addAdult,removeAdult;

    TextView adultsNumberTv, roomsNumberTv;
    int roomsNum = 1,totalGuests = 1;

    public FilterPackagesBottomSheet(){ }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.filter_packages_bottom_sheet,container,false);

        addRoom = parent.findViewById(R.id.room_plus);
        removeRoom = parent.findViewById(R.id.room_minus);
        addAdult = parent.findViewById(R.id.adults_plus);
        removeAdult = parent.findViewById(R.id.adults_minus);
        addRoom.setOnClickListener(this);
        removeRoom.setOnClickListener(this);
        addAdult.setOnClickListener(this);
        removeAdult.setOnClickListener(this);

        adultsNumberTv = parent.findViewById(R.id.adults_number);
        adultsNumberTv.setText(String.valueOf(totalGuests));
        roomsNumberTv = parent.findViewById(R.id.room_number);
        roomsNumberTv.setText(String.valueOf(roomsNum));

        return parent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.room_plus:
                if(roomsNum>=0 && roomsNum<15) {
                    roomsNumberTv.setText(String.valueOf(roomsNum++));
                    removeRoom.setBackground(getResources().getDrawable(R.drawable.remove_stroke_button));
                }
                if (roomsNum==15)
                    addRoom.setBackground(getResources().getDrawable(R.drawable.add_button_stroke_disabled));
                break;

            case R.id.room_minus:
                if (roomsNum>0 && roomsNum<=15)
                roomsNumberTv.setText(String.valueOf(roomsNum--));
                addRoom.setBackground(getResources().getDrawable(R.drawable.add_button_stroke));
                if (roomsNum==0)
                    removeRoom.setBackground(getResources().getDrawable(R.drawable.remove_stroke_button_disabled));
                break;

            case R.id.adults_plus:
                if (totalGuests<30)
                    return;
                adultsNumberTv.setText(String.valueOf(totalGuests++));
                totalGuests++;
                if (totalGuests>=30)
                    addAdult.setBackground(getResources().getDrawable(R.drawable.add_button_stroke_disabled));
                break;

            case R.id.adults_minus:
                if (totalGuests<=1)
                    return;
                adultsNumberTv.setText(String.valueOf(totalGuests--));
                totalGuests--;
                if (totalGuests<=1)
                    removeAdult.setBackground(getResources().getDrawable(R.drawable.remove_stroke_button_disabled));
                else
                    removeAdult.setBackground(getResources().getDrawable(R.drawable.remove_stroke_button));
                break;

        }
    }
}
