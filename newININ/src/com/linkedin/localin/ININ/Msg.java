package com.linkedin.localin.ININ;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Msg 
{
	private long _id;
	private long _from;
	private long _to;
	private String _message;
	private Date _time;
	private int _retrieved;
	
	public Msg(long id, long from, long to, String message, String time, int retrieved)
	{
		_id = id;
		_from = from;
		_to = to;
		_message = message;
		_retrieved = retrieved;
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try
		{
			_time = df.parse(time);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public long getId()
	{
		return _id;
	}
	public long getFromUserId()
	{
		return _from;
	}
	public long getToUserId()
	{
		return _to;
	}
	public String getMessage()
	{
		return _message;
	}
	public Date getTime()
	{
		return _time;
	}
	public int getRetrieved()
	{
		return _retrieved;
	}

}
