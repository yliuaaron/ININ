package com.linkedin.localin;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Contact implements Serializable, Comparable<Contact>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7634888692255240162L;
	
	private long _memberId;
	private String _id;
	private String _firstName;
	private String _lastName;
	private String _headline;
	private String _region;
	private String _industry;
	private String _picUrl;
	private Date _lastUpdate;
	
	private double _lat;
	private double _lng;
	
	private double _distance;
	
	public Contact(long memberId, 
				   String id,
			       String firstName, 
			       String lastName, 
			       String headline, 
			       String region, 
			       String industry, 
			       String picUrl,
			       double lat,
			       double lng,
			       String lastUpdate)
	{
		_memberId = memberId;
		_id = id;
		_firstName = firstName;
		_lastName = lastName;
		_headline = headline;
		_region = region;
		_industry = industry;
		_picUrl = picUrl;
		_lat = lat;
		_lng = lng;
		
		
		try 
		{
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			_lastUpdate = df.parse(lastUpdate);
		} 
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			_lastUpdate = new Date();
		}
	}
	
	public void setDistance(double distance)
	{
		this._distance = distance;
	}
	public double getDistance()
	{
		return _distance;
	}
	public String getId()
	{
		return _id;
	}
	
	public long getMemberId()
	{
		return _memberId;
	}
	public String getName()
	{
		return _firstName + " " + _lastName;
	}
	public String getHeadline()
	{
		return _headline;
	}
	public String getRegion()
	{
		return _region;
	}
	public String getIndustry()
	{
		return _industry;
	}
	public String getPicUrl()
	{
		return _picUrl;
	}
	public double getLatitude()
	{
		return _lat;
	}
	public double getLongitude()
	{
		return _lng;
	}
	public Date getLastUpdate()
	{
		return _lastUpdate;
	}

	@Override
	public int compareTo(Contact another) 
	{
		if(_distance < another.getDistance())
			return -1;
		else
			return 1;
	}

}
