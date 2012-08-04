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


public class MessageListAdapter extends BaseAdapter 
{
	private Activity activity;
	private ArrayList<Message> data;
	private Contact owner;
	private Contact receiver;
	private static LayoutInflater inflater = null;
	private ImageLoader imageLoader;

	public MessageListAdapter(Activity a, ArrayList<Message> d, Contact o, Contact r)
	{
		activity = a;
		data = d;
		owner = o;
		receiver = r;
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
		return data.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		Message message = data.get(position);
		
		View vi = convertView;
		if(convertView == null)
		{
			if(message.getFromUserId() == owner.getMemberId())
				vi = inflater.inflate(R.layout.chat_row_right, null);
			else
				vi = inflater.inflate(R.layout.chat_row_left, null);
		}
		
		TextView msgText = (TextView) vi.findViewById(R.id.message);
		ImageView headImage = (ImageView) vi.findViewById(R.id.chat_image);
		
		
		
		// TODO Auto-generated method stub
		return null;
	}

}
