package com.blue.zaffico;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{


    ImageView avartaImageView;
    ImageView logout;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        avartaImageView = view.findViewById(R.id.avartaImageView);
        logout = view.findViewById(R.id.logout);
        avartaImageView.setOnClickListener(this);
        logout.setOnClickListener(this);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.avartaImageView:
                if(logout.getVisibility() == View.VISIBLE){
                    logout.setVisibility(View.INVISIBLE);
                }else{
                    logout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.logout:
                startActivity(new Intent(getContext(), LoginActivity.class));

        }
    }
}
