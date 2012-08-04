package com.linkedin.localin.ININ;

import java.util.Date;
import java.util.EnumSet;

import com.example.androidhive.ImageLoader;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.schema.Person;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends Activity 
{
	private TextView nameText;
	private TextView headlineText;
	private TextView infoText;
	private ImageView profileImage;
	private TextView locText;
	private TextView connText;
	private TextView commonText;
	
	private Button btnChat;
	
	private ImageLoader imageLoader;
	
	LinkedInApiClient client;
	LinkedInAccessToken token;
	
	Contact contact;

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
        connText = (TextView) findViewById(R.id.textView5);
        commonText = (TextView) findViewById(R.id.textView6);
        
        btnChat = (Button) findViewById(R.id.button1);
        btnChat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent data = new Intent();
				data.putExtra("contact", contact);
				setResult(RESULT_OK, data);
				finish();
				
			}
		});
        
        imageLoader = new ImageLoader(this.getApplicationContext());
        
        contact = (Contact)this.getIntent().getSerializableExtra("user");
        token = (LinkedInAccessToken)this.getIntent().getSerializableExtra("token");
        client = MainActivity.factory.createLinkedInApiClient(token);
        
        nameText.setText(contact.getName());
        headlineText.setText(contact.getHeadline());
        infoText.setText(contact.getRegion() + " | " + contact.getIndustry());
        Date now = new Date();
        Long diff = now.getTime() - contact.getLastUpdate().getTime();
        int minutes = (int)(diff / 1000 / 60) + 180;
        double distance = Math.round(contact.getDistance() * 100.0) / 100.0; 
        locText.setText(distance + " miles | " + minutes + " mins ago");
        
        imageLoader.DisplayImage(contact.getPicUrl(), profileImage);
        
        Person person = client.getProfileById(contact.getId(), EnumSet.of(
		        			ProfileField.NUM_CONNECTIONS,
		        			ProfileField.DISTANCE,
		        		    ProfileField.RELATION_TO_VIEWER_RELATED_CONNECTIONS,
		        			ProfileField.RELATION_TO_VIEWER,
		        			ProfileField.EDUCATIONS
		        		));
        
        connText.setText("" + person.getNumConnections());
        commonText.setText("" + person.getRelationToViewer().getRelatedConnections().getTotal());
        
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
