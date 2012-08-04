package com.linkedin.localin;

import java.util.Date;
import java.util.EnumSet;

import com.example.androidhive.ImageLoader;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.schema.Person;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends Activity 
{
	private TextView nameText;
	private TextView headlineText;
	private TextView infoText;
	private ImageView profileImage;
	private TextView locText;
	
	private ImageLoader imageLoader;
	
	LinkedInApiClient client;
	LinkedInAccessToken token;

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
        token = (LinkedInAccessToken)this.getIntent().getSerializableExtra("token");
        client = MainActivity.factory.createLinkedInApiClient(token);
        
        nameText.setText(user.getName());
        headlineText.setText(user.getHeadline());
        infoText.setText(user.getRegion() + " | " + user.getIndustry());
        Date now = new Date();
        Long diff = now.getTime() - user.getLastUpdate().getTime();
        int minutes = (int)(diff / 1000 / 60) + 180;
        double distance = Math.round(user.getDistance() * 100.0) / 100.0; 
        locText.setText(distance + " miles | " + minutes + " mins ago");
        
        imageLoader.DisplayImage(user.getPicUrl(), profileImage);
        
        Person person = client.getProfileById(user.getId(), EnumSet.of(
		        			ProfileField.NUM_CONNECTIONS,
		        			ProfileField.DISTANCE,
		        		    ProfileField.RELATION_TO_VIEWER_RELATED_CONNECTIONS,
		        			ProfileField.RELATION_TO_VIEWER,
		        			ProfileField.EDUCATIONS
		        		));
        
        Log.d("info", "numConnection: " + person.getNumConnections());
        Log.d("info", "distance: " + person.getDistance());
        Log.d("info", "common: " + person.getRelationToViewer().getRelatedConnections().getTotal());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_profile, menu);
        return true;
    }
}
