package com.mattcao.messenger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE = "com.mattcao.messenger";
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final String redirectedServerPort = deviceDetect();
		editText = (EditText) findViewById(R.id.editText1);
		try{
			
			ServerSocket serverSocket = new ServerSocket(10000);
			Log.e("l1", "start server");
			new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket); 
		}catch (IOException e) {
			
		}
		
		editText.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				boolean handle = false;
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && 
						keyCode == KeyEvent.KEYCODE_ENTER) {
					String msgs = editText.getText().toString() + "\n";
					editText.setText("");
					TextView textView = (TextView) findViewById(R.id.textView1);
					msgs = nameEncode(redirectedServerPort) + ":" + msgs;
					textView.append(msgs);

					Log.e("l1", "start client");
					new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msgs, redirectedServerPort);
					handle = true;
				}
				return handle;
			}
		} );
	}
	
	public String deviceDetect() {
		TelephonyManager tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
		final String redirectedServerPort;
		if(portStr.equals("5554")) {
			redirectedServerPort = "11112";
		}
		else {
			redirectedServerPort = "11108";
		}
		return redirectedServerPort;
	}
	
	public String nameEncode(String port) {
		String name;
		if(port.equals("11112")) {
			name = "Ellen";
		} else {
			name = "Alex";
		}
		return name;
	}
	
	public void sendMessage(View view) {
		final String redirectedServerPort = deviceDetect();
		editText = (EditText) findViewById(R.id.editText1);
		String msgs = editText.getText().toString() + "\n";
		editText.setText("");
		TextView textView = (TextView) findViewById(R.id.textView1);
		msgs = nameEncode(redirectedServerPort) + ":" + msgs;
		textView.append(msgs);
		

		Log.e("l1", "start client");
		new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msgs, redirectedServerPort);
	}
	
	private class ServerTask extends AsyncTask<ServerSocket, String, Void> {
		@Override
		protected Void doInBackground(ServerSocket... sockets){
			String msgs = null;
			ServerSocket serverSocket = sockets[0];
			Socket socket;
			try {
				while(true) {
					socket = serverSocket.accept();

					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					msgs = in.readLine();
					publishProgress(msgs);
					socket.close();
				}
			} catch (IOException e) {
				
			}
			return null;
		}
		
		protected void onProgressUpdate(String... string){
			TextView textView = (TextView) findViewById(R.id.textView1);
			textView.append(string[0] + "\n");
			return;
		}		
	}
	
	private class ClientTask extends AsyncTask<String, Void, Void>{
		private String serverIpAddress = "10.0.2.2";
		
		protected Void doInBackground(String... string){
			try {
				Socket socket = new Socket(InetAddress.getByName(serverIpAddress), Integer.parseInt(string[1]));
				Log.e("l1", "created a socket");
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);;
			    out.println(string[0]);
			    Log.e("l1", "Client sent message");
			    Log.e("l1", string[0]);
			    out.flush();
			    
			}catch (Exception e) {
				e.printStackTrace();
			} return null;
		}

	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
