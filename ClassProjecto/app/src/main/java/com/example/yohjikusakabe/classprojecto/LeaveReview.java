package com.example.yohjikusakabe.classprojecto;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LeaveReview extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_review);

        db = FirebaseFirestore.getInstance();

        Button locationDoesNotExist = findViewById(R.id.reviewNoExist);
        locationDoesNotExist.setOnClickListener(this);
        Button search = findViewById(R.id.Search);
        search.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == findViewById(R.id.reviewNoExist)) {
            Intent intent = new Intent(LeaveReview.this,WriteReview.class);
            startActivity(intent);
        }
        if (view == findViewById(R.id.Search)) {
            // Receive whats in search bar.
            SearchView searchView = findViewById(R.id.searchBarLeaveReview);
            final String locationName = searchView.getQuery().toString();

            // Check if it exists in DB
            db.collection("reviews").whereEqualTo("Name",locationName)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                         if (task.isSuccessful()) {
                                                           if (task.getResult().isEmpty()) {
                                                               Toast.makeText(LeaveReview.this, "Does not exist in Reviews. Please create new location!", Toast.LENGTH_SHORT).show();
                                                           } else {
                                                               Intent intent = new Intent(LeaveReview.this,WriteReview.class);
                                                               intent.putExtra("Name",locationName);
                                                               startActivity(intent);
                                                           }
                                                         } else {
                                                             Toast.makeText(LeaveReview.this, "Fetch failed", Toast.LENGTH_SHORT).show();
                                                         }
                                                     }
                                                 });
        }
    }
}
