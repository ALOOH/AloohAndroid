package com.cresprit.mqtt.ui;
import java.util.ArrayList;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cresprit.mqtt.R;
import com.cresprit.mqtt.service.MQTTService;
import com.cresprit.mqtt.manager.DeviceListManager;
import com.cresprit.mqtt.manager.MessageManager;
import com.cresprit.mqtt_sdk.ISubscribeListener;
import com.cresprit.mqtt_sdk.MQTTSDK;
import com.cresprit.mqtt_sdk.MQTTSDK.DeviceInfo;
import com.cresprit.mqtt_sdk.MQTTSDK.KeyInfo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RemoteViews;

public class SubscribeActivity extends Activity {
	EditText edtFeed;
	Button btnSubscribe;
	String feed;
	MessageAdapter adapter;
	ListView tvMessages;
	int mSelectedDevice;
	Intent intent = null;
	MessageManager mgr = null;
	private DeviceInfo deviceInfo=null;
    ArrayList<KeyInfo> KeyList = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subscribe);
		Intent intentTemp = getIntent();
		
		tvMessages = (ListView)findViewById(R.id.listMessages);
		adapter = new MessageAdapter(SubscribeActivity.this);
		tvMessages.setAdapter(adapter);
		//adapter.registerDataSetObserver(observer);
			
		registerReceiver(mBroadcastReceiver, new IntentFilter(MessageManager.MSG_RECEIVE));
	
		
		intent = new Intent(SubscribeActivity.this, MQTTService.class);
		intent.putExtra(DeviceListManager.SELECT_DEVICE, mSelectedDevice);
        startService(intent);
	}
	
	private void showNotification(String text) {
		Notification n = new Notification();
				
		n.flags |= Notification.FLAG_SHOW_LIGHTS;
	  	n.flags |= Notification.FLAG_AUTO_CANCEL;

	    n.defaults = Notification.DEFAULT_ALL;
	    n.flags |= Notification.FLAG_ONGOING_EVENT;
		n.icon = R.drawable.alooh_logo_w9;
		n.when = System.currentTimeMillis();

		// Simply open the parent activity
		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, LoginActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews contentView = new RemoteViews(getPackageName(),R.layout.notify);
 

 
        n.contentView = contentView;
 
        n.contentIntent = pi;
		// Change the name of the notification here
		//n.setLatestEventInfo(this, "NotiTest", text, pi);


	}
	
	final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.i("", "received Broadcast");
			JSONObject data_streams;
			JSONArray data_points;
			JSONObject data;
			JSONObject  json;
			JSONArray jvalue;
			JSONObject jvalue2;
			String value = null;
			int nValue=0;
			
			String message = (String)intent.getCharSequenceExtra("message");
			mgr.getMessages().add(message);
			adapter.notifyDataSetChanged();
			tvMessages.setSelection(mgr.getMessages().size());
			
			try {
				json = new JSONObject(message);
				data_streams = json.getJSONObject("data");
				data_points = data_streams.getJSONArray("sensors");
				data = data_points.getJSONObject(0);
				jvalue = data.getJSONArray("data_points");
				jvalue2 = jvalue.getJSONObject(0);
				value = jvalue2.get("v").toString();				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			nValue = Integer.parseInt(value);
			Log.i("nValue",""+nValue);
		}
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//MQTTSDK.Disconnect();
		unregisterReceiver(mBroadcastReceiver);
		stopService(intent);
		

		super.onBackPressed();
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		

		super.onDestroy();
	}

			
	DataSetObserver observer = new DataSetObserver()
	{

		@Override
		public void onChanged() {
			// TODO Auto-generated method stub
			Log.i("","onChanged");
			super.onChanged();
		}

		@Override
		public void onInvalidated() {
			// TODO Auto-generated method stub
			Log.i("","onInvalidated");
			super.onInvalidated();
		}
		
	};

}