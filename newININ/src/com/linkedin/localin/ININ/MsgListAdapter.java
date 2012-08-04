package com.linkedin.localin.ININ;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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


public class MsgListAdapter extends BaseAdapter 
{
	private Activity activity;
	private ArrayList<Msg> data;
	private Contact owner;
	private Contact receiver;
	private static LayoutInflater inflater = null;
	private ImageLoader imageLoader;

	public MsgListAdapter(Activity a, ArrayList<Msg> d, Contact o, Contact r)
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
		Msg message = data.get(position);
		
		View vi = convertView;
		if(convertView == null)
		{
			if(message.getFromUserId() == owner.getMemberId())
				vi = inflater.inflate(R.layout.chat_row_right, null);
			else
				vi = inflater.inflate(R.layout.chat_row_left, null);
		}
		
		TextView msgText = (TextView) vi.findViewById(R.id.message);
		TextView timeText = (TextView) vi.findViewById(R.id.time);
		ImageView headImage = (ImageView) vi.findViewById(R.id.chat_image);
		msgText.setText(message.getMessage());
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timeText.setText(df.format(message.getTime()));
		
		if(message.getFromUserId() == owner.getMemberId())
			imageLoader.DisplayImage(owner.getPicUrl(), headImage);
		else
			imageLoader.DisplayImage(receiver.getPicUrl(), headImage);
		
		return vi;
	}
	
	

}