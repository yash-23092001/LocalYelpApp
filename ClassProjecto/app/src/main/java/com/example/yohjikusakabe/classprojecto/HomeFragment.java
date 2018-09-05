package com.example.yohjikusakabe.classprojecto;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private String currentLocation_;
    private Double myLong_;
    private Double myLat_;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home2, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        TextView currentLocation = v.findViewById(R.id.textView5);
        myLong_ = mainActivity.getLongitude();
        myLat_ = mainActivity.getLatitude();
        // Geolocation .getLocality returning null for some reason? Hardcode for now.
        currentLocation_ = "Currently in New York! " + mainActivity.getLatitude() + " " + mainActivity.getLongitude();
        currentLocation.setText(currentLocation_);

        Button foodButton = v.findViewById(R.id.placesToEat);
        foodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Categories.class);
                intent.putExtra("Category","Food");
                startActivity(intent);
            }
        });

        Button transportation = v.findViewById(R.id.transportation);
        transportation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Categories.class);
                intent.putExtra("Category","Transportation");
                startActivity(intent);
            }
        });
        Button eventButton = v.findViewById(R.id.Events);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Categories.class);
                intent.putExtra("Category","Event");
                startActivity(intent);
            }
        });
        Button funStuff = v.findViewById(R.id.funStuff);
        funStuff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Categories.class);
                intent.putExtra("Category","Fun Stuff");
                startActivity(intent);
            }
        });

        Button random = v.findViewById(R.id.randomButton);
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),Categories.class);
                intent.putExtra("Category","Random");
                startActivity(intent);
            }
        });



        return v;
    }

}
