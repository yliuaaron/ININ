package com.linkedin.localin;

import java.util.Date;

import com.example.androidhive.ImageLoader;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends Activity 
{
	TextView nameText;
	TextView headlineText;
	TextView infoText;
	ImageView profileImage;
	TextView locText;
	
	ImageLoader imageLoader;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        
        nameText = (TextView) findViewById(R.id.textView1);
        headlineText = (TextView) findViewById(R.id.textView2);
        infoText = (TextView) findViewById(R.id.textView3);
        profileImage = (ImageView)findViewById(R.id.profile_image);
        locText = (TextView) findViewById(R.id.textView4);
        
        imageLoader = new ImageLoader(this.getApplicationContext());
        
        Contact user = (Contact)this.getIntent().getSerializableExtra("user");
        
        nameText.setText(user.getName());
        headlineText.setText(user.getHeadline());
        infoText.setText(user.getRegion() + " | " + user.getIndustry());
        Date now = new Date();
        Long diff = now.getTime() - user.getLastUpdate().getTime();
        int minutes = (int)(diff / 1000 / 60) + 180;
        double distance = Math.round(user.getDistance() * 100.0) / 100.0; 
        locText.setText(distance + " miles | " + minutes + " mins ago");
        
        imageLoader.DisplayImage(user.getPicUrl(), profileImage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_profile, menu);
        return true;
    }
}
