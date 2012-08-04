package com.linkedin.localin.ININ;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ChatActivity extends Activity implements OnClickListener
{
	private Contact currentUser;
	private Contact contact;
	
	private TextView titleText;
	private Button btnSend;
	private EditText contentText;
	private ListView chatList;
	
	private ArrayList<Message> messages;
	private MessageListAdapter adapter;
	
	private HttpClient httpclient = new DefaultHttpClient();

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        
        contact = (Contact)this.getIntent().getSerializableExtra("contact");
        currentUser = (Contact)this.getIntent().getSerializableExtra("current");
        titleText = (TextView)findViewById(R.id.textView1);
        btnSend = (Button)findViewById(R.id.button1);
        chatList = (ListView) findViewById(R.id.listView1);
        contentText = (EditText) findViewById(R.id.editText1);
        
        titleText.setText("Chat with " + contact.getName());
        
        btnSend.setOnClickListener(this);
        
        messages = new ArrayList<Message>();
        adapter = new MessageListAdapter(this, messages, currentUser, contact);
        chatList.setAdapter(adapter);
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
		contentText.setText("");
		if(message.length() != 0)
		{
			HttpPost mpost = new HttpPost("http://aaronplex.net/project/localin/send.php");
			String messageid = null;
			try
			{
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("from", "" + currentUser.getMemberId()));
				params.add(new BasicNameValuePair("to", "" + contact.getMemberId()));
				params.add(new BasicNameValuePair("message", message));
				mpost.setEntity(new UrlEncodedFormEntity(params));
				
				HttpResponse response = httpclient.execute(mpost);
				if(response.getStatusLine().getStatusCode() == 200)
				{
					BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    			StringBuilder sb = new StringBuilder();
	    			String line;
	    			while((line = br.readLine()) != null)
	    			{
	    				sb.append(line + "\n");
	    				//Log.d("info", line);
	    			}
	    			br.close();
	    			Log.d("info", sb.toString());
	    			messageid = sb.toString().trim();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Message m = new Message(Long.parseLong(messageid), currentUser.getMemberId(), contact.getMemberId(), message, df.format(new Date()), 0);
			messages.add(m);
			
			adapter.notifyDataSetChanged();
		}
		
	}
}
