package com.dds.sms.frontend.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;



import com.dds.sms.ServerReplica.ServerReplicaHelper;
import com.dds.sms.frontend.Request;
import com.dds.sms.frontend.Response;;

public class FECommunication {

	private Request reqObj;
	private DatagramSocket socket;
	
	/*Storing the last fifty reqObj in a list to maintain high availability*/
	public static Queue bufferList;

	public FECommunication(Request reqObj){
		this.reqObj = reqObj;
		bufferList = new LinkedList();
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		System.out.println("Debug: In FEComunnication ctor printing rcvd data: requestID : "+reqObj.getRequestID() +reqObj.getFirstName());
	}
	
	//TODO
	public int getGroupLeader(){
		//return 0;
		return 5200;// Debug: 1 server
	}

	public void send(){

		System.out.println("Debug: In send() buffer outside if condition, buffer size before addition: "+bufferList.size());
		/*until list is full*/
		if(bufferList.size()<=50){
			bufferList.add(reqObj);
			System.out.println("Debug: In send() buffer if condition, buffer size after addition: "+bufferList.size());
		}
		/*if buffer becomes full, the entire buffer is cleared and filled with recent 50 request objects*/
		else{
			bufferList.remove();
			bufferList.add(reqObj);
		}

		ByteArrayOutputStream bs = null;
		ObjectOutput os = null;
		int groupLeaderPort = getGroupLeader();
		System.out.println("Debug: In send() in groupLEadeRPort: "+groupLeaderPort);

		try {
			bs = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bs);
			os.writeObject(reqObj);

			byte[] sendBuffer = bs.toByteArray();
			
			System.out.println("Debug: In send() of Front end priniting requestPakcet"+sendBuffer);
			
			DatagramPacket requestPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getLocalHost(), groupLeaderPort);
			socket.send(requestPacket);
			System.out.println("Debug: In send, after socket.send");
			
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
