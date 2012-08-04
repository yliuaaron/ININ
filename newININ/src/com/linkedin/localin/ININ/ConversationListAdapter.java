package com.linkedin.localin.ININ;

import java.util.ArrayList;

import com.example.androidhive.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ConversationListAdapter extends BaseAdapter 
{
	private Activity activity;
	
	
	
	
	private ArrayList<Contact> ppl;
	private ArrayList<Msg> message;
	
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public ConversationListAdapter(Activity a, ArrayList<Contact> contact, ArrayList<Msg> message) 
	{
        activity = a;
        //data = d;
        this.ppl = contact;
        this.message = message;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }
	
	@Override
	public int getCount() 
	{
		return ppl.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return ppl.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View vi = convertView;
		if(convertView == null)
			vi = inflater.inflate(R.layout.conversation, null);
		
		TextView name = (TextView)vi.findViewById(R.id.name);
		TextView msgPlace = (TextView)vi.findViewById(R.id.message);
		TextView timePlace = (TextView)vi.findViewById(R.id.timestamp);
		ImageView thumb_image = (ImageView)vi.findViewById(R.id.list_image);
		
		Contact contact = ppl.get(position);
		Msg msg = message.get(position);
		
		
		name.setText(contact.getName());
		msgPlace.setText(msg.getMessage());
		timePlace.setText(msg.getTime().toString());
		
		//distance.setText("" + contact.getLatitude() + ", " + contact.getLongitude());
		
		imageLoader.DisplayImage(contact.getPicUrl(), thumb_image);
		
		return vi;
	}

}
