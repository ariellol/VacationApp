package com.ariellip.vacationApp;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class EditVacationFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener {

    Vacation currentVacation;

    EditText titleEt,descriptionEt,roomNumEt,guestsNumEt,priceEt,apartmentSizeEt;
    Button openDeleteDialog;
    LinearLayout imagesLayout;
    ArrayList<String> images;

    final int IMAGE_WIDTH = 200;
    final int IMAGE_HEIGHT = 200;

    int imageCount;

    Dialog dialogDeleteVacation;
    Button deleteVacationButton;
    Button cancelDeleteVacationButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.edit_vacation_fragment,container,false);

        currentVacation = (Vacation) getArguments().getSerializable("currentVacation");

        images = new ArrayList<>();
        imagesLayout = parent.findViewById(R.id.imagesLayout);
        titleEt = parent.findViewById(R.id.title_edit_et);
        descriptionEt = parent.findViewById(R.id.description_edit_et);
        roomNumEt = parent.findViewById(R.id.room_amount_et);
        guestsNumEt = parent.findViewById(R.id.max_guests_et);
        priceEt = parent.findViewById(R.id.price_for_night_et);
        apartmentSizeEt = parent.findViewById(R.id.apartment_size_et);
        openDeleteDialog = parent.findViewById(R.id.open_delete_vacation_dialog);

        titleEt.setOnFocusChangeListener(this);
        descriptionEt.setOnFocusChangeListener(this);
        roomNumEt.setOnFocusChangeListener(this);
        guestsNumEt.setOnFocusChangeListener(this);
        priceEt.setOnFocusChangeListener(this);
        apartmentSizeEt.setOnFocusChangeListener(this);
        openDeleteDialog.setOnClickListener(this);

        titleEt.setText(currentVacation.getTitle());
        descriptionEt.setText(currentVacation.getDescription());
        roomNumEt.setText(String.valueOf(currentVacation.getAmountOfRooms()));
        guestsNumEt.setText(String.valueOf(currentVacation.getAmountOfGuests()));
        priceEt.setText(String.valueOf(currentVacation.getPriceForWeekend()));
        apartmentSizeEt.setText(String.valueOf(currentVacation.getApartmentSize()));

        for (int i = 0; i<currentVacation.getImages().size(); i++){
            images.add(currentVacation.getImages().get(i));
            addImageToScrollView(images.get(i));
        }
        imageCount = images.size();

        dialogDeleteVacation = new Dialog(getActivity());
        dialogDeleteVacation.setContentView(R.layout.delete_vacation_dialog);
        deleteVacationButton = dialogDeleteVacation.findViewById(R.id.delete_vacation);
        cancelDeleteVacationButton = dialogDeleteVacation.findViewById(R.id.cancel_delete_vacation);
        deleteVacationButton.setOnClickListener(this);
        cancelDeleteVacationButton.setOnClickListener(this);
        return parent;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus)
            v.setBackgroundResource(R.drawable.edit_text_selected);
        else
            v.setBackgroundResource(R.drawable.edit_text_background);
    }

    private void addImageToScrollView(String imageName){
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("images/").child(imageName);
        CustomTarget<Bitmap> imageBitmap = null;
        Bitmap bitmap;
        GlideApp.with(getActivity()).asBitmap().load(reference).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                imageCount++;
                resizeBitmap(resource);
                ImageView newImage = new ImageView(getActivity());
                newImage.setImageBitmap(resource);
                newImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(IMAGE_WIDTH),dpToPx(IMAGE_HEIGHT));
                params.setMargins(0,0,dpToPx(5),0);

                newImage.setLayoutParams(params);
                imagesLayout.addView(newImage);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) { }
        });

    }

    private int dpToPx(int dps) {
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dps * scale + 0.5f);
        return pixels;
    }

    public void resizeBitmap(Bitmap imageBitmap){
        double ratio = 1;

        if (imageBitmap.getWidth() < imageBitmap.getHeight())
            ratio = (double)dpToPx(IMAGE_HEIGHT) / imageBitmap.getHeight();
        else
            ratio = (double)dpToPx(IMAGE_WIDTH) / imageBitmap.getWidth();
        int scaledWidth = (int) (imageBitmap.getWidth() * ratio);
        int scaledHeight = (int) (imageBitmap.getHeight() * ratio);
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, scaledWidth, scaledHeight, true);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == openDeleteDialog.getId()){
            dialogDeleteVacation.show();
        }
        else if(v.getId() == cancelDeleteVacationButton.getId()){
            dialogDeleteVacation.dismiss();
        }
        else if(v.getId() == deleteVacationButton.getId()){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Vacations")
                    .child(currentVacation.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/");
                    for (int i= 0; i<currentVacation.getImages().size(); i++) {
                        storageReference.child(currentVacation.getImages().get(i)).delete();
                    }
                    snapshot.getRef().removeValue();
                    Toast.makeText(getActivity(), "חבילת נופש נמחקה.", Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new VacationListManager())
                    .addToBackStack(null).commit();
                    dialogDeleteVacation.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
