package com.dds.sms.ServerReplica;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.dds.sms.dummy.FIFO;
import com.dds.sms.frontend.Request;
import com.dds.sms.server.ClinicServer;

public class ServerReplica {

	private int RequestID;
	private boolean GroupLeader;
	private DatagramSocket replicaSocket;
	private int myReplicaPort;
	private int replicaPorts[];
	private ClinicServer serverObjects[];
	private FIFO fifoObj;
	Request reqObj;

	public ServerReplica(int myReplicaPort, int replicaPorts[], ClinicServer serverObjects[], FIFO fifoObj){
		this.myReplicaPort = myReplicaPort;
		this.replicaPorts = new int[replicaPorts.length];
		this.serverObjects = new ClinicServer[serverObjects.length];

		for(int i =0; i<replicaPorts.length; i++){
			this.replicaPorts[i] = replicaPorts[i];
		}

		for(int j =0; j<serverObjects.length; j++){
			this.serverObjects[j] = serverObjects[j];
		}

		this.fifoObj = fifoObj;

		try {
			replicaSocket = new DatagramSocket(myReplicaPort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ClinicServer getClinicObject(String location){

		for(int i =0; i<serverObjects.length; i++){
			if(serverObjects[i].getLocation().equals(location))
				return serverObjects[i];
		}
		return null;
	}

	public void listen(){
		DatagramPacket requestPacket = null;
		ServerReplicaHelper serverReplicaHelperObj = null;

		while(true){
			byte[] buf = new byte[256];
			requestPacket = new DatagramPacket(buf, buf.length);

			try {
				replicaSocket.receive(requestPacket);

				serverReplicaHelperObj = new ServerReplicaHelper(this,requestPacket);
				Thread thread  = new Thread(serverReplicaHelperObj); 
				thread.start();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public ClinicServer[] getServerObjects() {
		return serverObjects;
	}



	public void setServerObjects(ClinicServer[] serverObjects) {
		this.serverObjects = serverObjects;
	}



	public DatagramSocket getReplicaSocket() {
		return replicaSocket;
	}



	public void setReplicaSocket(DatagramSocket replicaSocket) {
		this.replicaSocket = replicaSocket;
	}



	public int getMyReplicaPort() {
		return myReplicaPort;
	}



	public void setMyReplicaPort(int myReplicaPort) {
		this.myReplicaPort = myReplicaPort;
	}



	public int[] getReplicaPorts() {
		return replicaPorts;
	}



	public void setReplicaPorts(int[] replicaPorts) {
		this.replicaPorts = replicaPorts;
	}

	public FIFO getFifoObj() {
		return fifoObj;
	}



	public void setFifoObj(FIFO fifoObj) {
		this.fifoObj = fifoObj;
	}



	public Request getReqObj() {
		return reqObj;
	}



	public void setReqObj(Request reqObj) {
		this.reqObj = reqObj;
	}



	public int getRequestID() {
		return RequestID;
	}

	public void setRequestID(int requestID) {
		RequestID = requestID;
	}

	public boolean isGroupLeader() {
		return GroupLeader;
	}

	public void setGroupLeader(boolean groupLeader) {
		GroupLeader = groupLeader;
	}



}
