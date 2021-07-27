package com.ariellip.vacationApp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class ImagePagerAdapter extends PagerAdapter{

    private final Context context;
    private ArrayList<String> imagesUrls;
    private LayoutInflater inflater;
    StorageReference reference;
    public ImagePagerAdapter(Context context, ArrayList<String> imagesUrls){
        this.context = context;
        this.imagesUrls = imagesUrls;
        reference = FirebaseStorage.getInstance().getReference().child("images/");
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return imagesUrls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.image_view_pager,container,false);
        ImageView imageView = itemView.findViewById(R.id.image_to_view);
        reference = reference.child(imagesUrls.get(position));
        GlideApp.with(context).load(reference).into(imageView);
        this.notifyDataSetChanged();
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
        this.notifyDataSetChanged();
    }

}
