package com.cresprit.mqtt.manager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cresprit.mqtt.ui.IUpdateListener;
import com.cresprit.mqtt_sdk.MQTTSDK;
import com.cresprit.mqtt_sdk.MQTTSDK.DeviceInfo;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.util.Log;

public class DeviceListManager{
	private String TAG = "SelectDeviceManager";
	public static String SELECT_DEVICE = "com.cresprit.mqtt.DeviceListActivity.Select_device";
	public static String SHARED_URL = "com.cresprit.mqtt.DeviceListManager.Shared_URL";
	private ArrayList<DeviceInfo> deviceList = null;

	
	private static DeviceListManager __instance;
	private Context context;
	private String mSelectDevice = null;
	private String mAuthKey = null;
	private DataSetObserver mObserver = null;
	
	private IUpdateListener mListener = null;
	static int retryCnt = 0;
	

	public static DeviceListManager getInstance(Context appContext) {
		if (__instance == null) {
			__instance = new DeviceListManager(appContext);
			
		}
		return __instance;
	}
	
	public DeviceListManager(Context ctx)
	{
		this.context=ctx;
	}
	
	public void setDialogUpdateListener(IUpdateListener _listener)
	{
		mListener = _listener;
	}
	
	public void setDeviceListObserverListener(DataSetObserver _observer) {
		mObserver = _observer;
	}
	
	public ArrayList<DeviceInfo> getDeviceList()
	{
		return deviceList;
	}
	
	public void setSelectDevice(String _device)
	{
		mSelectDevice = _device;
	}
	
	public String getSelectDevice()
	{
		return mSelectDevice;
	}
	
	
	public int getDeviceCount()
	{
		if(deviceList == null)
			return 0;
		return deviceList.size();
	}
	
	public void getDeviceList(String _authKey)
	{
		mAuthKey = _authKey;
		new DeviceList().execute(mAuthKey);
	}
	
	class DeviceList extends AsyncTask<String, Void, Boolean> {
		String auid = null;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//mListener.update(IUpdateListener.SHOW_DIALOG);
		}

		@Override
		protected Boolean doInBackground(String... key) {
			// TODO Auto-generated method stub
			deviceList = MQTTSDK.AloohCloud_GetDeviceList(key[0]);
			return true;
		}

		@Override
		protected void onPostExecute(Boolean _auid) {
			// TODO Auto-generated method stub
			if(mObserver != null)
				mObserver.onInvalidated();
			mListener.update(IUpdateListener.REMOVE_DIALOG, null);
			super.onPostExecute(_auid);
		}

	};	
	
}




