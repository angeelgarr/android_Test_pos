package com.pax.mposapi.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.pax.mposapi.ConfigManager;
import com.pax.mposapi.util.MyLog;
import com.pax.mposapi.util.Utils;
import com.pax.mposapi.util.Utils.RingBuffer;


class BtAutoBond {
	public static boolean createBond(Class<?> btClass, BluetoothDevice btDevice) throws Exception
	{
		Method createBondMethod = btClass.getMethod("createBond");
		Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}
	
	public static boolean removeBond(Class<?> btClass, BluetoothDevice btDevice) throws Exception
	{
		Method removeBondMethod = btClass.getMethod("removeBond");
		Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
		return returnValue.booleanValue();
	}

	public static boolean setPin(Class<?> btClass, BluetoothDevice btDevice, String str) throws Exception
	{
		try
		{
			Method setPinMethod = btClass.getDeclaredMethod("setPin",
					new Class[]
					{byte[].class});
			Boolean returnValue = (Boolean) setPinMethod.invoke(btDevice,
					new Object[]
					{str.getBytes()});
			MyLog.d("setPinMethod", "setPinMethod returns: " + returnValue);
		}
		catch (SecurityException e)
		{
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}
	
	public static boolean cancelPairingUserInput(Class<?> btClass, BluetoothDevice device) throws Exception
	{
		Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		return returnValue.booleanValue();
	}

	public static boolean cancelBondProcess(Class<?> btClass, BluetoothDevice device)	throws Exception
	{
		Method createBondMethod = btClass.getMethod("cancelBondProcess");
		Boolean returnValue = (Boolean) createBondMethod.invoke(device);
		return returnValue.booleanValue();
	}	
}

//used for broadcast receiver notifying the bt paring thread
class BtBondStateSynchronizer {
	private boolean btBondResultKnown = false;

	public synchronized void setResultUnKnown() {
		btBondResultKnown = false;
	}
	
	public synchronized void setResultKnown() {
		btBondResultKnown = true;
		notifyAll();
	}	
	public synchronized void waitForResult(int timeout, BluetoothDevice btDev) throws InterruptedException {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + timeout;
		while (!btBondResultKnown) {
			wait(10);
			if(btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
				btBondResultKnown = true;
				break;
			}
			if(endTime <= System.currentTimeMillis()) {
				btBondResultKnown = true;
				break;
			}
		}
	}
}

@SuppressLint("NewApi")
public class Comm {

	//public static SocketChannel clientTemp=null;
	private static final String TAG = "COMM";
	
	private Context context;
	private static Comm comm;
	
	private SocketChannel client = null;
	private OutputStream ipOutputStream = null;
	private InputStream ipInputStream = null;
	private OutputStream btOutputStream = null;
	private InputStream btInputStream = null;

	boolean isIpConected = false;
	private static boolean isBTConnected = false;
	//public boolean isBTConnected = false;
	
	private String ipLastConnectedAddr = null;
	private int ipLastConnectedPort = 0;
	private String btLastConnectedMac  = null;
	
	public static final int CONN_TIMEOUT_DEFAULT = 37000; //ms
	public static final int READ_TIMEOUT_DEFAULT = 60000;	//ms   //jason
	//private final int READ_TIMEOUT_SHORT = 100;	//ms
	
	private ConfigManager cfg;
	private BluetoothDevice btDev = null;
	private UUID BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");	// BT chat "fa87c0d0-afac-11de-8a39-0800200c9a66"); 
	private BluetoothSocket btsock = null;
	private BluetoothAdapter btAdapter = null;
	private String BT_PAIR_PIN = "0000";
	private BtBondStateSynchronizer btBondStateSynchronizer = new BtBondStateSynchronizer();
	
	private Comm(Context context){
		this.context = context;
		cfg = ConfigManager.getInstance(context);
	}
	
	public static Comm getInstance(Context context){
		if (comm == null){
			comm = new Comm(context);
		} 
		
		return comm;
	}

	private boolean BtPair(String mac, String pin) throws Exception
	{
		MyLog.d(TAG, "try to bond to " + mac + " with pin " + pin);
		/*
		 * no use in this stage!
		if (!BtAutoBond.setPin(btDev.getClass(), btDev, pin)) {
			MyLog.d(TAG, "setPin failed");
			return false;
		}
		*/
		if (!BtAutoBond.createBond(btDev.getClass(), btDev)) {
			MyLog.d(TAG, "createBond failed");
			return false;
		}
		/*
		if (!BtAutoBond.cancelPairingUserInput(btDev.getClass(), btDev)) {
			MyLog.d(TAG, "cancelPairingUserInput failed");
			return false;
		}*/		
		return true;
	}	

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
        public void onReceive(Context context, Intent intent) {   
            String action = intent.getAction();   

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);   
            if(BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)){   
                switch (device.getBondState()) {   
                case BluetoothDevice.BOND_BONDING:   
                    MyLog.d(TAG, "state bonding");   
                    break;   
                case BluetoothDevice.BOND_BONDED:   
                    MyLog.d(TAG, "state bonded");   
                    btBondStateSynchronizer.setResultKnown();
                    break;   
                case BluetoothDevice.BOND_NONE:   
                    MyLog.d(TAG, "state bond_none");   
                    btBondStateSynchronizer.setResultKnown();
                default:   
                    break;   
                }   
            } else if (action.equals("android.bluetooth.device.action.PAIRING_REQUEST")) 
            {                 
            	MyLog.i(TAG, "received paring request");
                try 
                { 
                	/*
                	 * NOTE: in SSP mode, setting pin here will cause paring failure.
                	 *  
                    BtAutoBond.setPin(device.getClass(), device, BT_PAIR_PIN); // �ֻ��6�2ɼ������ 
                    BtAutoBond.createBond(device.getClass(), device); 
                	 */
                    //BtAutoBond.cancelPairingUserInput(device.getClass(), device); 
                } 
                catch (Exception e) 
                { 
                    // TODO Auto-generated catch block 
                    e.printStackTrace(); 
                } 
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            	MyLog.i(TAG, "bluetootch disconnected!");
            	comm.close();
            }
        } 
    	
    };
	
	public void connect() throws IOException{
		String commType = cfg.commType;
		
		if (commType.equals("ip")) {
			String addr = cfg.serverAddr;
			int port = cfg.serverPort;
			
			if (isIpConected) {
				//if host changed, close it firstly and re-connect
				if (!addr.equals(ipLastConnectedAddr) || ipLastConnectedPort != port) {
					MyLog.i(TAG, "close previous link with: " + ipLastConnectedAddr + ":" + ipLastConnectedPort);
					close();
				} else {
					/*
					boolean disconnectedUnexpected = false;
					try {
						reset();	// for ip, it's unblocking read test, if it fails, then close it and reconnect...
					} catch(Exception e) {
						disconnectedUnexpected = true;
					}
					
					if (disconnectedUnexpected) {
						MyLog.i(TAG, "unexpected disconnected, try to reconnect...");
						close();
					} else {
						return;
					}
					*/
					return;
				}
			}

			MyLog.i(TAG, "connecting " + addr + ": " + port);
			/*
			 * BLOCKING conect, cannot interrupt
			 * 
			client = new Socket();
			client.setSoTimeout(cfg.receiveTimeout);
			client.connect(new InetSocketAddress(addr, port), CONN_TIMEOUT_DEFAULT);
			ipOutputStream = client.getOutputStream();
			ipInputStream = client.getInputStream();
			*/
			
			/*
			 * NON-BLOCKING, busy polling
			 * 
			SocketAddress svrAddr = new InetSocketAddress(addr, port);
			client = SocketChannel.open();
			client.configureBlocking(false);
			client.connect(svrAddr);
			while (!client.finishConnect()) {
				Thread.yield();
			}
			*/

			/*
			 * NON-BLOCKING, using selector
			 */
			SocketAddress svrAddr = new InetSocketAddress(addr, port);
			client = SocketChannel.open();
			client.configureBlocking(false);

			Selector selector = Selector.open();
			SelectionKey key = client.register(selector, SelectionKey.OP_CONNECT);

			client.connect(svrAddr);
			//clientTemp=client;
			int readyChannels = selector.select(CONN_TIMEOUT_DEFAULT);
			if (readyChannels == 0) {
				MyLog.w(TAG, "no channel ready!");
				throw new IOException("Connetion failed");
			}
			
			if (key.isConnectable()) {
				if (client.finishConnect()) {
					//For socketchannel, this doesn't mean it's connected (if you unplug the cable or swith off wifi, it also returns true
					//we can check further later with reset() (try to read..) to check if it's really connected.
					//MyLog.i(TAG, "connected!");
				} else {
					MyLog.w(TAG, "not econnected!");
					throw new IOException("Connection failed");
				}
			} else {
				MyLog.e(TAG, "not connectable!");
				throw new IOException("Connetion failed");
			}
			
			//cancel this key, so we can set back to blocking mode
			key.cancel();
			
			//NOTE: this is used to check if it's really connected or NOT (by reading it)!
			reset();
			
			client.configureBlocking(true);
			
			ipOutputStream = client.socket().getOutputStream();
			ipInputStream = client.socket().getInputStream();
			
			isIpConected = true;
			
			ipLastConnectedAddr = addr;
			ipLastConnectedPort = port;
			
			MyLog.i(TAG, "IP connected!");
		} else if (commType.equals("bluetooth")) {
			
			IntentFilter intent = new IntentFilter();
			
			String btMac = cfg.bluetoothMac;

			if (isBTConnected) {
				//if bt mac changed, close it firstly and re-connect
				if (!btMac.equals(btLastConnectedMac)) {
					MyLog.i(TAG, "close previous link with: " + btLastConnectedMac);
					close();
				} else {
					return;
				}
			}
			
			MyLog.i(TAG, "connecting bt mac :" + cfg.bluetoothMac);
			
			btAdapter = BluetoothAdapter.getDefaultAdapter();
			if (btAdapter == null) {
				throw new IOException("No bluetooth available!");
			}
			if (!btAdapter.isEnabled()) {
				throw new IOException("bluttooth is not enabled!");
			}
			
			btDev = btAdapter.getRemoteDevice(btMac);
			
			btAdapter.cancelDiscovery();
			boolean bondResult = true;

			//try to auto bond
			if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
				/*
				 * an ugly busy waiting version 
				long end = System.currentTimeMillis() + conn_timeout;				
				while (end - System.currentTimeMillis() > 0) {
					MyLog.i(TAG, "waiting till bonded...");
					if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
						MyLog.i(TAG, "bonded!");
						SystemClock.sleep(5000);
						break;
					}
					SystemClock.sleep(1000);
				}
				*/				
				
				intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);   
				intent.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
				context.registerReceiver(mReceiver, intent);

				try {
					btBondStateSynchronizer.setResultUnKnown();					
					bondResult = BtPair(btMac, BT_PAIR_PIN);
				} catch (Exception e) {
					e.printStackTrace();
					throw new IOException("bluetooth bond exception");
				} finally {
					if (!bondResult) {
						context.unregisterReceiver(mReceiver);
						throw new IOException("bluetooth bond failed");
					}
				}

				try {
					MyLog.i(TAG, "waiting for bond result....");
					btBondStateSynchronizer.waitForResult(CONN_TIMEOUT_DEFAULT, btDev);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				MyLog.i(TAG, "waked up...");

				context.unregisterReceiver(mReceiver);
				// if not bonded, throws exception.
				if (btDev.getBondState() != BluetoothDevice.BOND_BONDED) {
					MyLog.w(TAG, "bt not bonded");
					throw new IOException("bluetooth not bonded");
				}
				//bonded!
				//experiments show that we should sleep for a while...
				SystemClock.sleep(5000);
			}

			MyLog.i(TAG, "btsock.connect...");
			btsock = btDev.createInsecureRfcommSocketToServiceRecord(BT_UUID);
			
			if(btsock == null)
			{
				
				throw new IOException("Create socket failed!");
			}
			btsock.connect();

			btInputStream = btsock.getInputStream();
			btOutputStream = btsock.getOutputStream();
			isBTConnected = true;
			
			btLastConnectedMac = btMac;
			MyLog.i(TAG, "BT connected.");
			
			intent.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);   
			context.registerReceiver(mReceiver, intent);			
		}
	}
	
	public void send(byte[] buf) throws IOException{
		if (cfg.commType.equals("ip")) {
			ipOutputStream.write(buf);
		} else if (cfg.commType.equals("bluetooth")) {
			if(isBTConnected)
			{
				btOutputStream.write(buf);
			}
			else 
			{
				throw new IOException("BT Not Connected");
			}
		}
	}
	
	private IOException btIoException = null;
	private BTReadThread btReadThread = null;
	
	private RingBuffer btRingBuffer;
	
	class BTReadThread extends Thread {
		private byte[] tmpBuffer;

		public BTReadThread() {
			tmpBuffer = new byte[10240];
			btRingBuffer = new RingBuffer(10240);
		}
		
		public void run() {
			Looper.prepare();
			try {
				while (true) {
					int len = btInputStream.read(tmpBuffer);
					MyLog.e(TAG, "len: " + String.valueOf(len) +"　debug Data: " + Utils.byte2HexStr(tmpBuffer, 0, len));
					if (len < 0) {
						throw new IOException("input stream read error: " + len);
					} else {
						btRingBuffer.write(tmpBuffer, len);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				btIoException = e;
			}
		}
	}
	
	public int recv(byte[] buf, int offset, int exp) throws IOException{
		//if exp nothing, directly return
		if (exp == 0) {
			return 0;
		}
		
		int len = 0;
		if (cfg.commType.equals("ip")) {
			int totalLen = 0;
			int cLen;
			if (client != null) {
				client.socket().setSoTimeout(cfg.receiveTimeout);
			}
			
			while (totalLen < exp) {
				cLen = ipInputStream.read(buf, offset + totalLen, exp - totalLen);
				
				//NOTE: if connection is closed, read will return -1 and NO exception is thrown.
				if (cLen < 0)
				{
					if (totalLen > 0) {
						break;
					} else {
						throw new IOException("Conntection reset");
					}
				} else if (cLen == 0) {	//receive timeout
					break;
				}
				
				totalLen += cLen;
			}
			
			len = totalLen;
			
		} else if (cfg.commType.equals("bluetooth")) {
			if (btIoException != null)
			{
				btReadThread = null;
				btIoException = null;
			}			

			if (btReadThread == null) {
				btReadThread = new BTReadThread();
				btReadThread.start();				
			}
			
			int totalLen = 0;
			int cLen;
//			long countDown = cfg.receiveTimeout;
			long countDown = 5000;    // Ocean 2015-7-30  17000改成5000
			long end = System.currentTimeMillis() + countDown;
			//MyLog.e(TAG, "exp " + String.valueOf(exp) + "bytes: " + Utils.byte2HexStr(buf, 0, exp));
			while (totalLen < exp && (System.currentTimeMillis() < end)) {
				cLen = btRingBuffer.read(buf, offset + totalLen, exp - totalLen);
				//MyLog.e(TAG, "<<<< ipInputStream clen " + String.valueOf(cLen) + " buffer: " + btRingBuffer.toString());
				totalLen += cLen;
				Thread.yield();
				if (btIoException != null)
				{
					throw btIoException;
				}
				//Add by Jim 2015.02.04:For abort command
//				if(Proto.tries == 0)   
//				{
//					break;
//				}
			}
			
			if (totalLen == 0) {
				//throw new IOException("Recv timeout");
				Log.w(TAG, "recv nothing");
			}
			
			len = totalLen;
//			MyLog.i(TAG, "<<<< buf Data totalLen : " + totalLen);
//			MyLog.i(TAG, "<<<< buf Data: " + Utils.byte2HexStr(buf, 0, totalLen));
		}
		
		
		return len;
	}
	
	public int recv1(byte[] buf, int offset, int exp) throws IOException{
		//if exp nothing, directly return
		if (exp == 0) {
			return 0;
		}
		
		int len = 0;
		if (cfg.commType.equals("ip")) {
			int totalLen = 0;
			int cLen;
			if (client != null) {
				client.socket().setSoTimeout(cfg.receiveTimeout);
			}
			
			while (totalLen < exp) {
				cLen = ipInputStream.read(buf, offset + totalLen, exp - totalLen);
				
				//NOTE: if connection is closed, read will return -1 and NO exception is thrown.
				if (cLen < 0)
				{
					if (totalLen > 0) {
						break;
					} else {
						throw new IOException("Conntection reset");
					}
				} else if (cLen == 0) {	//receive timeout
					break;
				}
				
				totalLen += cLen;
			}
			
			len = totalLen;
			
		} else if (cfg.commType.equals("bluetooth")) {
			if (btIoException != null)
			{
				btReadThread = null;
				btIoException = null;
			}			

			if (btReadThread == null) {
				btReadThread = new BTReadThread();
				btReadThread.start();				
			}
			
			int totalLen = 0;
			int cLen;
//			long countDown = cfg.receiveTimeout;
			long countDown = 20000;
			long end = System.currentTimeMillis() + countDown;
			//MyLog.e(TAG, "exp " + String.valueOf(exp) + "bytes: " + Utils.byte2HexStr(buf, 0, exp));
			while (totalLen < exp && (System.currentTimeMillis() < end)) {
				cLen = btRingBuffer.read(buf, offset + totalLen, exp - totalLen);
				//MyLog.e(TAG, "<<<< ipInputStream clen " + String.valueOf(cLen) + " buffer: " + btRingBuffer.toString());
				totalLen += cLen;
				Thread.yield();
				if (btIoException != null)
				{
					throw btIoException;
				}
				//Add by Jim 2015.02.04:For abort command
//				if(Proto.tries == 0)
//				{
//					break;
//				}
			}
			
			if (totalLen == 0) {
				//throw new IOException("Recv timeout");
				Log.w(TAG, "recv nothing");
			}
			
			len = totalLen;
//			MyLog.i(TAG, "<<<< buf Data totalLen : " + totalLen);
//			MyLog.i(TAG, "<<<< buf Data: " + Utils.byte2HexStr(buf, 0, totalLen));
		}
		
		
		return len;
	}

	private ByteBuffer garbageBuffer = ByteBuffer.allocate(1024);
	public void reset() throws IOException{
		/*
		 * don't, inputstream.available() NO guarantee
		 * 
		if (ipInputStream.available() > 0) 
		{
			MyLog.i(TAG, "garbage len " + ipInputStream.read(new byte[1024]));
		}
		*/
		
		if (cfg.commType.equals("ip")) {
			//set it to non-blocking and read till no data available.
			client.configureBlocking(false);
			
			while (client.read(garbageBuffer) > 0) {
				garbageBuffer.clear();
			}

			client.configureBlocking(true);

		} else if (cfg.commType.equals("bluetooth")) {
			//reset bt ring buffer
			if (btRingBuffer != null) {
				MyLog.e(TAG, "<<<< btRingBuffer reset");
				btRingBuffer.reset();
			}
		}
	}
	
	public void close(){
		try{
			MyLog.i(TAG, "closing...");
			if (client != null) {
				client.socket().shutdownInput();
				client.socket().shutdownOutput();
				client.close();
				client = null;
				MyLog.i(TAG, "ip client closed");
			} 
			
			if (btsock != null) {
				Thread.sleep(500);
				btsock.close();
				btsock = null;
				MyLog.i(TAG, "bt closed");
			}
			
		}catch(Exception exception){
			exception.printStackTrace();
		}finally{
			ipInputStream = null;
			ipOutputStream = null;
			isIpConected = false;
			ipLastConnectedAddr = null;
			ipLastConnectedPort = 0;

			btInputStream = null;
			btOutputStream = null;
			isBTConnected = false;	
			btLastConnectedMac = null;
			MyLog.i(TAG, "close finally");
		}
		
	}
}
