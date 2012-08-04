package com.linkedin.localin;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ChatActivity extends Activity implements OnClickListener
{
	private Contact currentUser;
	private Contact contact;
	
	private TextView titleText;
	private Button btnSend;
	private TextView contentText;
	
	private HttpClient httpclient = new DefaultHttpClient();

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        contact = (Contact)this.getIntent().getSerializableExtra("contact");
        titleText = (TextView)findViewById(R.id.textView1);
        btnSend = (Button)findViewById(R.id.button1);
        
        titleText.setText("Chat with " + contact.getName());
        
        btnSend.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat, menu);
        return true;
    }

	@Override
	public void onClick(View v) 
	{
		String message = contentText.getText().toString();
		if(message.length() != 0)
		{
			 
		}
		
	}
}
