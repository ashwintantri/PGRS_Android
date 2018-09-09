package com.example.ashwin.pgrs;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectActivity extends AppCompatActivity
{
    TextView viewComplaint;
    RecyclerView rv;
    DatabaseReference db;
    ComplaintsAdapter complaintsAdapter;
    FirebaseUser currentUser;
    ProgressBar progressBar;
    private FirebaseAuth mAuth;
    double lat,longitude;
    FusedLocationProviderClient fusedLocationProviderClient;
    ArrayList<Complaints> cr = new ArrayList<Complaints>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        getSupportActionBar().setTitle("Submitted Complaints");
        mAuth = FirebaseAuth.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SelectActivity.this);
        progressBar = findViewById(R.id.progress_id);
        currentUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance().getReference();
        rv = findViewById(R.id.complaint_recyclerview_id);
        rv.setLayoutManager(new LinearLayoutManager(this));
        complaintsAdapter = new ComplaintsAdapter(this,cr);
        rv.setAdapter(complaintsAdapter);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                lat = location.getLatitude();
                longitude = location.getLongitude();
            }
        });
        Query query = FirebaseDatabase.getInstance().getReference();
        query.addListenerForSingleValueEvent(vEl);
        complaintsAdapter.notifyDataSetChanged();
    }

    private double getDistance(double LAT1, double LONG1, double LAT2, double LONG2)
    {
        return 2 * 6371000 * Math.asin(Math.sqrt(Math.pow((Math.sin((LAT2 * (3.14159 / 180) - LAT1 * (3.14159 / 180)) / 2)), 2) + Math.cos(LAT2 * (3.14159 / 180)) * Math.cos(LAT1 * (3.14159 / 180)) * Math.sin(Math.pow(((LONG2 * (3.14159 / 180) - LONG1 * (3.14159 / 180)) / 2), 2))));
    }
    ValueEventListener vEl = new ValueEventListener()
    {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            progressBar.setVisibility(View.GONE);
            for(DataSnapshot complaintSnapshot : dataSnapshot.getChildren())
            {
                Complaints c = complaintSnapshot.getValue(Complaints.class);
                if(getDistance(c.getLat(),c.getLongitude(),lat,longitude)<300)
                    cr.add(c);
            }
            complaintsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError)
        {

        }
    };
    @Override
    public void onBackPressed()
    {
        Toast.makeText(this, "Click on Sign Out", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.add_complaint:startNewComplaintActivity();return true;
            case R.id.signout_button:
                FirebaseAuth.getInstance().signOut();
                startLoginActivity();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }


    public void startLoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startNewComplaintActivity()
    {
        Intent startNewComplaint = new Intent(this, newComplaintActivity.class);
        startActivity(startNewComplaint);
    }

}