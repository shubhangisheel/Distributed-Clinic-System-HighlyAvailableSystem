package com.dds.sms.dummy;

import java.net.DatagramPacket;

import com.dds.sms.frontend.Request;
import com.dds.sms.frontend.Response;

public class FIFO implements Runnable {
	
	private DatagramPacket RequestPacket;
	private Response response[];
	
	public FIFO(DatagramPacket RequestPacket){
		this.RequestPacket = RequestPacket;
		response = new Response[2];
	}
	
	public FIFO(){
	}
	
	public void broadcast(DatagramPacket requestPacket){
	}
	
	public void run(){
		broadcast(getRequestPacket());
		Response response[] =recieveResponse();
		
	}
	private Response[] recieveResponse() {
		return null;
	}
	public DatagramPacket getRequestPacket() {
		return RequestPacket;
	}
	public void setRequestPacket(DatagramPacket requestPacket) {
		RequestPacket = requestPacket;
	}
	public Response[] getResponse() {
		return response;
	}
	public void setResponse(Response[] response) {
		this.response = response;
	}
	
}
