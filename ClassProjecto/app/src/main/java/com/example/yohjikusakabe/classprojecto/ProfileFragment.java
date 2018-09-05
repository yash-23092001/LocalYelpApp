package com.example.yohjikusakabe.classprojecto;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private TextView hiUser;
    private Button writeReview;
    private Button myReviews_;
    private View v;

    public MainActivity mainActivityProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        mainActivityProfile = (MainActivity) getActivity();
        String userName = mainActivityProfile.userNameProfile;

        hiUser = v.findViewById(R.id.hiUserTextView);
        hiUser.setText("Hi " + userName + "!");

//        WriteReview button
        writeReview = v.findViewById(R.id.writeReviewProfile);
        writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(getActivity(),LeaveReview.class);
                    startActivity(intent);
            }
        });

          // My reviews button
        myReviews_ = v.findViewById(R.id.myReviewsProfile);
        myReviews_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MyReviews.class);
                startActivity(intent);
            }
        });


        return v;
    }


}
