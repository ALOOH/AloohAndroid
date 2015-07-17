package com.cresprit.mqtt.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.cresprit.mqtt.R;
import com.cresprit.mqtt.manager.*;
import com.cresprit.mqtt_sdk.MQTTSDK;
import com.cresprit.mqtt_sdk.MQTTSDK.DeviceInfo;
import com.cresprit.mqtt_sdk.MQTTSDK.KeyInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PublishActivity extends Activity implements OnCheckedChangeListener {
	private int DIALOG_LOGIN = 0;
	private int DIALOG_QUIT = 1;
	private int DIALOG_DEVICELIST = 2;
	private int DIALOG_NODEVICE = 3;
	EditText edtMessage;
	TextView tvFeedId;
	TextView tvProductName;
	TextView tvDeviceName;
	
	Button btnPublish;
	MqttClient client;
	DeviceListManager deviceListMgr;
	DeviceInfo deviceInfo;
	UserManager userMgr;
	//	ConnectionMgr connMgr;
	
	String feed;
	String message;
	int mSelectedDevice = 0;;
	CharSequence extra_text = null;
	ArrayList<KeyInfo> KeyList = null;
	boolean isDataOnlyChecked = false;
	CheckBox cbDataOnly;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.publish);
		Intent intent = getIntent();
		
        Uri stream = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
        Log.i("", "URI : "+stream);
        extra_text = intent.getCharSequenceExtra(Intent.EXTRA_TEXT);
        Log.i("", "extra_text : "+extra_text);		

    	deviceListMgr = DeviceListManager.getInstance(this);

		cbDataOnly = (CheckBox)findViewById(R.id.check_dataonly);
		cbDataOnly.setOnCheckedChangeListener(this);	
   	
        edtMessage = (EditText)findViewById(R.id.edtMessage);
        tvFeedId = (TextView)findViewById(R.id.feed_id);
		tvProductName = (TextView)findViewById(R.id.product_name);
		tvDeviceName = (TextView)findViewById(R.id.device_name);
        if(extra_text != null)//공유로 진입했을 경우 로그인을 통해 Auth Key을 저장한다.
        {
        	userMgr = UserManager.getInstance(PublishActivity.this);
        	String passwd = userMgr.getPassword();
        	String id = userMgr.getUserId();
        	
        	if(passwd == null || "".equals(passwd))
        	{
        		showDialog(DIALOG_QUIT);
        		return;
        	}

        	userMgr = UserManager.getInstance(this);
        	userMgr.setDialogUpdateListener(listener);
        	userMgr.doLogin();
        }
        else
        {
    		mSelectedDevice = intent.getIntExtra(DeviceListManager.SELECT_DEVICE,  0);
    		
    		deviceInfo = deviceListMgr.getDeviceList().get(mSelectedDevice);
       
			String contentUrl = intent.getStringExtra(DeviceListManager.SHARED_URL);
			//connMgr = new ConnectionMgr(PublishActivity.this);
	        
			if(contentUrl != null)
				edtMessage.setText(contentUrl);
			else
				edtMessage.setText("");
	
			tvFeedId.setText(deviceInfo.getTopicId());
			tvProductName.setText(deviceInfo.getProductname());
			tvDeviceName.setText(deviceInfo.getDeviceName());
        }
        
		btnPublish = (Button)findViewById(R.id.btnPublish);
		btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            	
            	message = edtMessage.getText().toString();

            	            	
            	if("".equals(message))
            	{
            		Toast.makeText(PublishActivity.this, R.string.insert_message, Toast.LENGTH_SHORT).show();
                	return;
            	}
            	KeyList = MQTTSDK.AloohCloud_GetKeyInfos(deviceInfo.getAuid());	
            	String SensorName = MQTTSDK.AloohCloud_GetSensorList(deviceInfo.getTopicId(), KeyList.get(0).getAccessKey()).get(0);
            	
            	if(isDataOnlyChecked == false)
            	{
	            	SimpleDateFormat formatUTC = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");  
	    			formatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));  
	    			String time =  formatUTC.format(new Date());
	    			
	            	MQTTSDK.AloohMessage_SetPublishData(SensorName, message, time);
	            	
					MQTTSDK.AloohMessage_Publish(deviceInfo.getTopicId(), KeyList.get(0).getAccessKey());
            	}
            	else
            	{
            		MQTTSDK.AloohMessage_PublishMessageOnly(deviceInfo.getTopicId(), KeyList.get(0).getAccessKey(), message);
            	}
            }
        });
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}



	
	private IUpdateListener listener = new IUpdateListener()
	{
		@Override
		public void update(int status, String _key) {
			// TODO Auto-generated method stub
			if(status == IUpdateListener.SHOW_DIALOG)
			{
				showDialog(DIALOG_LOGIN);
			}
			else//status == IUpdateListener.REMOVE_DIALOG
			{
				removeDialog(DIALOG_LOGIN);
				if(_key == null)
					Toast.makeText(PublishActivity.this, "네트워크 에러", Toast.LENGTH_SHORT).show();
				else if("401".equals(_key))
					Toast.makeText(PublishActivity.this, R.string.error_different_passwd_eachother, Toast.LENGTH_SHORT).show();
				else if("406".equals(_key))
					Toast.makeText(PublishActivity.this, R.string.noti_relogin_auth_email, Toast.LENGTH_SHORT).show();
				else if("404".equals(_key))
					Toast.makeText(PublishActivity.this, R.string.noti_not_find_email, Toast.LENGTH_SHORT).show();
				else if("500".equals(_key))
					Toast.makeText(PublishActivity.this, "서버에러"+_key, Toast.LENGTH_SHORT).show();
				else
				{
					UserManager.getInstance(PublishActivity.this).setAuthKey(_key);
					deviceListMgr.setDialogUpdateListener(deviceListlistener);
					deviceListMgr.getDeviceList(_key);
					
				}
			}
		}
	};
	
	private IUpdateListener deviceListlistener = new IUpdateListener()
	{
		@Override
		public void update(int status, String _key) {
			// TODO Auto-generated method stub
			if(status == IUpdateListener.SHOW_DIALOG)
			{
				showDialog(DIALOG_DEVICELIST);
			}
			else//status == IUpdateListener.REMOVE_DIALOG
			{
				removeDialog(DIALOG_DEVICELIST);
				if(deviceListMgr.getDeviceList().size() == 0)
					showDialog(DIALOG_NODEVICE);
				else if(deviceListMgr.getDeviceList().size() == 1)
				{
					deviceInfo = deviceListMgr.getDeviceList().get(0);
						
					edtMessage.setText(extra_text);
					tvFeedId.setText(deviceInfo.getTopicId());
					tvProductName.setText(deviceInfo.getProductname());
					tvDeviceName.setText(deviceInfo.getDeviceName());
				}
				else
				{
					deviceListMgr.getDeviceList().clear();
					Intent intent = new Intent(PublishActivity.this, DeviceListActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra(DeviceListManager.SHARED_URL, extra_text);
					startActivity(intent);
					finish();
					
				}
					//else
			}
		}
	};
		@Override
		protected Dialog onCreateDialog(int id) {
			// TODO Auto-generated method stub
			
			switch(id)
			{
				case 0:
				{
					 ProgressDialog dialog = new ProgressDialog(this);
					 dialog.setTitle(R.string.login);
					 dialog.setMessage(getResources().getString(R.string.noti_wait_moment));
					 dialog.setIndeterminate(true);
					 dialog.setCancelable(false);
					 return dialog;
				}
				
				case 1:
					return new AlertDialog.Builder(PublishActivity.this)
							.setTitle("WARNING")
							.setMessage("Please Check Remember Password !")
							.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int whichButton) {
											
											
											finish();
										}
									})
							.create();
					
				case 2:
				{
					 ProgressDialog dialog = new ProgressDialog(this);
					 dialog.setTitle("Searching Device");
					 dialog.setMessage(getResources().getString(R.string.noti_wait_moment));
					 dialog.setIndeterminate(true);
					 dialog.setCancelable(false);
					 return dialog;
				}
				
				case 3:
					return new AlertDialog.Builder(PublishActivity.this)
							.setTitle("WARNING")
							.setMessage("No registered Device !")
							.setPositiveButton(R.string.ok,
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,
												int whichButton) {
											
											
											finish();
										}
									})
							.create();
					
			}
			return super.onCreateDialog(id);
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean _isChecked) {
			// TODO Auto-generated method stub
			isDataOnlyChecked = _isChecked;
		}	
}