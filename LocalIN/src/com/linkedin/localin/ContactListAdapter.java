package com.linkedin.localin;

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

public class ContactListAdapter extends BaseAdapter 
{
	private Activity activity;
	private ArrayList<Contact> data;
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public ContactListAdapter(Activity a, ArrayList<Contact> d) 
	{
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }
	
	@Override
	public int getCount() 
	{
		return data.size();
	}

	@Override
	public Object getItem(int position) 
	{
		return data.get(position);
	}

	@Override
	public long getItemId(int position) 
	{
		return data.get(position).getMemberId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View vi = convertView;
		if(convertView == null)
			vi = inflater.inflate(R.layout.list_row, null);
		
		TextView name = (TextView)vi.findViewById(R.id.name);
		TextView headline = (TextView)vi.findViewById(R.id.headline);
		TextView info = (TextView)vi.findViewById(R.id.info);
		TextView distance = (TextView)vi.findViewById(R.id.distance);
		ImageView thumb_image = (ImageView)vi.findViewById(R.id.list_image);
		
		Contact contact = data.get(position);
		name.setText(contact.getName());
		headline.setText(contact.getHeadline());
		info.setText(contact.getRegion() + " | " + contact.getIndustry());
		//distance.setText("" + contact.getLatitude() + ", " + contact.getLongitude());
		distance.setText(Math.round(contact.getDistance() * 100.0) / 100.0 + " mi");
		imageLoader.DisplayImage(contact.getPicUrl(), thumb_image);
		
		return vi;
	}

}
