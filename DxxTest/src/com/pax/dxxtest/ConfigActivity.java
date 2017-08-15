package com.pax.dxxtest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pax.dxxtest.R;
import com.pax.mposapi.ConfigManager;

public class ConfigActivity extends Activity {

	private final String TAG = "ConfigActivity";
	private ConfigManager cfgMgr;
	
	private final int REQUEST_BT_ENABLE = 1;
	private final int REQUEST_BT_DISCOVER = 2;
	
	RadioGroup commTypeRadioGroup;
	TextView serverIpTextView;
	TextView serverPortTextView;
	Spinner btMacSpinner;
	Button btScanButton;
	Button resetBtn;
	Button saveBtn;
	
	TextView commTypeLabel;
	TextView serverIpLabel;
	TextView serverPortLabel;
	TextView btMacLabel;
	
	//temp configuration.
	private String tempBtMac;
	
	private BluetoothAdapter btAdapter;
	private ArrayList<BluetoothDevice> btScannedDevs;
    TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			updateModifiedStatus();
		}	    	
    };

	
	private boolean isCommTypeChanged() {
		int checkedId = commTypeRadioGroup.getCheckedRadioButtonId(); 
		if (checkedId == R.id.radioWifi) {
			return !cfgMgr.commType.equals("ip");
		} else if (checkedId == R.id.radioBluetooth) {
			return !cfgMgr.commType.equals("bluetooth");
		} else {
			return false;
		}
	}
	
	private boolean isServerIpChanged() {
		return !serverIpTextView.getText().toString().equals(cfgMgr.serverAddr);
	}
	
	private boolean isServerPortChanged() {
		String s = serverPortTextView.getText().toString();
		if (s.length() == 0) {
			return true;
		}
		
		int i;
		try {
			i = Integer.parseInt(s);
			return (i != cfgMgr.serverPort);
		} catch (NumberFormatException e) {
			return true;
		}
	}
	
	private boolean isBtMacChanged() {
		return !tempBtMac.equals(cfgMgr.bluetoothMac);
	}

	private void updateModifiedStatus() {

		if (isCommTypeChanged()) {
			commTypeLabel.setTextColor(Color.RED);
		} else {
			commTypeLabel.setTextColor(Color.BLACK);
		}

		if (isServerIpChanged()) {
			serverIpLabel.setTextColor(Color.RED);
		} else {
			serverIpLabel.setTextColor(Color.BLACK);
		}

		if (isServerPortChanged()) {
			serverPortLabel.setTextColor(Color.RED);
		} else {
			serverPortLabel.setTextColor(Color.BLACK);
		}

		if (isBtMacChanged()) {
			btMacLabel.setTextColor(Color.RED);
		} else {
			btMacLabel.setTextColor(Color.BLACK);
		}		
	}
	
	private void initView() {

	    if (cfgMgr.commType.equals("bluetooth")) {
		    commTypeRadioGroup.check(R.id.radioBluetooth);
	    } else {
		    commTypeRadioGroup.check(R.id.radioWifi);
	    }		
	    
	    serverIpTextView.removeTextChangedListener(textWatcher);
	    serverPortTextView.removeTextChangedListener(textWatcher);
	    
	    serverIpTextView.setText(cfgMgr.serverAddr);
	    serverPortTextView.setText(String.format("%d", cfgMgr.serverPort));
	    
	    serverIpTextView.addTextChangedListener(textWatcher);
	    serverPortTextView.addTextChangedListener(textWatcher);

	    tempBtMac = cfgMgr.bluetoothMac;
	    
	    updateModifiedStatus();	
	    updateBtMacView();
	}
	
	private void updateBtMacView() {
		
	    List<HashMap<String, String>> macItems = new ArrayList<HashMap<String, String>>();
	    Set<BluetoothDevice> btBondedDevs = btAdapter.getBondedDevices();
	    int position = 0;
	    int hit = -1;
	    HashMap<String, String> macItem;
	    for (BluetoothDevice bondedDev : btBondedDevs) {
		    macItem = new HashMap<String, String>();
		    macItem.put("macText", bondedDev.getName() + "\n" + bondedDev.getAddress());
		    macItems.add(macItem);
	    	if (bondedDev.getAddress().equals(tempBtMac)) {
	    		Log.i(TAG, "hit " + position);
	    		hit = position;
	    	}
		    position++;
	    }
	    
	    for (BluetoothDevice scannedDev : btScannedDevs) {
	    	if (scannedDev.getBondState() != BluetoothDevice.BOND_BONDED) {
			    macItem = new HashMap<String, String>();
			    macItem.put("macText", scannedDev.getName() + "\n" + scannedDev.getAddress());
			    macItems.add(macItem);
		    	if (scannedDev.getAddress().equals(tempBtMac)) {
		    		Log.i(TAG, "hit " + position);
		    		hit = position;
		    	}
			    position++;
	    	}
	    }
	    
	    if (hit < 0) {
		    macItem = new HashMap<String, String>();
		    BluetoothDevice dev = btAdapter.getRemoteDevice(tempBtMac);
		    macItem.put("macText", dev.getName() + "\n" + dev.getAddress());
		    macItems.add(macItem);
		    hit = position;
	    }
	    
	    btMacSpinner.setAdapter(new SimpleAdapter(ConfigActivity.this, 
	    		macItems,
	    		R.layout.btmac_item, 
	    		new String[] {"macText"}, 
	    		new int[] {R.id.btMacText }));
	    
	    btMacSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView btMacTv = (TextView)view.findViewById(R.id.btMacText);
				String[] btInfo = btMacTv.getText().toString().split("\n");
				tempBtMac = btInfo[1];
				updateModifiedStatus();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
	    	
		});

	    btMacSpinner.setSelection(hit);

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    // TODO Auto-generated method stub
	    setContentView(R.layout.activity_config);

	    commTypeLabel = (TextView)findViewById(R.id.commTypeLabel);
	    serverIpLabel = (TextView)findViewById(R.id.serverIpLabel);
	    serverPortLabel = (TextView)findViewById(R.id.serverPortLabel);
	    btMacLabel = (TextView)findViewById(R.id.btMacLabel);
	    
	    btScannedDevs = new ArrayList<BluetoothDevice>();
	    btAdapter = BluetoothAdapter.getDefaultAdapter();
	    cfgMgr = ConfigManager.getInstance(ConfigActivity.this);
	    tempBtMac = cfgMgr.bluetoothMac;
	    
	    commTypeRadioGroup = (RadioGroup)findViewById(R.id.commTypeRadioGroup);	    
	    commTypeRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				updateModifiedStatus();
			}
		});
	    
	    serverIpTextView = (TextView)findViewById(R.id.serverIpText);
	    serverPortTextView = (TextView)findViewById(R.id.serverPortText);	    
	    btMacSpinner = (Spinner)findViewById(R.id.btMacSpinner);
	    
	    btScanButton = (Button)findViewById(R.id.btScanBtn);
	    btScanButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent;
				if (!btAdapter.isEnabled()) {
					intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(intent, REQUEST_BT_ENABLE);					
				}
				else {
					intent = new Intent(ConfigActivity.this, DeviceListActivity.class);
					startActivityForResult(intent, REQUEST_BT_DISCOVER);
				}
			}
		});
	    
	    resetBtn = (Button)findViewById(R.id.resetBtn);
	    resetBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cfgMgr.load();
				initView();
			}
		});
	    
	    saveBtn = (Button)findViewById(R.id.saveBtn);
	    saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cfgMgr.serverAddr = serverIpTextView.getText().toString();
				try {
					//if not a valid port, don't change.
					int tempPort = Integer.parseInt(serverPortTextView.getText().toString());
					if (tempPort > 65535 || tempPort <= 0) {
					} else {
						cfgMgr.serverPort = tempPort;
					}
				} catch (NumberFormatException e) {
					//not changed
				}
				cfgMgr.commType = (commTypeRadioGroup.getCheckedRadioButtonId() == R.id.radioBluetooth) ? "bluetooth" : "ip";
				cfgMgr.bluetoothMac = tempBtMac;
				
				cfgMgr.save();
				initView();
				Toast.makeText(ConfigActivity.this, "Config Saved", Toast.LENGTH_SHORT).show();				
			}
		});

	    initView();	    
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_BT_ENABLE:
			if (resultCode == Activity.RESULT_OK) {
				Intent intent = new Intent(ConfigActivity.this, DeviceListActivity.class);
				startActivityForResult(intent, REQUEST_BT_DISCOVER);
			}
			break;
		case REQUEST_BT_DISCOVER:	//bt scan
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
            	btScannedDevs.clear();
                // Get the device MAC address
                String address = data.getExtras()
                                     .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                tempBtMac = address;
                
                ArrayList<String> devices = data.getExtras()
                					.getStringArrayList(DeviceListActivity.ALL_DEVICE_ADDRESS);
                for (String s : devices) {
                	String[] info = s.split("\n");
                	btScannedDevs.add(btAdapter.getRemoteDevice(info[1]));
                }
                
                updateBtMacView();
            }
			break;
		}
    }
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		  if (keyCode == KeyEvent.KEYCODE_BACK) {
			  finish();
		  }
		  return false;
	}
	
}
