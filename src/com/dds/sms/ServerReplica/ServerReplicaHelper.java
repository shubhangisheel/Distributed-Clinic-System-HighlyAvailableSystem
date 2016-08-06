package com.dds.sms.ServerReplica;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

import com.dds.sms.dummy.FIFO;
import com.dds.sms.frontend.Request;
import com.dds.sms.frontend.Response;
import com.dds.sms.server.ClinicServer;

public class ServerReplicaHelper implements Runnable{

	private DatagramPacket requestPacket;
	private ServerReplica serverReplicaObj;
	private Request reqObj;
	private String clinicLocation;

	/*ENUM*/
	private enum switchFunc{
		createDRecord, createNRecord, editRecord,getCount, transferRecord  
	}

	public ServerReplicaHelper(ServerReplica serverReplicaObj, DatagramPacket requestPacket){
		this.requestPacket = requestPacket;
		this.serverReplicaObj = serverReplicaObj;

		ByteArrayInputStream bs = null;
		ObjectInput is = null;
		
		byte[] b = requestPacket.getData();
		bs = new ByteArrayInputStream(b);
		try {
			is = new ObjectInputStream(bs);
			System.out.println("In SRH ctor requestpacket: "+ requestPacket);
			reqObj = (Request)is.readObject();
			System.out.println("Debug: fieldname "+reqObj.getFieldName());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*run method override*/
	public void run(){

		if(serverReplicaObj.isGroupLeader()){
			System.out.println("Debug: In run of SRH: GroupLEader");
			groupLeaderCrashHandle();
		}
		else{
			System.out.println("Debug: In run of SRH: Non-GroupLEader");
			nonGroupLeaderTask();
		}
	}

	/*function to handle group leader crash*/
	public void groupLeaderCrashHandle(){
		ByteArrayInputStream bs = null;
		ObjectInputStream in =  null;

		if(serverReplicaObj.isCrashed()){

			try {
				bs = new ByteArrayInputStream(requestPacket.getData());
				in = new ObjectInputStream(bs);

				Queue bufferList = new LinkedList();
				bufferList = (Queue) in.readObject();

				for(Object reqObj : bufferList){
					groupLeaderTask((Request)reqObj);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		groupLeaderTask(reqObj);
	}


	/*function for Group Leader Task*/
	public void groupLeaderTask(Request reqObj){
		System.out.println("Debug: In SRH groupLeaderTask : Reuqest object id and firstname and lastname : "+reqObj.getRequestID() + " "+ reqObj.getFirstName()+" "+reqObj.getLastName());
		Response responseObj[] = null;

		/*Debug:  Commenting OTherSR code */
//		Response responseOtherSR = sendAndRecieveOtherReplicas();
//		if(responseOtherSR==null){
//			System.out.println("Debug: In group leader task: responseOtherSR array object received is NULL");
//		}
		/*Comments ends here*/
		
		Response myLocationResponse = MyLocationServers(reqObj);

		/*Debug:  Commenting OTherSR code */
//		if(myLocationResponse!= null && responseOtherSR!= null){
//			Response finalResponse = majorityResponse(responseOtherSR, myLocationResponse);
//			sendToFrontEnd(finalResponse);
//		}
//		
//		else {
//			System.out.println("Debug: In group leader task: myLocationResponse object received is NULL");
//		}
		/*Comments ends here*/
		
		/*Debug: added for debugging myLocResponse */
		sendToFrontEnd(myLocationResponse);
		
	}
	

	/*function for Group Leader to send serialized packet to other 2 server replicas using FIFO*/	
	public Response sendAndRecieveOtherReplicas(){

		/*Creating FIFO object to broadcast it to other server replicas*/
		FIFO fifoObj = serverReplicaObj.getFifoObj();
		Thread threadFifo = new Thread(fifoObj);
		threadFifo.start();
		Response response = fifoObj.getResponse();
		return response;

	}

	/*function for Group Leader Process to send request to its own server*/
	public Response MyLocationServers(Request reqObj){
		System.out.println("Debug: In Myloci , Reuqest object id and firstname and lastname : "+reqObj.getRequestID() + " "+ reqObj.getFirstName()+" "+reqObj.getLastName());
		Response responseObj = null;

		/*Sending packet to its own location server*/
		clinicLocation = reqObj.getClinicLocation();
		System.out.println("Debug: cliniclocation "+clinicLocation);

		/*to check whether the req is processed before or not*/
		if(!(reqObj.getRequestID() <= serverReplicaObj.lastProcessed)){

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
			serverReplicaObj.lastProcessed++;

		}
		return responseObj;
	}


	/*function to return majority value*/
	public Response majorityResponse(Response responseOtherSR, Response myLocationResponse){
		int countTrue = 0;

		Response finalResponse = new Response();

		/*Switch case to decide majority response for all five functions */
//		switch(myLocationResponse.getMethodName()){
//
//		case "createDRecord":{
//			/*checking value from Server Replicas and incrementing counter if true*/
//			for(int i=0; i<responseOtherSR.length;i++){
//				if(responseOtherSR[i].isDoctorAdded()){
//					countTrue++;
//				}
//			}
//			/*checking value of my location server*/
//			if(myLocationResponse.isDoctorAdded()){
//				countTrue++;
//			}
//
//			/*if count is greater than or equal to 2, true is returned from 2 or more servers*/
//			if(countTrue>=2){
//				finalResponse.setDoctorAdded(true);
//			}
//			else{
//				finalResponse.setDoctorAdded(false);
//			}
//			break;
//		}
//
//		case "createNRecord":{
//			/*checking value from Server Replicas and incrementing counter if true*/
//			for(int i=0; i<responseOtherSR.length;i++){
//				if(responseOtherSR[i].isNurseAdded()){
//					countTrue++;
//				}
//			}
//			/*checking value of my location server*/
//			if(myLocationResponse.isNurseAdded()){
//				countTrue++;
//			}
//
//			/*if count is greater than or equal to 2, true is returned from 2 or more servers*/
//			if(countTrue>=2){
//				finalResponse.setNurseAdded(true);
//			}
//			else{
//				finalResponse.setNurseAdded(false);
//			}
//			break;
//		}
//
//		case "editRecord":{
//			/*checking value from Server Replicas and incrementing counter if true*/
//			for(int i=0; i<responseOtherSR.length;i++){
//				if(responseOtherSR[i].isRecordEdited()){
//					countTrue++;
//				}
//			}
//			/*checking value of my location server*/
//			if(myLocationResponse.isRecordEdited()){
//				countTrue++;
//			}
//
//			/*if count is greater than or equal to 2, true is returned from 2 or more servers*/
//			if(countTrue>=2){
//				finalResponse.setRecordEdited(true);
//			}
//			else{
//				finalResponse.setRecordEdited(false);
//			}
//			break;
//		}
//
//		case "getCount":{
//
//			if(responseOtherSR[0].getGetCount() == responseOtherSR[1].getGetCount()){
//				finalResponse.setGetCount(responseOtherSR[0].getGetCount());
//			}
//			else if(responseOtherSR[1].getGetCount() == myLocationResponse.getGetCount()){
//				finalResponse.setGetCount(responseOtherSR[1].getGetCount());
//			}
//			else if (myLocationResponse.getGetCount() == responseOtherSR[0].getGetCount()){
//				finalResponse.setGetCount(responseOtherSR[0].getGetCount());
//			}
//			else{
//				System.out.println("Response not recieved correctly : Try again!");
//			}
//			break;
//
//		}
//
//		case "transferRecord":{
//
//			/*checking value from Server Replicas and incrementing counter if true*/
//			for(int i=0; i<responseOtherSR.length;i++){
//				if(responseOtherSR[i].isRecordTransfered()){
//					countTrue++;
//				}
//			}
//			/*checking value of my location server*/
//			if(myLocationResponse.isRecordTransfered()){
//				countTrue++;
//			}
//
//			/*if count is greater than or equal to 2, true is returned from 2 or more servers*/
//			if(countTrue>=2){
//				finalResponse.setRecordTransfered(true);
//			}
//			else{
//				finalResponse.setRecordTransfered(false);
//			}
//			break;
//		}
//		}

		return finalResponse;
	}

	/*function to serialize final response and send it to front end */
	public void sendToFrontEnd(Response finalResponse){
		


		try {
			ByteArrayOutputStream bs = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bs);
			
			os.writeObject(finalResponse);

			os.close();
			bs.close();
			DatagramSocket socket = new DatagramSocket();

			
			byte[] buf = bs.toByteArray();
			DatagramPacket finalResponsePacket = new DatagramPacket(buf, buf.length, requestPacket.getAddress(), requestPacket.getPort());
			/*Debug: print add and port of req pakcet*/
			System.out.println("Debug: req add :"+ InetAddress.getLocalHost() +" and port "+requestPacket.getPort());
			socket.send(finalResponsePacket);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	/*function for non-group leader process*/
	public void nonGroupLeaderTask(){

		Response responseObj = null;

		/*Sending packet to its own location server*/
		clinicLocation = reqObj.getClinicLocation();

		if(reqObj.getRequestID()>serverReplicaObj.lastProcessed){
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
			serverReplicaObj.lastProcessed++;
		}
		sendToGroupLeader(responseObj);

	}

	/*function for non-group leader to send result back to Group Leader Process*/
	public void sendToGroupLeader(Response responseObj) {

		ByteArrayOutputStream bs = null;
		ObjectOutputStream os = null;
		DatagramSocket socket = null;

		try {

			socket = new DatagramSocket();
			bs = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bs);
			os.writeObject(responseObj);

			byte[] sendBuffer = bs.toByteArray();
			DatagramPacket responsePacket = new DatagramPacket(sendBuffer, sendBuffer.length,requestPacket.getAddress(),requestPacket.getPort());

			socket.send(responsePacket);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

	/*function to resolve request using deserialized request object and ClinicServer object*/
	public Response resolveRequest(Request reqObj, ClinicServer serverObj){
		System.out.println("Debug: In resolveRequest");
		Response responseObj = new Response();
		String methodName = reqObj.getMethodName();
		
		switchFunc switchFuncObj = switchFunc.valueOf(methodName);
		
		switch(switchFuncObj){

		case createDRecord: {
			responseObj.setMethodName("createDRecord");
			responseObj.setDoctorAdded(serverObj.createDRecord(reqObj.getFirstName(), reqObj.getLastName(), reqObj.getAddress(), reqObj.getPhone(), reqObj.getSpecialization(), reqObj.getLocation()));
			return responseObj;

		}
		case createNRecord:{
			responseObj.setMethodName("createNRecord");
			responseObj.setNurseAdded(serverObj.createNRecord(reqObj.getFirstName(), reqObj.getLastName(), reqObj.getDesignation(), reqObj.getStatus_Date(), reqObj.getStatus()));
			return responseObj;
		}
		case editRecord:{
			responseObj.setMethodName("editRecord");
			responseObj.setRecordEdited(serverObj.editRecord(reqObj.getRecordID(), reqObj.getFieldName(), reqObj.getNewValue()));
			return responseObj;
		}
		case getCount:{
			responseObj.setMethodName("getCount");
			responseObj.setGetCount(serverObj.getCount(reqObj.getRecordType()));
			return responseObj;
		}
		case transferRecord:{
			responseObj.setMethodName("transferRecord");
			responseObj.setRecordTransfered(serverObj.transferRecord(reqObj.getManagerID(), reqObj.getRecordID(), reqObj.getLocation()));
			return responseObj;
		}
		default :{
			System.out.println("This is default");
			return null;
		}
		}
	}

	/*Getters and Setters*/
	public DatagramPacket getRequestPacket() {
		return requestPacket;
	}

	public void setRequestPacket(DatagramPacket requestPacket) {
		this.requestPacket = requestPacket;
	}

	public ServerReplica getServerReplicaObj() {
		return serverReplicaObj;
	}

	public void setServerReplicaObj(ServerReplica serverReplicaObj) {
		this.serverReplicaObj = serverReplicaObj;
	}

	public Request getReqObj() {
		return reqObj;
	}

	public void setReqObj(Request reqObj) {
		this.reqObj = reqObj;
	}

	public String getClinicLocation() {
		return clinicLocation;
	}

	public void setClinicLocation(String clinicLocation) {
		this.clinicLocation = clinicLocation;
	}


}
