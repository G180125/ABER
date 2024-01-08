package com.example.aber.Activities.Main.Fragment.Profile.AboutUs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aber.Adapters.ViewPageAdapter;
import com.example.aber.R;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

public class AboutUsActivity extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout layout;

    ImageView buttonBack;

    TextView[] dots;
    ViewPageAdapter adapter;
    WormDotsIndicator wormDotsIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        buttonBack = findViewById(R.id.back);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new ViewPageAdapter(this);

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);

        wormDotsIndicator = findViewById(R.id.worm_dots_indicator);
        wormDotsIndicator.attachTo(viewPager);

    }
}