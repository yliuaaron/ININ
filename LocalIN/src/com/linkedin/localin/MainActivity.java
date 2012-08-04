package com.linkedin.localin;
import eu.erikw.*;
import eu.erikw.PullToRefreshListView.OnRefreshListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;


import com.google.code.linkedinapi.client.LinkedInApiClient;
import com.google.code.linkedinapi.client.LinkedInApiClientFactory;
import com.google.code.linkedinapi.client.enumeration.ProfileField;
import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthService;
import com.google.code.linkedinapi.client.oauth.LinkedInOAuthServiceFactory;
import com.google.code.linkedinapi.schema.Person;

public class MainActivity extends Activity 
{
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private static final int LOCATION_WAIT_TIME = 1000 * 10;
	
	public static final String CONSUMER_KEY = "rkqpdhrfmlbn";
    public static final String CONSUMER_SECRET = "U5ceoqmJfqrTTH93";
    public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-linkedin";
    public static final String OAUTH_CALLBACK_HOST = "callback";
    public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;
    
    public static final LinkedInOAuthService oAuthService = LinkedInOAuthServiceFactory.getInstance().createLinkedInOAuthService(CONSUMER_KEY, CONSUMER_SECRET);
    public static final LinkedInApiClientFactory factory = LinkedInApiClientFactory.newInstance(CONSUMER_KEY, CONSUMER_SECRET);
    LinkedInApiClient client;
    LinkedInAccessToken accessToken;
    
    private Person currentUser;
    private HttpClient httpclient = new DefaultHttpClient();
    
    private Location bestLocation;
	private String locationProvider = LocationManager.NETWORK_PROVIDER;
	private LocationManager locationManager;
	private long obtainLocationStartTime;
	
	private Context context;
	
	private boolean locationUpdated = false;
	private PullToRefreshListView listView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	if (android.os.Build.VERSION.SDK_INT > 9) 
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        context = this;
        
        // load last-time's location from application memory
        // bestLocation = ...
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if(bestLocation == null)
        	bestLocation = locationManager.getLastKnownLocation(locationProvider);
        
        
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, 0);
        
        TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
        tabHost.setup();
        
        TabSpec tspec1 = tabHost.newTabSpec("First Tab");
        tspec1.setIndicator("One");
        tspec1.setContent(R.id.tab1);
        tabHost.addTab(tspec1);
        
        TabSpec tspec2 = tabHost.newTabSpec("Second Tab");
        tspec2.setIndicator("Two");
        tspec2.setContent(R.id.tab2);
        tabHost.addTab(tspec2);
        
        TabSpec tspec3 = tabHost.newTabSpec("Third Tab");
        tspec3.setIndicator("Three");
        tspec3.setContent(R.id.tab3);
        tabHost.addTab(tspec3);
        
//        ArrayList<Contact> contacts = new ArrayList<Contact>();
//        for(int i = 0; i < 3; i++)
//        {
//        	Contact contact = new Contact("123", "Yuchen Aaron", "Liu", "Intern", 
//        			"BAY", "CS", 
//        			"http://m3.licdn.com/mpr/mprx/0_oLg_hd40zZMcBF1Jo3YphwIAB4aFc3iJeXWphER3ejVcH5qMQiS7mo7fMnm4N6GzI6xgfDL35ii4", 
//        			33.0, 118.0);
//        	contacts.add(contact);
//        }
//        ListView listView = (ListView)findViewById(R.id.listView1);
//        ContactListAdapter adapter = new ContactListAdapter(this, contacts);
//        listView.setAdapter(adapter);
        listView = (PullToRefreshListView) findViewById(R.id.listView1);
        listView.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				queryNearbyUsers();
				listView.onRefreshComplete();
				// Your code to refresh the list contents goes here

				// Make sure you call listView.onRefreshComplete()
				// when the loading is done. This can be done from here or any
				// other place, like on a broadcast receive from your loading
				// service or the onPostExecute of your AsyncTask.

				// For the sake of this sample, the code will pause here to
				// force a delay when invoking the refresh
//				listView.postDelayed(new Runnable() {
//					@Override
//					public void run() {
//						listView.onRefreshComplete();
//					}
//				}, 2000);
//				Toast.makeText(mContext, "fuck you ", Toast.LENGTH_LONG).show();
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	Log.d("info", "on activity result!");
    	if(requestCode == 0)
    	{
    		if(resultCode == RESULT_OK)
    		{
    			accessToken = (LinkedInAccessToken)data.getSerializableExtra("token");
    			client = factory.createLinkedInApiClient(accessToken);
    			currentUser = client.getProfileForCurrentUser(
    					EnumSet.of(
    							ProfileField.FIRST_NAME, 
    							ProfileField.LAST_NAME,
    							ProfileField.ID,
    							ProfileField.HEADLINE,
    							ProfileField.LOCATION,
    							ProfileField.INDUSTRY,
    							ProfileField.PICTURE_URL,
    							ProfileField.PUBLIC_PROFILE_URL,
    							ProfileField.SITE_STANDARD_PROFILE_REQUEST_URL));
    			TextView textView = (TextView) findViewById(R.id.textView2);
    			textView.setText("Hello " + currentUser.getFirstName() + "!");
    			Log.d("info", currentUser.getPublicProfileUrl());
    		    Log.d("info", currentUser.getSiteStandardProfileRequest().getUrl());
    			
    			Log.d("info", currentUser.toString());
    			
    			obtainLocation();
    		}
    	}
    }
    @Override
    protected void onResume()
    {
    	super.onResume();
    	Log.d("info", "on resume!");
    }
    
    public void obtainLocation()
    {
    	TextView textView = (TextView)findViewById(R.id.textView3);
    	textView.setText("locating....");
    	
    	obtainLocationStartTime = Calendar.getInstance().getTimeInMillis();
    	Log.d("location", "starting getting location...");
    	
    	
    	
    	LocationListener locationListener = new LocationListener()
    	{
    		protected boolean isBetterLocation(Location location, Location currentBestLocation)
    		{
    			if (currentBestLocation == null) {
    		        // A new location is always better than no location
    		        return true;
    		    }

    		    // Check whether the new location fix is newer or older
    		    long timeDelta = location.getTime() - currentBestLocation.getTime();
    		    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    		    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    		    boolean isNewer = timeDelta > 0;

    		    // If it's been more than two minutes since the current location, use the new location
    		    // because the user has likely moved
    		    if (isSignificantlyNewer) {
    		        return true;
    		    // If the new location is more than two minutes older, it must be worse
    		    } else if (isSignificantlyOlder) {
    		        return false;
    		    }

    		    // Check whether the new location fix is more or less accurate
    		    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
    		    boolean isLessAccurate = accuracyDelta > 0;
    		    boolean isMoreAccurate = accuracyDelta < 0;
    		    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

    		    // Check if the old and new location are from the same provider
    		    boolean isFromSameProvider = isSameProvider(location.getProvider(),
    		            currentBestLocation.getProvider());

    		    // Determine location quality using a combination of timeliness and accuracy
    		    if (isMoreAccurate) {
    		        return true;
    		    } else if (isNewer && !isLessAccurate) {
    		        return true;
    		    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
    		        return true;
    		    }
    		    return false;
    		}
    		
    		private boolean isSameProvider(String provider1, String provider2) {
    		    if (provider1 == null) {
    		      return provider2 == null;
    		    }
    		    return provider1.equals(provider2);
    		}

			@Override
			public void onLocationChanged(Location location) 
			{
				TextView textView = (TextView)findViewById(R.id.textView3);
//				textView.setText("lat: " + location.getLatitude() 
//						+ ", lng: " + location.getLongitude()
//						+ ", acc: " + location.getAccuracy());
				Log.d("location", "location changed! " + location.getLatitude() + "," + location.getLongitude() + 
						", acc:" + location.getAccuracy());
				
				if(isBetterLocation(location, bestLocation))
				{
					bestLocation = location;
					textView.setText("lat: " + location.getLatitude() 
					+ ", lng: " + location.getLongitude()
					+ ", acc: " + location.getAccuracy());
					
					if(!locationUpdated)
					{
						logCurrentLocation();
						locationUpdated = true;
						
						queryNearbyUsers();
					}
					
				}
				
				if(Calendar.getInstance().getTimeInMillis() - obtainLocationStartTime > LOCATION_WAIT_TIME)
				{
					if(bestLocation == null)
						obtainLocationStartTime = Calendar.getInstance().getTimeInMillis();
					else
					{
						Log.d("location", "stop getting location updates");
						locationManager.removeUpdates(this);
					}
				}
			}

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
    	};
    	
    	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, 
    			locationListener);
    	
    }
    
    private void logCurrentLocation()
    {
    	HttpPost logpost = new HttpPost("http://aaronplex.net/project/localin/log.php");
    	try
    	{
    		List<NameValuePair> params = new ArrayList<NameValuePair>();
    		String url = currentUser.getSiteStandardProfileRequest().getUrl();
    		Long mid = Long.parseLong(Uri.parse(url).getQueryParameter("key"));
    		params.add(new BasicNameValuePair("id", "" + mid));
    		params.add(new BasicNameValuePair("first", currentUser.getFirstName()));
    		params.add(new BasicNameValuePair("last", currentUser.getLastName()));
    		params.add(new BasicNameValuePair("headline", currentUser.getHeadline()));
    		params.add(new BasicNameValuePair("location", currentUser.getLocation().getName()));
    		params.add(new BasicNameValuePair("industry", currentUser.getIndustry()));
    		params.add(new BasicNameValuePair("pic", currentUser.getPictureUrl()));
    		params.add(new BasicNameValuePair("lat", "" + bestLocation.getLatitude()));
    		params.add(new BasicNameValuePair("lng", "" + bestLocation.getLongitude()));
    		logpost.setEntity(new UrlEncodedFormEntity(params));
    		
    		HttpResponse response = httpclient.execute(logpost);
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
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		Log.d("info", "http post error!");
    	}
    }
    
    private void queryNearbyUsers()
    {
    	HttpGet queryget = new HttpGet("http://aaronplex.net/project/localin/q.php?lat=" + bestLocation.getLatitude() + "&lng=" + bestLocation.getLongitude());
    	try
    	{
    		HttpResponse response = httpclient.execute(queryget);
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
    			
    			JSONArray array = (JSONArray)JSONValue.parse(sb.toString());
    			loadNearbyUsers(array);
    			
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		Log.d("info", "http get error");
    	}
    	
    }
    
    private void loadNearbyUsers(JSONArray array)
    {
    	ArrayList<Contact> contacts = new ArrayList<Contact>();
    	for(int i = 0; i < array.size(); i++)
    	{
    		JSONObject user = (JSONObject)array.get(i);
    		Contact contact = new Contact(
    				              Long.parseLong((String)user.get("memberId")),
    				              (String)user.get("id"),
    				              (String)user.get("firstName"),
    				              (String)user.get("lastName"),
    				              (String)user.get("headline"),
    				              (String)user.get("region"),
    				              (String)user.get("industry"),
    				              (String)user.get("pictureUrl"),
    				              Double.parseDouble((String)user.get("lat")),
    				              Double.parseDouble((String)user.get("lng")),
    				              (String)user.get("timestamp"));
    		contact.setDistance(distanceByLatLng(bestLocation.getLatitude(), bestLocation.getLongitude(),
    							contact.getLatitude(), contact.getLongitude()));
    		contacts.add(contact);		            		  
    	}
    	
    	ContactListAdapter adapter = new ContactListAdapter(this, contacts);
        listView.setAdapter(adapter);
        
        listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
			{
				Contact contact = (Contact)parent.getItemAtPosition(position);
				Intent intent = new Intent(context, ProfileActivity.class);
				intent.putExtra("user", contact);
				startActivity(intent);
				
			}
		});
    }
    
    public static double distanceByLatLng(double lat1, double lng1, double lat2, double lng2)
    {
    	double R = 6371;
    	double toRad = Math.PI / 180.0;
    	double dLat = (lat2 - lat1) * toRad;
    	double dLng = (lng2 - lng1) * toRad;
    	double rLat1 = lat1 * toRad;
    	double rLat2 = lat2 * toRad;
    	
    	double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2) + Math.sin(dLng / 2.0) * Math.sin(dLng / 2.0) * Math.cos(rLat1) * Math.cos(rLat2);
    	double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    	double d = R * c;
    	
    	return d * 0.621371; // km to miles
    	
//    	double convert = Math.PI / 180.0;
//    	double t1 = Math.sin(lat1 * convert);
//    	double t2 = Math.cos(lat1 * convert);
//    	double t3 = Math.cos(lng1 * convert - lng1 * convert);
//    	double t4 = t2 * t3;
//    	double t5 = t1 + t4;
//    	double rad_dist = Math.atan(-t5 / Math.sqrt(-t5 * t5 + 1)) + 2 * Math.atan(1);
//    	return rad_dist * 3437.74677 * 1.1508;
    }
}
