package com.cresprit.mqtt.manager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.cresprit.mqtt.ui.IUpdateListener;
import com.cresprit.mqtt_sdk.MQTTSDK;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class UserManager{
	private static final String TAG = "UserManager";
	private static final String PROPERTY_USER_ID = "userid";
	private static final String PROPERTY_PASSWORD = "password";
	private static final String JSON_ID_EMAIL = "email";
	private static final String JSON_ID_PASSWORD = "password";
	private static final String JSON_ID_DATA = "data";
	private static final String JSON_ID_KEY = "key";
	private static final String HEADER_TYPE = "Content-Type";
	private static final String HEADER_APPLICATION_JSON = "application/json";
	private static String m_pId;
	private String m_pAuthKey;
	private static UserManager __instance;
	private static SharedPreferences mSharedPreferences;
	private IUpdateListener mListener = null;
	String m_pPasswd;
	Context context;
	
	public static UserManager getInstance(Context appContext) {
		if (__instance == null) {
			__instance = new UserManager(appContext);
			
		}

		if(mSharedPreferences == null)
			mSharedPreferences = appContext.getSharedPreferences(appContext.getPackageName(), Context.MODE_PRIVATE);
		return __instance;
	}
	
	public UserManager(Context ctx)
	{
		this.context=ctx;
	}	
	
	public UserManager()
	{
		
	}
	
	public UserManager(String _id, String _passwd){
		m_pId = _id;
		this.m_pPasswd = _passwd;
	}
	
	public void setAuthKey(String _key)
	{
		m_pAuthKey = _key;
	}
	
	public String getAuthKey()
	{
		return m_pAuthKey;
	}
	
	public void setDialogUpdateListener(IUpdateListener _listener)
	{
		mListener = _listener;
	}
	
	public String getUserId()
	{
		m_pId = getString( PROPERTY_USER_ID, "" );
		return m_pId;
	}
	
	public void setUserId(String _id)
	{
		m_pId = _id;
		setString(PROPERTY_USER_ID, _id);
	}	
	
	public String getPassword()
	{
		m_pPasswd = getString( PROPERTY_PASSWORD, "" ); 
		return m_pPasswd;
	}
	
	public void setPassword(String _password)
	{
		m_pPasswd = _password;
		setString(PROPERTY_PASSWORD, _password);
	}
	
	public String getString(String key, String defaultValue )
	{
		if( mSharedPreferences == null )
			return defaultValue;

		return mSharedPreferences.getString(key, defaultValue);
		
	}
	
	public void setString(String key, String value)
	{
		if( mSharedPreferences == null )
			return;
		
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(key, value);

		editor.commit();
	}
	
	
	public void doLogin()
	{
		LoginTask loginTask = new LoginTask();
		loginTask.execute("");
	}
	
	class LoginTask extends AsyncTask<String, Void, String> {
		
		String key=null;
		int responseCode = 0;
		
		@Override
		protected String doInBackground(String... params1) {
			key = MQTTSDK.AloohCloud_LogIn(m_pId, m_pPasswd);
			setAuthKey(key);
			return key;	
		}

		@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				if(mListener != null)
					mListener.update(IUpdateListener.SHOW_DIALOG, null);
				super.onPreExecute();
		}
	
		@Override
		protected void onPostExecute(String _key) {
			if(mListener != null)
				mListener.update(IUpdateListener.REMOVE_DIALOG, _key);
			super.onPostExecute(key);
		}
	}
}