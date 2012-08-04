package com.linkedin.localin.ININ;

import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import com.example.androidhive.ImageLoader;
import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.constant.ApplicationConstants;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.schema.HttpHeader;
import com.google.code.linkedinapi.schema.Person;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity implements OnClickListener
{
	private TextView nameText;
	private TextView headlineText;
	private TextView infoText;
	private ImageView profileImage;
	private TextView locText;
	private TextView connText;
	private TextView commonText;
	private TextView sumText;
	private TextView skillText;
	
	private Button btnChat;
	private Button btnConnect;
	
	private ImageLoader imageLoader;
	
	LinkedInApiClient client;
	LinkedInAccessToken token;
	String invitationHeader;
	
	Contact contact;
	Person addInfo;

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
        sumText = (TextView)findViewById(R.id.textView10);
        skillText=(TextView)findViewById(R.id.textView12);
        
        btnChat = (Button) findViewById(R.id.button1);
        btnConnect = (Button) findViewById(R.id.button2);
        
        final Intent receive = this.getIntent();
        contact = (Contact)receive.getSerializableExtra("user");
        token = (LinkedInAccessToken)this.getIntent().getSerializableExtra("token");
        client = MainActivity.factory.createLinkedInApiClient(token);
        
        final Context context = this;
        btnChat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(context, ChatActivity.class);
				
				Contact currentUserContact = (Contact) receive.getSerializableExtra("current");
    			intent.putExtra("contact", contact);
    			intent.putExtra("current", currentUserContact);
    			startActivity(intent);
				
				
				//setResult(RESULT_OK, data);
				//finish();
				
			}
		});
        
        imageLoader = new ImageLoader(this.getApplicationContext());
        
        
        
        nameText.setText(contact.getName());
        headlineText.setText(contact.getHeadline());
        infoText.setText(contact.getRegion() + " | " + contact.getIndustry());
        Date now = new Date();
        Long diff = now.getTime() - contact.getLastUpdate().getTime();
        int minutes = (int)(diff / 1000 / 60) + 180;
        double distance = Math.round(contact.getDistance() * 100.0) / 100.0; 
        locText.setText(distance + " miles | " + minutes + " mins ago");
        
        imageLoader.DisplayImage(contact.getPicUrl(), profileImage);
        
        addInfo = client.getProfileById(contact.getId(), EnumSet.of(
        		            ProfileField.SUMMARY,
        		            ProfileField.SKILLS,
        				    ProfileField.API_STANDARD_PROFILE_REQUEST_HEADERS,
		        			ProfileField.NUM_CONNECTIONS,
		        			ProfileField.DISTANCE,
		        		    ProfileField.RELATION_TO_VIEWER_RELATED_CONNECTIONS,
		        			ProfileField.RELATION_TO_VIEWER,
		        			ProfileField.EDUCATIONS
		        		));
        
        List<HttpHeader> headers = addInfo.getApiStandardProfileRequest().getHeaders().getHttpHeaderList();
        for(int i = 0; i < headers.size(); i++)
        {
        	if(headers.get(i).getName().equals(ApplicationConstants.AUTH_HEADER_NAME))
        	{
        		invitationHeader = headers.get(i).getValue();
        		break;
        	}
        }
        
        Log.d("info", invitationHeader);
        
        String skillStr = "";
        for(int i = 0; i < addInfo.getSkills().getSkillList().size(); i++)
        	skillStr += addInfo.getSkills().getSkillList().get(i).getSkill().getName() + ", ";
        skillStr = skillStr.trim();
        skillStr = skillStr.substring(0, skillStr.length() - 1);
        
        
        connText.setText("" + addInfo.getNumConnections());
        commonText.setText("" + addInfo.getRelationToViewer().getRelatedConnections().getTotal());
        sumText.setText(addInfo.getSummary());
        skillText.setText(skillStr);
        
        Log.d("info", "numConnection: " + addInfo.getNumConnections());
        Log.d("info", "distance: " + addInfo.getDistance());
        Log.d("info", "common: " + addInfo.getRelationToViewer().getRelatedConnections().getTotal());
        
        if(addInfo.getDistance() == 1)
        	btnConnect.setEnabled(false);
        else
        {
        	btnConnect.setOnClickListener(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_profile, menu);
        return true;
    }

	@Override
	public void onClick(View v) 
	{
		client.sendInviteById(contact.getId(), 
				"Invitation to Connect",
				"Please join my professional network on LinkedIn.",
				invitationHeader);
		Log.d("info", "invitation sent!");
		Toast.makeText(this, "Invitation Send!", Toast.LENGTH_LONG).show();
		
	}
}
