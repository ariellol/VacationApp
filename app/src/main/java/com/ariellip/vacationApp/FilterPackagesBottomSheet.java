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

    Button addRoom,removeRoom,addAdult,removeAdult,addChild,removeChild;

    TextView childrenNumberTv, adultsNumberTv, roomsNumberTv;
    int childrenNum = 0;
    int roomsNum,adultsNum;
    int totalGuests = childrenNum + adultsNum;

    public FilterPackagesBottomSheet(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.filter_packages_bottom_sheet,container,false);

        addRoom = parent.findViewById(R.id.room_plus);
        removeRoom = parent.findViewById(R.id.room_minus);
        addAdult = parent.findViewById(R.id.adults_plus);
        removeAdult = parent.findViewById(R.id.adults_minus);
        addChild = parent.findViewById(R.id.kids_plus);
        removeChild = parent.findViewById(R.id.kids_minus);
        addRoom.setOnClickListener(this);
        removeRoom.setOnClickListener(this);
        addAdult.setOnClickListener(this);
        removeAdult.setOnClickListener(this);
        addChild.setOnClickListener(this);
        removeChild.setOnClickListener(this);

        roomsNum = 1;
        adultsNum = 0;
        childrenNumberTv = parent.findViewById(R.id.kids_number);
        childrenNumberTv.setText(String.valueOf(childrenNum));
        adultsNumberTv = parent.findViewById(R.id.adults_number);
        adultsNumberTv.setText(String.valueOf(adultsNum));
        roomsNumberTv = parent.findViewById(R.id.room_number);
        roomsNumberTv.setText(String.valueOf(roomsNum));

        return parent;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.room_plus:
                if(roomsNum>=15)
                    return;
                roomsNum++;
                roomsNumberTv.setText(String.valueOf(roomsNum));
                if (roomsNum>=15)
                    addRoom.setBackground(getResources().getDrawable(R.drawable.add_button_stroke_disabled));
                removeRoom.setBackground(getResources().getDrawable(R.drawable.remove_stroke_button));
                break;

            case R.id.room_minus:
                if (roomsNum<=0)
                    return;
                roomsNumberTv.setText(String.valueOf(roomsNum--));
                if (roomsNum<=0)
                    removeRoom.setBackground(getResources().getDrawable(R.drawable.remove_stroke_button_disabled));
                else
                    removeRoom.setBackground(getResources().getDrawable(R.drawable.remove_stroke_button));
                addRoom.setBackground(getResources().getDrawable(R.drawable.add_button_stroke));
                break;

            case R.id.adults_plus:
                if (totalGuests>=30)
                    return;
                adultsNumberTv.setText(String.valueOf(adultsNum++));
                totalGuests++;
                if (totalGuests>=30)
                    addAdult.setBackground(getResources().getDrawable(R.drawable.add_button_stroke_disabled));
                break;

            case R.id.adults_minus:
                if (adultsNum<=1)
                    return;
                adultsNumberTv.setText(String.valueOf(adultsNum--));
                totalGuests--;
                if (adultsNum<=1)
                    removeAdult.setBackground(getResources().getDrawable(R.drawable.remove_stroke_button_disabled));
                else
                    removeAdult.setBackground(getResources().getDrawable(R.drawable.remove_stroke_button));
                break;

            case R.id.kids_plus:
                if (totalGuests>=30)
                    return;
                childrenNumberTv.setText(String.valueOf(childrenNum++));
                totalGuests++;
                if (totalGuests>=30)
                    addAdult.setBackground(getResources().getDrawable(R.drawable.add_button_stroke_disabled));
                break;

            case R.id.kids_minus:
                if (childrenNum<=0)
                    return;
                childrenNumberTv.setText(String.valueOf(childrenNum--));
                totalGuests--;
                if (childrenNum<=1)
                    removeChild.setBackground(getResources().getDrawable(R.drawable.remove_stroke_button_disabled));
                else
                    removeChild.setBackground(getResources().getDrawable(R.drawable.remove_stroke_button));
                break;

        }
    }
}
