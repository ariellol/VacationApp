package com.ariellip.vacationApp;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class AddVacationFragment extends Fragment implements View.OnFocusChangeListener, View.OnClickListener {

    EditText titleEt, descriptionEt,roomNumEt,priceEt,apartSize,maxGuestsNum;
    Button addVacation;

    LinearLayout imageLayout;
    HorizontalScrollView imagesScrollView;

    final int IMAGE_WIDTH = 200;
    final int IMAGE_HEIGHT = 200;
    Bitmap imageBitmap;
    int imageCount = 0;
    ArrayList<String> images;
    ArrayList<Uri> imageUris;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_add_new_vacation,container,false);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        titleEt = parent.findViewById(R.id.package_title_et);
        descriptionEt = parent.findViewById(R.id.description_et);
        roomNumEt = parent.findViewById(R.id.room_amount_et);
        priceEt = parent.findViewById(R.id.price_for_night_et);
        apartSize = parent.findViewById(R.id.apartment_size_et);
        maxGuestsNum = parent.findViewById(R.id.max_guests_et);
        addVacation = parent.findViewById(R.id.addVacationButton);

        imageLayout = parent.findViewById(R.id.imageLayout);
        imagesScrollView = parent.findViewById(R.id.imageLayoutScrollView);

        titleEt.setOnFocusChangeListener(this);
        descriptionEt.setOnFocusChangeListener(this);
        roomNumEt.setOnFocusChangeListener(this);
        priceEt.setOnFocusChangeListener(this);
        apartSize.setOnFocusChangeListener(this);
        maxGuestsNum.setOnFocusChangeListener(this);
        addVacation.setOnClickListener(this);

        images = new ArrayList<>();
        imageUris = new ArrayList<>();

        setHasOptionsMenu(true);
        return parent;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == addVacation.getId()){
            if (emptyEditTextError(titleEt) && emptyEditTextError(descriptionEt) && emptyEditTextError(roomNumEt) &&
                    emptyEditTextError(priceEt) && emptyEditTextError(apartSize) && emptyEditTextError(maxGuestsNum) && imageCount>0){
                    uploadImages(imageUris);


                Vacation newVacation = new Vacation(titleEt.getText().toString(),descriptionEt.getText().toString(),true,
                        Integer.parseInt(roomNumEt.getText().toString()),Integer.parseInt(maxGuestsNum.getText().toString()),
                        Integer.parseInt(apartSize.getText().toString()),Integer.parseInt(priceEt.getText().toString()),
                        images);

                saveVacationInDatabase(newVacation);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new VacationListManager())
                .addToBackStack(null).commit();
            }
            else{
                Log.d("empty","yes");
            }


        }
    }

    private boolean emptyEditTextError(EditText editText){
        if (editText.getText().toString().equals("")) {
            editText.setFocusable(true);
            editText.setError("חובה למלא שדה זה");
            return false;
        }
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus)
            v.setBackgroundResource(R.drawable.edit_text_selected);
        else
            v.setBackgroundResource(R.drawable.edit_text_background);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.add_vacation_menu,menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_image_item){
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            else{
                if(imageCount>=6){
                    item.setCheckable(false);
                    item.setIconTintList(null);
                    Toast.makeText(getActivity(), "הגעת לכמות המקסימלית של תמונות.", Toast.LENGTH_SHORT).show();
                }
                else{
                    item.setCheckable(true);
                    File imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    String imagePath = imagesDir.getPath();
                    Uri imageUri = Uri.parse(imagePath);

                    Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                    galleryIntent.setDataAndType(imageUri,"image/*");
                    startActivityForResult(Intent.createChooser(galleryIntent,"בחירת תמונה"), 1);
                }

            }

        }
        return super.onOptionsItemSelected(item);
    }

    public void uploadImages(ArrayList<Uri> filePaths) {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("מעלה...");
        progressDialog.show();
        for (int i =0; i<filePaths.size(); i++) {
            images.add(UUID.randomUUID().toString());
            StorageReference ref = storageReference.child("images/" + images.get(i));
            Log.d("filePath",filePaths.get(i).toString());
            ref.putFile(filePaths.get(i)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressDialog.setTitle(snapshot.getBytesTransferred()+"");
                }
            }).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
        progressDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            addImageToScrollView(data.getData());
            imageUris.add(data.getData());
            imageCount++;
        }

    }

    private void addImageToScrollView(Uri imageUri){

        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        resizeBitmap(imageBitmap);
        ImageView newImage = new ImageView(getActivity());
        newImage.setImageBitmap(imageBitmap);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(IMAGE_WIDTH),dpToPx(IMAGE_HEIGHT));
        params.setMargins(0,0,dpToPx(5),0);

        newImage.setLayoutParams(params);
        imageLayout.addView(newImage);

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

    public void saveVacationInDatabase(Vacation vacation) {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("מעלה...");
        progressDialog.show();

        String uid = databaseReference.child("Vacations").push().getKey();
        vacation.setUid(uid);
        databaseReference.child("Vacations").child(uid).setValue(vacation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

}
