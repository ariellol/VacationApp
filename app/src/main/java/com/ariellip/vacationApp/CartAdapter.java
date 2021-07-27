package com.ariellip.vacationApp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.function.LongFunction;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    Context context;
    ArrayList<Vacation> vacations;
    StorageReference reference;
    Vacation recentlyDeletedItem;
    int recentlyDeletedPosition;
    Activity activity;
    View view;
    ItemDeleted itemDeletedInterface;
    int moneyCount = 0;

    public CartAdapter(@NonNull Context context, ArrayList<Vacation> vacations, View view, ItemDeleted itemDeleted) {
        this.context = context;
        this.vacations = vacations;
        activity = (Activity) context;
        this.view = view;
        this.itemDeletedInterface = itemDeleted;
    }




    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_in_cart, parent, false);

        return new CartViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        reference = FirebaseStorage.getInstance().getReference().child("images/").child(vacations.get(position).getFirstImage());
        GlideApp.with(context).load(reference).into(holder.getImage());
        holder.getTitle().setText(vacations.get(position).getTitle());
        holder.getPrice().setText(vacations.get(position).getPriceForWeekend() + " שקלים חדשים");
        moneyCount += vacations.get(position).getPriceForWeekend();
    }

    public void deleteItem(int position) {
        recentlyDeletedItem = vacations.get(position);
        recentlyDeletedPosition = position;
        vacations.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }


    private void showUndoSnackbar() {
        View view = this.view.findViewById(R.id.snackbar_layout);
        Snackbar snackbar = Snackbar.make(view, "ביטול מחיקה", Snackbar.LENGTH_LONG);
        snackbar.setAction("ביטול", v -> undoDelete());
        snackbar.show();

        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                    FirebaseDatabase.getInstance().getReference("Users")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        DatabaseReference ref = ds.child("cart").getRef();
                                        ref.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String cartUid = "";
                                                outerloop:
                                                for (DataSnapshot snap : snapshot.getChildren()) {
                                                    ArrayList<String> takenVacations = new ArrayList<>();
                                                    takenVacations.add(snap.child(recentlyDeletedItem.getUid()).getKey());
                                                    Log.d("deletedVacation",snap.child(recentlyDeletedItem.getUid())
                                                            .child("takenDate").getValue(String.class)+"");
                                                    Log.d("recentleyDeleted",recentlyDeletedItem.getTakenDates().toString());
                                                    for (int i = 0; i < takenVacations.size(); i++) {
                                                        for (String takenDate : recentlyDeletedItem.getTakenDates()) {
                                                            if(snap.child(recentlyDeletedItem.getUid())
                                                                    .child("takenDate").getValue(String.class) != null)
                                                            {
                                                                if (snap.child(recentlyDeletedItem.getUid())
                                                                        .child("takenDate").getValue(String.class).equals(takenDate)) {
                                                                    moneyCount -= recentlyDeletedItem.getPriceForWeekend();
                                                                    cartUid = snap.getKey();
                                                                    break outerloop;
                                                                }
                                                            }
                                                        }

                                                    }
                                                }

                                                if (!cartUid.equals("")) {
                                                    snapshot.child(cartUid).getRef().removeValue();

                                                }
                                                String finalCartUid = cartUid;
                                                FirebaseDatabase.getInstance().getReference("takenDates")
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (!finalCartUid.equals(""))
                                                                    snapshot.child(recentlyDeletedItem.getUid()).child(finalCartUid).getRef().removeValue();
                                                                    itemDeletedInterface.onItemDeleted(moneyCount);
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
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }
        });
    }


    private void undoDelete() {
        vacations.add(recentlyDeletedPosition,
                recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedPosition);
    }


    @Override
    public int getItemCount() {
        return vacations.size();
    }


    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView price;
        Button deleteItem;

        public ImageView getImage() {
            return image;
        }


        public TextView getTitle() {
            return title;
        }


        public TextView getPrice() {
            return price;
        }

        public Button getDeleteItem() {
            return deleteItem;
        }


        public CartViewHolder(View view, CartAdapter context) {
            super(view);

            image = view.findViewById(R.id.image_in_cart_item);
            title = view.findViewById(R.id.title_in_cart_item);
            price = view.findViewById(R.id.price_in_cart_item);
        }
    }
}
