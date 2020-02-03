package com.meivaldi.adminwisatatanohgayo;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.MyViewHolder>
        implements Filterable {

    private Activity activity;
    private Context context;
    private List<Place> places;
    private List<Place> placesFiltered;

    public PlaceAdapter(Activity activity, Context context, List<Place> places) {
        this.activity = activity;
        this.context = context;
        this.places = places;
        this.placesFiltered = places;
    }

    @NonNull
    @Override
    public PlaceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PlaceAdapter.MyViewHolder holder, final int position) {
        final Place place = placesFiltered.get(position);

        holder.placeName.setText(place.getNama_tempat());

        StorageReference reference = FirebaseStorage.getInstance().getReference(place.getFoto());

        reference.getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Glide.with(context)
                                    .load(task.getResult())
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .thumbnail(0.5f)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(holder.image);
                        }
                    }
                });

        int starCount = place.getRating();

        for (int i=0; i<starCount; i++) {
            holder.stars[i].setBackgroundResource(R.drawable.star);
        }

        holder.placeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditTempat.class);
                intent.putExtra("place", place);
                context.startActivity(intent);
            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditTempat.class);
                intent.putExtra("place", place);
                context.startActivity(intent);
            }
        });

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopUp(holder.more, place.getId());
            }
        });
    }

    private void showPopUp(View view, int i) {
        PopupMenu popup =  new PopupMenu(context, view);
        MenuInflater menuInflater = popup.getMenuInflater();
        menuInflater.inflate(R.menu.item_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(i));
        popup.show();
    }

    @Override
    public int getItemCount() {
        return placesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence sequence) {
                String charString = sequence.toString();

                if (charString.isEmpty()) {
                    placesFiltered = places;
                } else {
                    List<Place> filteredList = new ArrayList<>();

                    for (Place place: places) {
                        if (place.getNama_tempat().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(place);
                        }
                    }

                    placesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = placesFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                placesFiltered = (ArrayList<Place>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView placeName;
        private ImageView image;
        private ImageView[] stars;
        private ImageView more;

        public MyViewHolder(@NonNull View view) {
            super(view);
            more = view.findViewById(R.id.more);
            placeName = view.findViewById(R.id.place);
            image = view.findViewById(R.id.image);

            stars = new ImageView[5];
            stars[0] = view.findViewById(R.id.star1);
            stars[1] = view.findViewById(R.id.star2);
            stars[2] = view.findViewById(R.id.star3);
            stars[3] = view.findViewById(R.id.star4);
            stars[4] = view.findViewById(R.id.star5);
        }
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;

        public MyMenuItemClickListener(int i) {
            this.position = i;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.hapus:
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbRef = db.getReference().child("tempat_wisata").child(String.valueOf(position));
                    dbRef.removeValue();

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference("tempat_wisata").child(String.valueOf(position));

                    StorageReference image1 = storageReference.child("image1.jpg");
                    StorageReference image2 = storageReference.child("image2.jpg");
                    StorageReference image3 = storageReference.child("image3.jpg");

                    image1.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });

                    image2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });

                    image3.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });

                    notifyDataSetChanged();

                    break;
                default:

            }

            return false;
        }
    }

}

