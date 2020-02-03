package com.meivaldi.adminwisatatanohgayo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class EditTempat extends AppCompatActivity {

    private Toolbar toolbar;
    private Button update, ubah;
    private EditText namaET, alamatET, deskripsiET, jamET,
            ketinggianET, luasET, latET, lonET, tempatTerdekat;
    private ProgressDialog pDialog;
    private ImageView picture1, picture2, picture3;
    private Spinner spinner;
    private LinearLayout closer;

    private DatabaseReference dbRef;
    private Uri file1, file2, file3;
    private Button changePicture1, changePicture2, changePicture3;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tempat);

        dbRef = FirebaseDatabase.getInstance().getReference().child("tempat_wisata");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Tempat");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Memroses...");
        pDialog.setCancelable(false);

        namaET = findViewById(R.id.namaET);
        alamatET = findViewById(R.id.alamatET);
        deskripsiET = findViewById(R.id.deskripsiET);
        jamET = findViewById(R.id.jamET);
        ketinggianET = findViewById(R.id.ketinggianET);
        luasET = findViewById(R.id.luasET);
        latET = findViewById(R.id.latitudeET);
        lonET = findViewById(R.id.longitudeET);
        spinner = findViewById(R.id.closerPlace);
        spinner.setSelection(2);
        tempatTerdekat = findViewById(R.id.tempatTerdekat);
        closer = findViewById(R.id.changeCloserPlace);

        ubah = findViewById(R.id.ubah);
        ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closer.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
            }
        });

        getCloser();

        picture1 = findViewById(R.id.picture1);
        picture2 = findViewById(R.id.picture2);
        picture3 = findViewById(R.id.picture3);

        changePicture1 = findViewById(R.id.changePicture1);
        changePicture2 = findViewById(R.id.changePicture2);
        changePicture3 = findViewById(R.id.changePicture3);

        changePicture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, 100);
            }
        });

        changePicture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, 101);
            }
        });

        changePicture3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, 102);
            }
        });

        update = findViewById(R.id.update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();
                Place place = (Place) getIntent().getSerializableExtra("place");
                String tempat_terdekat = spinner.getSelectedItem().toString();

                place.setId(place.getId());
                place.setAlamat(alamatET.getText().toString());
                place.setDeskripsi(deskripsiET.getText().toString());

                if (place.getFoto().equals("tempat_wisata/default/no_photo.png")) {
                    if (file1 != null) {
                        place.setFoto("tempat_wisata/" + place.getId() + "/image1.jpg");
                    } else {
                        place.setFoto("tempat_wisata/default/no_photo.png");
                    }
                }

                place.setJam_operasional(jamET.getText().toString());
                place.setKetinggian(ketinggianET.getText().toString());
                place.setLuas(luasET.getText().toString());
                place.setNama_tempat(namaET.getText().toString());
                place.setLat(Double.valueOf(latET.getText().toString()));
                place.setLon(Double.valueOf(lonET.getText().toString()));
                place.setRating(place.getRating());
                place.setSumber(place.getSumber());

                if (spinner.getVisibility() == View.VISIBLE) {
                    if (tempat_terdekat.equals("-- Tempat terdekat --")) {
                        place.setTempat_terdekat(0);
                    } else {
                        place.setTempat_terdekat(spinner.getSelectedItemPosition()-1);
                    }
                } else {
                    place.setTempat_terdekat(place.getTempat_terdekat());
                }

                StorageReference storageReference =FirebaseStorage.getInstance().getReference("tempat_wisata")
                        .child(String.valueOf(place.getId()));

                if (file1 != null) {
                    UploadTask task1 = storageReference.child("image1.jpg").putFile(file1);

                    task1.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        }
                    });
                }

                if (file2 != null) {
                    UploadTask task2 = storageReference.child("image2.jpg").putFile(file2);

                    task2.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        }
                    });
                }

                if (file3 != null) {
                    UploadTask task3 = storageReference.child("image3.jpg").putFile(file3);

                    task3.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        }
                    });
                }

                dbRef.child(String.valueOf(place.getId())).setValue(place)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditTempat.this, "Berhasil mengupdate data", Toast.LENGTH_SHORT).show();

                                    pDialog.dismiss();
                                    finish();
                                } else {
                                    Toast.makeText(EditTempat.this, "Gagal mengupdate data", Toast.LENGTH_SHORT).show();

                                    pDialog.dismiss();
                                }
                            }
                        });
            }
        });

        loadData();
    }

    private void getCloser() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("tempat_wisata");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> label = new ArrayList<>();
                label.add("-- Tempat terdekat --");

                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    Place place = data.getValue(Place.class);

                    label.add(place.getNama_tempat());
                    adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.custom_spinner, label);
                    spinner.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadData() {
        Place place = (Place) getIntent().getSerializableExtra("place");

        namaET.setText(place.getNama_tempat());
        alamatET.setText(place.getAlamat());
        deskripsiET.setText(place.getDeskripsi());
        jamET.setText(place.getJam_operasional());
        ketinggianET.setText(place.getKetinggian());
        luasET.setText(place.getLuas());
        latET.setText(String.valueOf(place.getLat()));
        lonET.setText(String.valueOf(place.getLon()));
        spinner.setSelection(10);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("tempat_wisata").child(String.valueOf(place.getTempat_terdekat()));
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Place place = dataSnapshot.getValue(Place.class);

                tempatTerdekat.setText(place.getNama_tempat());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        StorageReference reference1 = FirebaseStorage.getInstance().getReference().child("tempat_wisata")
                .child(String.valueOf(place.getId())).child("image1.jpg");

        StorageReference reference2 = FirebaseStorage.getInstance().getReference().child("tempat_wisata")
                .child(String.valueOf(place.getId())).child("image2.jpg");

        StorageReference reference3 = FirebaseStorage.getInstance().getReference().child("tempat_wisata")
                .child(String.valueOf(place.getId())).child("image3.jpg");


        reference1.getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Glide.with(getApplicationContext())
                                    .load(task.getResult())
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .thumbnail(0.5f)
                                    .into(picture1);
                        }
                    }
                });

        reference2.getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Glide.with(getApplicationContext())
                                    .load(task.getResult())
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .thumbnail(0.5f)
                                    .into(picture2);
                        }
                    }
                });

        reference3.getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Glide.with(getApplicationContext())
                                    .load(task.getResult())
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .thumbnail(0.5f)
                                    .into(picture3);
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK) {
            file1 = data.getData();

            picture1.setImageURI(file1);
            picture1.setVisibility(View.VISIBLE);
        } else if (requestCode == 101 && resultCode == RESULT_OK) {
            file2 = data.getData();

            picture2.setImageURI(file2);
            picture2.setVisibility(View.VISIBLE);
        } else if (requestCode == 102 && resultCode == RESULT_OK) {
            file3 = data.getData();

            picture3.setImageURI(file3);
            picture3.setVisibility(View.VISIBLE);
        }
    }
}
