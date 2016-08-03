package com.dds.sms.frontend;



import com.dds.sms.frontend.communication.CrashResponse;
import com.dds.sms.frontend.communication.FECommunication;
import com.dds.sms.frontend.Response;


public class FrontEnd {
	
	private String clinicLocation;
	private CrashResponse crashResponseObj;
	private int groupLeader;
	public static int RequestID;
	
	public FrontEnd(String clinicLocation){
		this.clinicLocation = clinicLocation;
		this.crashResponseObj = new CrashResponse(this);
		
		Thread thread = new Thread(crashResponseObj);
		thread.start();
	}

	/*Function to send packet for a doctor record. Input: Doctor details, Output: Doctor obj packet, Updated log file*/
	public boolean createDRecord (String fName, String lName, String add, String phn, String spclztn, String loc){
		
		Response responseObj = null;
		Request reqObj = new Request(giveID(), "createDRecord",fName, lName, add,  phn, spclztn, loc, clinicLocation);
		FECommunication feCommunicationObj = new FECommunication(reqObj);
		feCommunicationObj.send();
		responseObj = feCommunicationObj.recieve();
		return responseObj.isDoctorAdded();
		
	}
	
	/*Function to create a nurse record. Input: Doctor details, Output: Doctor obj, Updated log file*/
	public boolean	createNRecord (String fName, String lName, String desig,String stat_Date, String stat){
		
		Response responseObj = null;
		Request reqObj = new Request (giveID(),"createNRecord",fName, lName, desig,stat_Date, stat, clinicLocation);
		FECommunication feCommunicationObj = new FECommunication(reqObj);
		feCommunicationObj.send();
		responseObj = feCommunicationObj.recieve();
		return responseObj.isNurseAdded();
		
	}
	
	public boolean editRecord (String recordID, String fieldName, String newValue){
		
		Response responseObj = null;
		Request reqObj = new Request (giveID(),"editRecord",recordID, fieldName, newValue, clinicLocation);
		FECommunication feCommunicationObj = new FECommunication(reqObj);
		feCommunicationObj.send();
		responseObj = feCommunicationObj.recieve();
		return responseObj.isRecordEdited();
		
	}
	
	public int getCount(String recordType){
		
		Response responseObj = null;
		Request reqObj = new Request (giveID(),"getCount", recordType, clinicLocation);
		FECommunication feCommunicationObj = new FECommunication(reqObj);
		feCommunicationObj.send();
		responseObj = feCommunicationObj.recieve();
		return responseObj.getGetCount();

	}
	
	public boolean transferRecord(String managerID,String recordID, String location){
		Request reqObj = new Request();
		Response responseObj = null;
		
		reqObj.setRequestID(giveID());
		reqObj.setManagerID(managerID);
		reqObj.setRecordID(recordID);
		reqObj.setLocation(location);
		reqObj.setClinicLocation(clinicLocation);
		reqObj.setMethodName("transferRecord");
		
		FECommunication feCommunicationObj = new FECommunication(reqObj);
		feCommunicationObj.send();
		responseObj = feCommunicationObj.recieve();
		return responseObj.isRecordTransfered();
		
	}

	public synchronized static int giveID(){
		return RequestID++;
		
	}
	public static int getRequestID() {
		return RequestID;
	}

	public static void setRequestID(int requestID) {
		RequestID = requestID;
	}

	public String getClinicLocation() {
		return clinicLocation;
	}

	public void setClinicLocation(String clinicLocation) {
		this.clinicLocation = clinicLocation;
	}

	public CrashResponse getCrashResponseObj() {
		return crashResponseObj;
	}

	public void setCrashResponseObj(CrashResponse crashResponseObj) {
		this.crashResponseObj = crashResponseObj;
	}

	public int getGroupLeader() {
		return groupLeader;
	}

	public void setGroupLeader(int groupLeader) {
		this.groupLeader = groupLeader;
	}
	
	
}
