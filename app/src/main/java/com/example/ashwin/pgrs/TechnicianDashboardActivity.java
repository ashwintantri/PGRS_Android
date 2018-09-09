package com.example.ashwin.pgrs;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.GONE;

public class TechnicianDashboardActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressBar progressBar;
    Technician technician;
    ComplaintsTechAdapter complaintsAdapter;
    RecyclerView recyclerView;
    ArrayList<Complaints> complaints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_dashboard);
        mAuth = FirebaseAuth.getInstance();
        complaints = new ArrayList<>();
        recyclerView = findViewById(R.id.tech_rv_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(TechnicianDashboardActivity.this));
        complaintsAdapter = new ComplaintsTechAdapter(TechnicianDashboardActivity.this,complaints);
        recyclerView.setAdapter(complaintsAdapter);
        progressBar = findViewById(R.id.technician_progress);
        getSupportActionBar().setTitle("Dashboard");
        Query query = FirebaseDatabase.getInstance().getReference("Technicians").orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail());
        query.addValueEventListener(valueEventListener);
        Query queryComplaint = FirebaseDatabase.getInstance().getReference();
        queryComplaint.addValueEventListener(valueEventListenerComplaint);

    }
    private double getDistance(double LAT1, double LONG1, double LAT2, double LONG2)
    {
        return 2 * 6371000 * Math.asin(Math.sqrt(Math.pow((Math.sin((LAT2 * (3.14159 / 180) - LAT1 * (3.14159 / 180)) / 2)), 2) + Math.cos(LAT2 * (3.14159 / 180)) * Math.cos(LAT1 * (3.14159 / 180)) * Math.sin(Math.pow(((LONG2 * (3.14159 / 180) - LONG1 * (3.14159 / 180)) / 2), 2))));
    }
    ValueEventListener valueEventListenerComplaint = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            progressBar.setVisibility(GONE);
            for(DataSnapshot ds:dataSnapshot.getChildren())
            {
                Complaints c = ds.getValue(Complaints.class);
                if(getDistance(technician.getLat(),technician.getLongitude(),c.getLat(),c.getLongitude())<300)
                {
                    complaints.add(c);
                }
            }
            complaintsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            progressBar.setVisibility(GONE);
            for(DataSnapshot ds:dataSnapshot.getChildren())
            {
                technician = ds.getValue(Technician.class);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.signout_button:mAuth.signOut();startActivity(new Intent(TechnicianDashboardActivity.this,LoginActivity.class));
            finish();return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item,menu);
        menu.findItem(R.id.add_complaint).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }
}
