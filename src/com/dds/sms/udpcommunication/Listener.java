package com.dds.sms.udpcommunication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.dds.sms.server.ClinicServer;

/*Class to receive packet in an infinite loop for all Clinic servers */
public class Listener extends Communication{

	ClinicServer server;
	DatagramSocket socket;

	/*Constructor of Class Listener*/
	public Listener(ClinicServer server) {
		this.server = server;

		try {
			this.socket = new DatagramSocket(server.getPortNumber());
			System.out.println("Debug: In Listener of "+server.getLocation()+ " socket created with port number: "+ server.getPortNumber());
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	/*Function to listen infinitely at the defined port*/
	public void portlistener(){

		System.out.println(server.getLocation()+ " server is up and running");

		/*Creating a packet to receive a packet at the port*/
		byte[] buf = new byte[256];

		/*Listens forever*/
		while(true){
			try {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);

				String packetString = new String(packet.getData(), 0, packet.getLength()) ;
				System.out.println("Debug: In Listener of: "+server.getLocation()+ " message received packet: " + packetString+" packet adrress: "+packet.getAddress()+" and packet port number: "+packet.getPort());

				/*Passing packet to other 2 UDPServers*/
				UDPServer objUDPServer = new UDPServer(packet, server);
				Thread serverUDPThread = new Thread(objUDPServer);
				serverUDPThread.start();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
