package com.example.driverstation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.widget.TextView;

public class PacketSender extends Thread{
	
	private int packetIndex;;
	private byte teamNum1;
	private byte teamNum2;
	private DatagramSocket sock;
	private CRioPacket rioPacket = new CRioPacket();
	private DatagramPacket packet;
	private TextView messageLog;
	private Activity activity;
	private UIManager ui;
	private boolean isRobotNet = true;
	
	public PacketSender(Activity activity, UIManager uiMngr){
		this.activity = activity;
		ui = uiMngr;
		
		//Set up an error console.
		messageLog = (TextView)activity.findViewById(R.id.control_log);
		
		//Find the team number from current ip address.
		WifiManager wifi = (WifiManager)activity.getSystemService(Context.WIFI_SERVICE);
		int address = wifi.getConnectionInfo().getIpAddress();
		teamNum1 = (byte)((address & 0xff00)>>8);
		teamNum2 = (byte)((address & 0xff0000)>>16);
		
		//Set up the datagram packet.
		InetAddress crioAddr;
		try {
			crioAddr = InetAddress.getByAddress(new byte[]
					{10, teamNum1, teamNum2, 2});
			postMessage("Setting robot to " + crioAddr.toString());		//Tell the user the robot's IP
			packet = new DatagramPacket(rioPacket.data, 1024, crioAddr, 1110);
			if((address & 0xff) != 10 && (address & 0xff000000)>>24 != 6)	//Let the application know if it is
			{																//not connected to a robot network.
				isRobotNet = false;
			}
		} catch (UnknownHostException e) {
			postMessage("Packet Error: " + e.getMessage());
			e.printStackTrace();
		}
		
		
		//Make a socket
		try {
			sock = new DatagramSocket();
		} catch (SocketException e) {
			postMessage("Network Error: " + e.getMessage());
		}
		
		//Set up static fields in the packet.
		rioPacket.setAlliance('R');
		rioPacket.setPosition(0x36);
		rioPacket.setTeam(teamNum1, teamNum2);
		
	}
	

	
	//Thread function.
	@Override
	public void run(){
		//Set all of the fields in the packet and send it, then wait for 20 ms.
			try {
				while(isRobotNet){
					for(int i = 0; i < 8; i++)
					{
						rioPacket.setButton(i, ui.buttons[i]);
						rioPacket.setDigitalIn(i, true);
					}
					rioPacket.setIndex(packetIndex);
					rioPacket.setJoystick(ui.joy1X, ui.joy1Y, CRioPacket.JOY1);
					rioPacket.setAuto(ui.auto);
					rioPacket.setEnabled(ui.enabled);
					rioPacket.makeCRC();
					packet.setData(rioPacket.data);
					sock.send(packet);
					rioPacket.clearCRC();  //Took me a while to find this bug
											//have to clear the CRC otherwise the
											//next CRC generated will take the
											//previous one into account.
					packetIndex++;
					Thread.sleep(20);
				}
				postMessage("Device not connected to robot hotspot.");
			} catch (IOException e) {
				postMessage("Network Error: " + e.getMessage());
				e.printStackTrace();
			} catch (InterruptedException e) {
				postMessage("Interruption Error: " + e.getMessage());
				e.printStackTrace();
			}
		
	}
	
	//Function to print a message to the error console.
	private void postMessage(final String s){
		activity.runOnUiThread(new Runnable(){

			@Override
			public void run() {
				messageLog.setText(s + '\n' + messageLog.getText());
			}	
		});
	}
}


