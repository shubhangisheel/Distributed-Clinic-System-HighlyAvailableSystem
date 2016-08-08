package com.dds.sms.frontend.communication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import com.dds.sms.frontend.FrontEnd;

public class CrashResponse implements Runnable{
	private DatagramSocket crashSocket;
	private FrontEnd frontEndObj;

	public CrashResponse(FrontEnd frondEndObj) {
		this.frontEndObj = frondEndObj;
		try {
			crashSocket = new DatagramSocket(10000);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		listenForLeaderCrash();
	}
	
	/*Port for listening for Group Leader crash, received crahsInfo from isAlive Only when group leader crashes
	 * calls sendBufferToNewGL to send the stored buffer to new group leader*/
	public void listenForLeaderCrash(){
		DatagramPacket newGroupLeader = null;

		while(true){
			byte[] buf = new byte[256];
			newGroupLeader = new DatagramPacket(buf, buf.length);

			try {
				crashSocket.receive(newGroupLeader);
				sendBufferToNewGL(newGroupLeader);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*serialize buffer and send it to new group leader*/
	public void sendBufferToNewGL(DatagramPacket newGroupLeader){
		DatagramSocket socket = null;
		ByteArrayOutputStream bs = null;
		ObjectOutputStream os = null;

		String newLeaderPort = new String(newGroupLeader.getData(), 0, newGroupLeader.getLength());
		int leaderPort = Integer.parseInt(newLeaderPort); 
		frontEndObj.setGroupLeader(leaderPort);

		try {
			socket = new DatagramSocket();
			bs = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bs);
			os.writeObject(FECommunication.bufferList);

			byte[] sendBuffer = bs.toByteArray();
			DatagramPacket requestPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getLocalHost(), leaderPort);
			socket.send(requestPacket);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	 
}
