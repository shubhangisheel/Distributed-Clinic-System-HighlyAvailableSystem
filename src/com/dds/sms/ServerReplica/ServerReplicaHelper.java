package com.dds.sms.ServerReplica;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;

import com.dds.sms.dummy.FIFO;
import com.dds.sms.frontend.Request;
import com.dds.sms.frontend.Response;
import com.dds.sms.server.ClinicServer;

public class ServerReplicaHelper implements Runnable{
	
	private DatagramPacket requestPacket;
	private ServerReplica serverReplicaObj;
	private Request reqObj;
	private String clinicLocation;
	
	public ServerReplicaHelper(ServerReplica serverReplicaObj, DatagramPacket requestPacket){
		this.requestPacket = requestPacket;
		this.serverReplicaObj = serverReplicaObj;
		
		ByteArrayInputStream bs = null;
		ObjectInputStream is = null;
		ServerReplicaHelper serverReplicaHelperObj = null;
		
		
		bs = new ByteArrayInputStream(requestPacket.getData());
		try {
			is = new ObjectInputStream(bs);
			reqObj = (Request)is.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void run(){
		
		if(serverReplicaObj.isGroupLeader()){
			groupLeaderTask();
		}
		else{
			nonGroupLeaderTask();
		}
	}
	
	
	public void groupLeaderTask(){
		Response responseObj[];
		sendToOtherReplicas();
		responseObj = receiveFromOtherReplicas();
	}
	
	public void sendToOtherReplicas(){
		/*Creating FIFO object to broadcast it to other sevrer replicas*/
		FIFO fifoObj = serverReplicaObj.getFifoObj();
		fifoObj.broadcast(reqObj);
		
		
		
	}
	
	public Response[] receiveFromOtherReplicas(){
		return null;
	}
	
	public void nonGroupLeaderTask(){
		
		Response responseObj=null;
		
		/*Sending packet to its own location server*/
		clinicLocation = reqObj.getClinicLocation();
		
		/*Loop to check location of server*/
		if(clinicLocation.equals("MTL")){
			responseObj = resolveRequest(reqObj, serverReplicaObj.getClinicObject("MTL"));
		}
		else if(clinicLocation.equals("LVL")){
			responseObj = resolveRequest(reqObj, serverReplicaObj.getClinicObject("LVL"));
		}
		else if(clinicLocation.equals("DDO")){
			responseObj = resolveRequest(reqObj, serverReplicaObj.getClinicObject("DDO"));
		}
	}
	
	public Response resolveRequest(Request reqObj, ClinicServer serverObj){
		
		
		return null;
	}
}
