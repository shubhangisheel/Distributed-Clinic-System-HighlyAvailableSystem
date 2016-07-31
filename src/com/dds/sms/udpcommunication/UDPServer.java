package com.dds.sms.udpcommunication;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.dds.sms.datatypes.DoctorRecord;
import com.dds.sms.server.ClinicServer;

public class UDPServer extends Communication implements Runnable{

	public ClinicServer server;
	public DatagramPacket serverpacket;

	/*Constructor*/
	public UDPServer(DatagramPacket packet, ClinicServer server) {
		serverpacket = packet;
		this.server = server;
		System.out.println("Debug: In CONSTRUCTOR : "+server.getLocation()+" and packet address: "+serverpacket.getAddress() + " and port number: "+ serverpacket.getPort());
	}
	
	/*UPDATE=ASN2*/
	public void run(){
		receive();
	}

	/*Override run() method for threads*/
	public void receive(){

		System.out.println("Debug: In UDP Server class of "+server.getLocation() + " and send function");

		/*Creating a socket*/
		DatagramSocket socketResult = null;

		/* ******************************************************EDITING FOR ASN2********************************************************************* */

		String received = new String(serverpacket.getData(), 0, serverpacket.getLength()); /*ASN2*/
		System.out.println("Debug: String received is "+ received);

		/*if condition to check whether received message is from getCount, createDRecord or createNRecord  */

		int index1 = received.indexOf(",");
		System.out.println("Debug: Index is: "+ index1);
		String functionName = received.substring(0, index1);
		
		System.out.println("Debug: functionName is "+functionName);
		/*LOOP TO CHECK FUNCTIONNAME*/

		/* 1. When functionName is getCount*/

		if(functionName.equals("getCount")){

			/*PREVIOUS 'GET COUNT' CODE PASTED in this condition: NOTE no changes made in this code*/
			/*Parsing integer to String to store into a message*/
			String message = Integer.toString(server.getNum_Of_Records()); /*ASN2*/

			System.out.println("Debug: In UDP Server class: "+server.getLocation()+" message sent:  "+message);

			/*Creating a packet to store result*/
			byte[] bufResult = new byte[256];
			bufResult = message.getBytes();

			System.out.println("Debug: In UDP Server class: bufResult after conversion: "+bufResult+" Clinic server location: "+server.getLocation());

			System.out.println("Debug: In UDP Server class : "+server.getLocation()+" and packet address: "+serverpacket.getAddress() + " and port number: "+ serverpacket.getPort());

			DatagramPacket packetResult = new DatagramPacket(bufResult, message.length(), serverpacket .getAddress(), serverpacket .getPort());

			System.out.println("Debug: UDPS: sending packet to "+ serverpacket.getAddress() +" and port 0 "+serverpacket .getPort()+" from server "+server.getLocation());

			/*Sending packetResult to UDPClientServer*/
			try {
				socketResult = new DatagramSocket();
				socketResult.send(packetResult);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				socketResult.close();
			}

		}

		/* 2. When functionName is createDRecord*/
		else if(functionName.equals("createDRecord")){
			System.out.println("Debug: ");
			String message = "false";

			int index2 = received.indexOf(",",index1+1);
			String add = received.substring(index1+1, index2); /* Since beginIndex is inclusive, so +1 : [) */

			int index3 = received.indexOf(",",index2+1);
			String fName = received.substring(index2+1, index3);

			int index4 = received.indexOf(",", index3+1);
			String lName = received.substring(index3+1,index4);

			int index5 = received.indexOf(",", index4+1);
			String loc = received.substring(index4+1,index5);

			int index6 = received.indexOf(",", index5+1);
			String phn = received.substring(index5+1,index6);

			String spclztn = received.substring(index6+1,received.length());

			boolean recordAdded = server.createDRecord(fName, lName, add, phn, spclztn, loc);

			if(recordAdded){
				message = "true";
			}

			/*Creating a packet to store message*/
			byte[] bufResult = new byte[256];
			bufResult = message.getBytes();

			DatagramPacket packetResult = new DatagramPacket(bufResult, message.length(), serverpacket .getAddress(), serverpacket .getPort());

			/*Sending packetResult to UDPClientServer*/
			try {
				socketResult = new DatagramSocket();
				socketResult.send(packetResult);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				socketResult.close();
			}
		}

		else if(functionName.equals("createNRecord")){

			String message = "false";

			int index2 = received.indexOf(",",index1+1);
			String fName = received.substring(index1+1, index2); /* Since beginIndex is inclusive, so +1 : [) */

			int index3 = received.indexOf(",",index2+1);
			String lName = received.substring(index2+1, index3);

			int index4 = received.indexOf(",", index3+1);
			String desig = received.substring(index3+1,index4);

			int index5 = received.indexOf(",", index4+1);
			String stat_Date = received.substring(index4+1,index5);

			String stat = received.substring(index5+1,received.length());

			boolean recordAdded = server.createNRecord(fName, lName, desig, stat_Date, stat);

			if(recordAdded){
				message = "true";
			}

			/*Creating a packet to store message*/
			byte[] bufResult = new byte[256];
			bufResult = message.getBytes();

			DatagramPacket packetResult = new DatagramPacket(bufResult, message.length(), serverpacket .getAddress(), serverpacket .getPort());

			/*Sending packetResult to UDPClientServer*/
			try {
				socketResult = new DatagramSocket();
				socketResult.send(packetResult);
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				socketResult.close();
			}

		}
		/* ******************************************************EDITING FOR ASN2********************************************************************* */
	}
}
