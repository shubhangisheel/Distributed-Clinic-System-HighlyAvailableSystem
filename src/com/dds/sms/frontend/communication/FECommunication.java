package com.dds.sms.frontend.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;



import com.dds.sms.frontend.Request;
import com.dds.sms.frontend.Response;;

public class FECommunication{

	private Request reqObj;
	private DatagramSocket socket;
	
	public FECommunication(Request reqObj){
		this.reqObj = reqObj;
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public int getGroupLeader(){
		return 0;
	}
	
	
	
	public void send(){
		
		ByteArrayOutputStream bs = null;
		ObjectOutputStream os = null;
		int groupLeaderPort = getGroupLeader();
		
		try {
			bs = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bs);
			os.writeObject(reqObj);
		
			byte[] sendBuffer = bs.toByteArray();
			DatagramPacket requestPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getLocalHost(), groupLeaderPort);
			socket.send(requestPacket);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public Response recieve(){
		
		byte[] recieveBuf = new byte[256];
		ByteArrayInputStream bs = null;
		ObjectInputStream in =  null;
		Response responseObj = null;
		
		DatagramPacket responsePacket = new DatagramPacket(recieveBuf, recieveBuf.length);
		try {
			socket.receive(responsePacket);
			bs = new ByteArrayInputStream(responsePacket.getData());
			in = new ObjectInputStream(bs);
			responseObj = (Response)in.readObject();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return responseObj;
		
	}
}
