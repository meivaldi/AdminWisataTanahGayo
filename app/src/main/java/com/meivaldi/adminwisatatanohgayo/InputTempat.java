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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class InputTempat extends AppCompatActivity {

    private Toolbar toolbar;
    private Button kirim;
    private EditText namaET, alamatET, deskripsiET, jamET,
        ketinggianET, luasET, latET, lonET;
    private ProgressDialog pDialog;
    private RelativeLayout photo1, photo2, photo3;
    private Button upload1, upload2, upload3;
    private ImageView picture1, picture2, picture3;
    private AppCompatSpinner spinner;

    private DatabaseReference dbRef;
    private Uri file1, file2, file3;

    private int lastPosition = 0;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_tempat);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Input Tempat");

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

        getCloser();

        picture1 = findViewById(R.id.picture1);
        picture2 = findViewById(R.id.picture2);
        picture3 = findViewById(R.id.picture3);

        upload1 = findViewById(R.id.upload1);
        upload2 = findViewById(R.id.upload2);
        upload3 = findViewById(R.id.upload3);

        photo1 = findViewById(R.id.photo1);
        photo2 = findViewById(R.id.photo2);
        photo3 = findViewById(R.id.photo3);

        upload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, 100);
            }
        });

        upload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, 101);
            }
        });

        upload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, 102);
            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference().child("tempat_wisata");
        getValue(new Addable() {
            @Override
            public void onPlaceAdded(int n) {
                lastPosition = n+1;
            }
        });

        kirim = findViewById(R.id.kirim);
        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.show();

                String nama = namaET.getText().toString();
                String alamat = alamatET.getText().toString();
                String deskripsi = deskripsiET.getText().toString();
                String jam = jamET.getText().toString();
                String ketinggian = ketinggianET.getText().toString();
                String luas = luasET.getText().toString();
                String tempatTerdekat = spinner.getSelectedItem().toString();

                double lat = Double.valueOf(latET.getText().toString());
                double lon = Double.valueOf(lonET.getText().toString());

                final Place place = new Place();
                place.setId(lastPosition);
                place.setAlamat(alamat);
                place.setDeskripsi(deskripsi);

                if (file1 != null) {
                    place.setFoto("tempat_wisata/" + lastPosition + "/image1.jpg");
                } else {
                    place.setFoto("tempat_wisata/default/no_photo.png");
                }

                place.setJam_operasional(jam);
                place.setKetinggian(ketinggian);
                place.setLuas(luas);
                place.setNama_tempat(nama);
                place.setLat(lat);
                place.setLon(lon);
                place.setRating(0);
                place.setSumber("Admin");

                if (tempatTerdekat.equals("-- Tempat terdekat --")) {
                    place.setTempat_terdekat(0);
                } else {
                    place.setTempat_terdekat(spinner.getSelectedItemPosition()-1);
                }

                dbRef.child(String.valueOf(lastPosition)).setValue(place)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(InputTempat.this, "Berhasil", Toast.LENGTH_SHORT).show();

                            namaET.setText("");
                            alamatET.setText("");
                            deskripsiET.setText("");
                            jamET.setText("");
                            ketinggianET.setText("");
                            luasET.setText("");
                            latET.setText("");
                            lonET.setText("");

                            picture1.setVisibility(View.GONE);
                            picture2.setVisibility(View.GONE);
                            picture3.setVisibility(View.GONE);

                            photo1.setVisibility(View.VISIBLE);
                            photo2.setVisibility(View.VISIBLE);
                            photo3.setVisibility(View.VISIBLE);

                            pDialog.dismiss();
                            return;
                        }

                        pDialog.dismiss();
                        }
                    });

                StorageReference storageReference =FirebaseStorage.getInstance().getReference("tempat_wisata")
                        .child(String.valueOf(lastPosition));

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
            }
        });

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

    private void getValue(final Addable addable) {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int n = 0;
                for (DataSnapshot data: dataSnapshot.getChildren()) {
                    n++;
                }

                addable.onPlaceAdded(n);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
            photo1.setVisibility(View.GONE);
        } else if (requestCode == 101 && resultCode == RESULT_OK) {
            file2 = data.getData();

            picture2.setImageURI(file2);
            picture2.setVisibility(View.VISIBLE);
            photo2.setVisibility(View.GONE);
        } else if (requestCode == 102 && resultCode == RESULT_OK) {
            file3 = data.getData();

            picture3.setImageURI(file3);
            picture3.setVisibility(View.VISIBLE);
            photo3.setVisibility(View.GONE);
        }
    }
}
