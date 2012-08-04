package com.linkedin.localin.ININ;

public class Contact 
{
	private long _memberId;
	private String _firstName;
	private String _lastName;
	private String _headline;
	private String _region;
	private String _industry;
	private String _picUrl;
	
	private double _lat;
	private double _lng;
	
	private double _distance;
	
	public Contact(long memberId, 
			       String firstName, 
			       String lastName, 
			       String headline, 
			       String region, 
			       String industry, 
			       String picUrl,
			       double lat,
			       double lng)
	{
		_memberId = memberId;
		_firstName = firstName;
		_lastName = lastName;
		_headline = headline;
		_region = region;
		_industry = industry;
		_picUrl = picUrl;
		_lat = lat;
		_lng = lng;
	}
	
	public void setDistance(double distance)
	{
		this._distance = distance;
	}
	public double getDistance()
	{
		return _distance;
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
	

}
