package com.linkedin.localin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.code.linkedinapi.client.oauth.LinkedInAccessToken;
import com.google.code.linkedinapi.client.oauth.LinkedInRequestToken;

public class LoginActivity extends Activity 
{
	protected LinkedInRequestToken requestToken;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	Log.d("info", "on create!");
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        
        WebView webview = (WebView)findViewById(R.id.webView1);
        webview.setVisibility(View.GONE);
        
        Button btnLogin = (Button) findViewById(R.id.button1);
        btnLogin.setText("Login with LinkedIn");
        btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
//				service = new ServiceBuilder()
//								        	.provider(LinkedInApi.class)
//								        	.apiKey(MainActivity.CONSUMER_KEY)
//								        	.apiSecret(MainActivity.CONSUMER_SECRET)
//								        	.callback(MainActivity.OAUTH_CALLBACK_URL)
//								        	.build();
				
				WebView webView = (WebView) findViewById(R.id.webView1);
				Button btnLogin = (Button) findViewById(R.id.button1);
				btnLogin.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
				
				
				requestToken = MainActivity.oAuthService.getOAuthRequestToken(MainActivity.OAUTH_CALLBACK_URL);
				String authURL = requestToken.getAuthorizationUrl();
				//Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(authURL));
				//startActivity(i);
				
				webView.setWebViewClient(new WebViewClient()
				{
					public boolean shouldOverrideUrlLoading(WebView view, String url)
					{
						if(url.startsWith(MainActivity.OAUTH_CALLBACK_URL))
						{
							String verifier = Uri.parse(url).getQueryParameter("oauth_verifier");
							LinkedInAccessToken accessToken = MainActivity.oAuthService.getOAuthAccessToken(requestToken, verifier);
					    	Intent data = new Intent();
					    	data.putExtra("token", accessToken);
					    	setResult(RESULT_OK, data);
					    	finish();
					    	
					    	view.setVisibility(View.GONE);
					    	return false;
						}
						
						view.loadUrl(url);
						return false;
					}
				});
				webView.loadUrl(authURL);
				
//				webView.loadUrl("http://www.google.com");
				
				
				//Intent data = new Intent();
				//setResult(RESULT_OK, data);
				//finish();
				
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }
    
    @Override
    protected void onNewIntent(Intent intent)
    {
    	Log.d("info", "on new intent");
//    	String verifier = intent.getData().getQueryParameter("oauth_verifier");
//    	Token accessToken = service.getAccessToken(requestToken, new Verifier(verifier));
//    	Intent data = new Intent();
//    	data.putExtra("token", accessToken);
//    	setResult(RESULT_OK, data);
//    	finish();
    }
}
