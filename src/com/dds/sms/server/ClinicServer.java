package com.dds.sms.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.dds.sms.datatypes.*;
import com.dds.sms.udpcommunication.Listener;
import com.dds.sms.udpcommunication.UDPClientServer;

public class ClinicServer implements Runnable {

	private int num_Of_Records;
	private int recordIDCounter;
	private int portNumber;
	private String location;
	//private ArrayList<Record> list;
	private ArrayList<String> managers;
	private FileWriter logFile;
	private FileWriter managerFile;
	
	/*ENUM*/
	private enum switchFunc{
		address, firstName, lastName,location, phone, specialization, designation, status,status_Date  
	}

	/* Creating Hashmap of 26 alphabets (Key) and objects of DR and NR(List)*/
	private HashMap <Character , ArrayList<Record>> clinicDatabase; 

	/*Objects of Doctor and Nurse*/
	DoctorRecord objDR;
	NurseRecord objNR;

	/*Constructor*/
	public ClinicServer(String location,int rmiRegisteryPort, int portNumber) throws RemoteException{

		this.location = location;
		this.portNumber = portNumber;

		num_Of_Records = 0;
		recordIDCounter = 10000;

		/*Initializing clinicDatabase keys*/
		clinicDatabase = new HashMap<Character, ArrayList<Record>>();	
		for(char i='A'; i<='Z'; i++){
			clinicDatabase.put(i, new ArrayList<Record>());
		}

		/*Initializing Managers list*/
		managers = new ArrayList<String>();
		int x = 1000;
		for(int i=0; i<5; i++){
			x++;
			String s = getLocation() + Integer.toString(x);
			managers.add(s);
			System.out.println(managers.get(i));
		}


		/*Message for server logs*/
		String message = location + " server created at " + Calendar.getInstance().getTime() 
				+ "and a corresponding remote object registered with RMI Registry" + "\n" ;

//		/*Creating server logs*/
//
//		String fileName = location + "_log" + ".txt" ;
//		try {
//			logFile = new FileWriter ( fileName ) ;
//			logFile.write(message + System.lineSeparator());
//			logFile.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

	/*Getters and Setters*/
	public boolean managerAuthentication(String managerID){
		return(managers.contains(managerID));
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getRecordIDCounter() {
		return recordIDCounter;
	}

	public void setRecordIDCounter(int recordIDCounter) {
		this.recordIDCounter = recordIDCounter;
	}

	public int getNum_Of_Records() {
		return num_Of_Records;
	}

	public void setNum_Of_Records(int num_Of_Records) {
		this.num_Of_Records = num_Of_Records;
	}

	/*GETTER FOR HASHMAP - ASN2*/
	public HashMap<Character, ArrayList<Record>> getClinicDatabase(){
		return clinicDatabase;
	}

	
	/*Function to create a doctor record. Input: Doctor details, Output: Doctor obj, Updated log file*/
	public boolean createDRecord (String fName, String lName, String add, String phn, String spclztn, String loc){

		ArrayList<Record> list = null;
		/*String to create RecordID*/
		String recordID;

		synchronized(this){
			recordIDCounter++;
			recordID = "DR"+ recordIDCounter;
		}

		/*Creating Record type object*/
		Record objDR = new DoctorRecord(fName, lName, add, phn, spclztn, loc, recordID);

		/*Checking whether firstletter in lastname is uppercase or not */
		Character firstletter = lName.charAt(0);
		boolean valid = Character.isUpperCase(firstletter);

		/*Adding record to matched key*/
		if(valid){

			list = clinicDatabase.get(firstletter);

			if(!doctorExists(fName, phn, list)){

				synchronized (list){
					list.add(objDR);
				}

				clinicDatabase.put(firstletter, list);

				/*Printing record for clear understanding*/

				System.out.println("Record with firstname : "+fName+"is added" );

				/*Updating total count of records for this server*/
				synchronized (this){
					num_Of_Records++;
				}
				String message = getLocation() + " server " + " added a doctor record with RecordID: "+recordID +" at "+ Calendar.getInstance().getTime() + "\n" ;
				writeLog(logFile, message);
				return true;

			}else{
				String message = getLocation() + " server " + " An unsucessful attempt to create doctor record with RecordID: "+recordID +" at "+ Calendar.getInstance().getTime() + "\n" ;
				writeLog(logFile, message);
				return false;
			}

		}

		else{
			String message = getLocation() + " server " + " An unsucessful attempt to create doctor record with RecordID: "+recordID +" at "+ Calendar.getInstance().getTime() + "\n" ;
			writeLog(logFile, message);
			return false;
		}
	}

	/*Function to create a nurse record. Input: Doctor details, Output: Doctor obj, Updated log file*/
	public boolean	createNRecord (String fName, String lName, String desig,String stat_Date, String stat){

		/*String to create RecordID*/
		String recordID;

		synchronized(this){
			recordIDCounter++;
			recordID = "NR"+ recordIDCounter;
		}

		/*Creating Record type object*/
		NurseRecord objNR = new NurseRecord(fName, lName, desig, stat_Date, stat, recordID);

		/*Checking whether firstletter in lastname is uppercase or not */
		Character firstletter = lName.charAt(0);
		System.out.println(firstletter);
		boolean valid = Character.isUpperCase(firstletter);
		System.out.println(valid);

		/*Adding record to matched key*/
		if(valid){
			ArrayList<Record> list = clinicDatabase.get(firstletter);

			if(!nurseExists(fName,stat_Date, list)){

				synchronized (list){
					list.add(objNR);
				}

				/*Printing hashMap for clear understanding*/
				for(int i = 0; i<list.size();i++){
					System.out.println("Key:"+ firstletter+"  RecordID "+ (list.get(i)).getRecordID());
				}

				/*Updating total count of records for this server*/
				synchronized (this){
					num_Of_Records++;
				}
				String message = getLocation() + " server " + " added a nurse record with RecordID: "+recordID +" at "+ Calendar.getInstance().getTime() + "\n" ;
				writeLog(logFile, message);
				return true;
			}else{
				String message = getLocation() + " server " + " An unsucessful attempt to create doctor record with RecordID: "+recordID +" at "+ Calendar.getInstance().getTime() + "\n" ;
				writeLog(logFile, message);
				return false;
			}

		}

		else{
			String message = getLocation() + " server " + " An unsucessful attempt to create doctor record with RecordID: "+recordID +" at "+ Calendar.getInstance().getTime() + "\n" ;
			writeLog(logFile, message);
			return false;
		}
	}

	public boolean editRecord (String recordID, String fieldName, String newValue){

		/*Creating a flag of type boolean*/
		boolean flag = false;
		ArrayList<Record> editlist;
		Record editObj = null;

		/*Retrieving an object from recordID and storing it in editObj*/
		for (char i='A'; i<='Z'; i++){
			editlist = clinicDatabase.get(i);

			for(int j = 0; j<editlist.size()-1; j++){
				if(editlist.get(j).getRecordID().equals(recordID)){
					editObj = editlist.get(j);
					break;
				}
			}
			if(editObj != null){
				break;
			}			
		}

		if(editObj == null){
			return false;
		}

		System.out.println("Debug: "+ editObj.getRecordID());
		/*SwitchCase to handle different field names*/

		synchronized (editObj){
			
			/*ENUM*/
			switchFunc switchFuncObj = switchFunc.valueOf(fieldName);

			switch(switchFuncObj){

			case address:{
				((DoctorRecord) editObj).setAddress(newValue);
				flag = true;
				break;
			}

			case firstName:{
				if(editObj.getRecordID().contains("DR")){
					((DoctorRecord) editObj).setFirstName(newValue);
					//			System.out.println(((DoctorRecord) editObj).getFirstName());
				}
				else{
					((NurseRecord) editObj).setFirstName(newValue);
					System.out.println(((DoctorRecord) editObj).getFirstName());
				}
				flag = true;
				break;
			}

			case lastName:{
				flag =updateLastName(editObj,newValue);
				break;
			}

			case location:{	
				((DoctorRecord) editObj).setLocation(newValue);
				flag = true;
				break;
			}

			case phone:{
				((DoctorRecord) editObj).setPhone(newValue);
				flag = true;
				break;
			}

			case specialization:{
				((DoctorRecord) editObj).setSpecialization(newValue);
				flag = true;
				break;
			}

			case designation:{
				((NurseRecord) editObj).setDesignation(newValue);
				flag = true;
				break;
			}

			case status:{
				((NurseRecord) editObj).setStatus(newValue);
				flag = true;
				break;
			}

			case status_Date:{
				((NurseRecord) editObj).setStatus_Date(newValue);
				flag = true;
				break;
			}

			default:  flag = false;

			}
		}

		if(flag){
			String message = getLocation() + " server " + " Successfully updated RecordID: "+recordID +" at "+ Calendar.getInstance().getTime() + "\n" ;
			writeLog(logFile, message);
		}else
		{
			String message = getLocation() + " server " + " An unsucessful attempt to update RecordID: "+recordID +" at "+ Calendar.getInstance().getTime() + "\n" ;
			writeLog(logFile, message);	
		}
		return flag;	
	}

	public boolean updateLastName(Record editObj, String newValue){

		if(editObj.getRecordID().contains("DR")){

			/*Creating temporary object to store original object*/
			DoctorRecord tempObj = (DoctorRecord) editObj;
			ArrayList<Record> list = clinicDatabase.get(((DoctorRecord) editObj).getLastName().charAt(0));
			list.remove(editObj);

			/*Extracting lastname firstletter and saving it to a new value*/
			((DoctorRecord) tempObj).setLastName(newValue);
			boolean valid = Character.isUpperCase(newValue.charAt(0));

			if(valid){
				list = clinicDatabase.get(newValue.charAt(0));
				list.add(tempObj);
				System.out.println("updated last name: "+tempObj.getLastName());
				ArrayList<Record> tempList = clinicDatabase.get('P');
				for(int i =0; i<tempList.size();i++){
					System.out.println(tempList.get(i).getRecordID());
				}
				return true;
			}

			else{
				System.out.println("Error: Please enter new last name with the first letter in capital");
				return false;
			}
		}
		else{

			/*Creating temporary object to store original object*/
			NurseRecord tempObj = (NurseRecord) editObj;
			ArrayList<Record> list = clinicDatabase.get(((NurseRecord) editObj).getLastName().charAt(0));
			list.remove(editObj);
			System.out.println("Record deleted from previous key's  array list");


			/*Extracting lastname firstletter and saving it to a new value*/
			((NurseRecord) tempObj).setLastName(newValue);
			boolean valid = Character.isUpperCase(newValue.charAt(0));

			if(valid){
				list = clinicDatabase.get(newValue.charAt(0));
				list.add(tempObj);
				return true;
			}

			else{
				System.out.println("Error: Please enter new last name with the first letter in capital");
				return false;
			}
		}
	}

	public int getCount(String recordType){

		/*Creating an object of type UDPClientServer and assigning it to a thread*/
		UDPClientServer objUDPCS = new UDPClientServer(this, "getCount");
		Thread threadUDPCS = new Thread(objUDPCS);
		threadUDPCS.start();

		try {
			threadUDPCS.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		/*Creating a variable to store the return value*/
		int result = objUDPCS.getResultGetCount();

		/*Returning summation of total count received from 2 servers and itself*/
		String message = getLocation() + " server " + "Count sent to client "+this.getNum_Of_Records() + result+ Calendar.getInstance().getTime() + "\n" ;
		writeLog(logFile, message);

		return this.getNum_Of_Records() + result;
	}

	public boolean doctorExists(String fName, String phn, ArrayList<Record> list){

		boolean flag= false;

		for(int i =0; i<list.size();i++){
			if(list.get(i).getRecordID().contains("DR")){
				String firstName= ((DoctorRecord)list.get(i)).getFirstName();
				String phoneNum = ((DoctorRecord)list.get(i)).getPhone();
				if(fName.equals(firstName) && phn.equals(phoneNum)){
					flag=true;
				}
			}
		}
		return flag;
	}

	public boolean nurseExists(String fName, String status_date,ArrayList<Record> list){
		boolean flag= false;

		for(int i =0; i<list.size();i++){
			if(list.get(i).getRecordID().contains("NR")){
				String firstName= ((NurseRecord)list.get(i)).getFirstName();
				String stat_date = ((NurseRecord)list.get(i)).getStatus_Date();
				if(fName.equals(firstName) && stat_date.equals(status_date)){
					flag=true;
				}
			}

		}
		return flag;
	}

	public void run(){
		(new Listener(this)).portlistener();
	}

	public synchronized void writeLog ( FileWriter obj, String message ) {
//		try {
//			obj.write( message + System.lineSeparator() ) ;
//			obj.flush();
//		} catch ( IOException e ) {
//			System.out.println( "Could not write the following string to log for " + location + " " + e.getMessage() ) ;
//		}
	}

	/*Function to check recordID exists-ASN2*/
	public Record recordExists(String recordID){

		ArrayList<Record> temp;

		/*Checking recordID existence*/
		for(char i = 'A'; i<='Z';i++){
			temp = getClinicDatabase().get(i);

			for(int j = 0; j<temp.size(); j++){
				if(temp.get(j).getRecordID().equals(recordID)){
					return temp.get(j);
				}
			}
		}
		return null;
	}


	/*Function transferRecord function for Assignment 2-ASN2*/
	public boolean transferRecord(String managerID,String recordID, String location){

		boolean flag;
		String data = null;
		ArrayList<Record> list = null;

		/*Manager Authentication*/
		if(!managerAuthentication(managerID)){
			return false;
		}

		Record temp = recordExists(recordID);
		/*RecordID exists or not*/
		if(temp==null){
			return false;
		}

		/* synchronizing object until value for flag is obtained */
		synchronized (temp){

			/*if doctor record: fetching data to be sent*/
			if(temp.getRecordID().contains("DR")){

				String add = ((DoctorRecord)temp).getAddress();
				String fName = ((DoctorRecord)temp).getFirstName();
				String lName = ((DoctorRecord)temp).getLastName();
				String loc = ((DoctorRecord)temp).getLocation();
				String phn = ((DoctorRecord)temp).getPhone();
				String spclztn = ((DoctorRecord)temp).getFirstName();

				data = "createDRecord" + "," + add + "," + fName + "," + lName + "," + loc + "," + phn + "," + spclztn;

			}

			/*if nurse record: fetching data to be sent*/
			else if (temp.getRecordID().contains("NR")){

				String fName = ((NurseRecord)temp).getFirstName();
				String lName = ((NurseRecord)temp).getLastName();
				String desig = ((NurseRecord)temp).getDesignation();
				String stat_Date = ((NurseRecord)temp).getStatus_Date();
				String stat = ((NurseRecord)temp).getStatus();

				data = "createNRecord" + "," + fName + "," + lName + "," + desig + "," + stat_Date + "," + stat;
			} 

			else{
				return false;
			}

			System.out.println("Debug: In tranfserREcord function in ClinicServer: Location = "+location);
			/*Creating an object of type UDPClientServer and assigning it to a thread*/
			UDPClientServer objUDPCSOther = new UDPClientServer(this, "transferRecord", data, location);
			Thread threadUDPCSOther = new Thread(objUDPCSOther);
			threadUDPCSOther.start();

			try {
				threadUDPCSOther.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			/*Creating a variable to store the return value*/
			flag = objUDPCSOther.getTransferConfirmation();

		}
		/*synchronized closed*/

		System.out.println("Debug: In transferRecord function of ClinicServer : flag = "+flag);	 

		if(flag==true){

			/*DELETE EXISTING RECORD HERE */
			/*Returning true is transfer confirmation is received*/
			String message = "In "+getLocation() + " server, recordID" + recordID +" is transferred to "+location+ " at "+ Calendar.getInstance().getTime() + "\n" ;
			writeLog(logFile, message);
		}

		else{
			String message = "In "+getLocation() + " server, recordID" + recordID +" is NOT transferred to "+location+ " at "+ Calendar.getInstance().getTime() + "\n" ;
			writeLog(logFile, message);
		}

		return flag;
	}
}





