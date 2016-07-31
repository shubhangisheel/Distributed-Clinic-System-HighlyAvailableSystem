package com.dds.sms.udpcommunication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import com.dds.sms.datatypes.DoctorRecord;
import com.dds.sms.datatypes.Record;
import com.dds.sms.server.ClinicServer;

public class UDPClientServer extends Communication implements Runnable {

	private ClinicServer server;
	private DatagramSocket socketRequest;
	private int resultGetCount; 
	String callFrom; 
	boolean transferConfirmation; /*NEW MEMBER - ASN2*/
	String transferLocation; /*NEW MEMBER - ASN2*/
	String data; /*NEW MEMBER - ASN2*/

	/*Constructor*/
	public UDPClientServer(ClinicServer server, String callFrom) {
		this.server = server;
		resultGetCount = 0;
		this.callFrom = callFrom;
		transferConfirmation = false;
		data = ""; /*ASN2*/

		/*Creating a socketRequest to send and receive packets*/
		try {
			socketRequest = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/*OVERLOADED CONSTRUCTOR - ASN2*/
	public UDPClientServer(ClinicServer server, String callFrom, String data, String transferLocation){

		this.server = server;
		resultGetCount = 0;
		this.callFrom = callFrom;
		transferConfirmation = false;
		this.data = data;
		this.transferLocation = transferLocation;

		/*Creating a socketRequest to send and receive packets*/
		try {
			socketRequest = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}

	}

	/*Getters and Setters*/
	public int getResultGetCount() {
		return resultGetCount;
	}

	public void setResultGetCount(int resultGetCount) {
		this.resultGetCount = resultGetCount;
	}

	/*ASN2*/
	public boolean getTransferConfirmation(){
		return transferConfirmation;
	}

	/*Override run() for Threads*/
	public void run(){
		send();
		receive();		
	}

	/*Send function to send the packet to the other 2 UDPServers*/
	public void send(){

		/*Condition to check whether call is from getCount() or transferRecord() */

		/*logic for getCount()*/
		if(callFrom == "getCount"){

			String message = "getCount,";

			/*Creating packet*/
			byte[] bufRequest = new byte[256];
			bufRequest = message.getBytes();

			try {

				/*if else condition to manage different clinic locations*/
				if(server.getPortNumber() == 2525){
					DatagramPacket packetRequest1 = new DatagramPacket(bufRequest, bufRequest.length, InetAddress.getLocalHost(), 3030);
					System.out.println("Debug: UDPCS: sending packet to "+ packetRequest1.getAddress() +" and port 0 "+packetRequest1.getPort()+" from server "+server.getLocation());
					socketRequest.send(packetRequest1);

					DatagramPacket packetRequest2 = new DatagramPacket(bufRequest, bufRequest.length, InetAddress.getLocalHost(), 3535);
					System.out.println("Debug: UDPCS: sending packet to "+ packetRequest2.getAddress() +" and port 0 "+packetRequest2.getPort()+" from server "+server.getLocation());
					socketRequest.send(packetRequest2);

				}
				else if(server.getPortNumber() == 3030){
					DatagramPacket packetRequest1 = new DatagramPacket(bufRequest, bufRequest.length, InetAddress.getLocalHost(), 2525);
					socketRequest.send(packetRequest1);

					DatagramPacket packetRequest2 = new DatagramPacket(bufRequest, bufRequest.length, InetAddress.getLocalHost(), 3535);
					socketRequest.send(packetRequest2);


				}
				else if(server.getPortNumber() == 3535){
					DatagramPacket packetRequest1 = new DatagramPacket(bufRequest, bufRequest.length, InetAddress.getLocalHost(), 3030);
					socketRequest.send(packetRequest1);

					DatagramPacket packetRequest2 = new DatagramPacket(bufRequest, bufRequest.length, InetAddress.getLocalHost(), 2525);
					socketRequest.send(packetRequest2);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/*logic for transferRecord() - ASN2*/
		else if (callFrom == "transferRecord"){

			/*Creating packet of 'data' transferred from ClinicServer*/
			byte[] bufRequest = new byte[256];
			bufRequest = data.getBytes();
			System.out.println("Debug: In send function for transferRecord: Data = "+ data);

			try{

				/*if else condition to manage different clinic locations*/
				if(transferLocation.equals("MTL")){
					DatagramPacket packetRequest1 = new DatagramPacket(bufRequest, bufRequest.length, InetAddress.getLocalHost(), 2525);
					socketRequest.send(packetRequest1);
					System.out.println("Debug: Sent packet for TRANSFER RECORD FOR MTL");
				}
				else if (transferLocation.equals("LVL")){
					DatagramPacket packetRequest2 = new DatagramPacket(bufRequest, bufRequest.length, InetAddress.getLocalHost(), 3030);
					socketRequest.send(packetRequest2);
					System.out.println("Debug: Sent packet for TRANSFER RECORD FOR LVL");
				}
				else if (transferLocation.equals("DDO")){
					DatagramPacket packetRequest3 = new DatagramPacket(bufRequest, bufRequest.length, InetAddress.getLocalHost(), 3535);
					socketRequest.send(packetRequest3);
					System.out.println("Debug: Sent packet for TRANSFER RECORD FOR DDO");
				}

			}catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/*Receive function to receive packetResult from other 2 UDPServers*/
	public void receive() {

		/* 1. When functionName is getCount*/
		if(callFrom.equals("getCount")){

			/*Initializing 2 integers to save results received*/
			int result1 = 0;
			int result2 = 0;

			try {
				byte[] bufResult1 = new byte[256];
				DatagramPacket packetResult1 = new DatagramPacket(bufResult1, bufResult1.length); 
				socketRequest.receive(packetResult1);

				System.out.println("Debug: Received one packet");

				byte[] bufResult2 = new byte[256];
				DatagramPacket packetResult2 = new DatagramPacket(bufResult2, bufResult2.length); 
				socketRequest.receive(packetResult2);
				System.out.println("Debug: Received second packet");

				/*Converting results to Strings*/
				String result11 = new String(packetResult1.getData(), 0, packetResult1.getLength());
				String result22 = new String(packetResult2.getData(), 0, packetResult2.getLength()) ;

				System.out.println("Debug: result1 "+result11 + " and result2 "+result22 + " ");

				/*Converting results to Integers from Strings*/
				result1 = Integer.parseInt(result11);
				result2 = Integer.parseInt(result22);


			} catch (IOException e) {
				e.printStackTrace();
			}

			/*Adding both the result1 and result2 and returning final value*/
			resultGetCount = result1 + result2;

		}
		
		/* 1. When functionName is getCount*/
		else if(callFrom.equals("transferRecord")){
			
			byte[] bufResult1 = new byte[256];
			DatagramPacket packetResult1 = new DatagramPacket(bufResult1, bufResult1.length); 
			
			try {
				System.out.println("Debug: BEFORE Receive packet for TRANSFER RECORD");
				socketRequest.receive(packetResult1);
				System.out.println("Debug: AFTER Receive packet for TRANSFER RECORD");
				
			String result = new String(packetResult1.getData(), 0, packetResult1.getLength());
			System.out.println("Debug: RESULT for transfer record: "+result);
			
			transferConfirmation = Boolean.parseBoolean(result);
			System.out.println("Debug: In receive function of UDPClientServer : transferConfirmation = "+transferConfirmation);	 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}		
}

