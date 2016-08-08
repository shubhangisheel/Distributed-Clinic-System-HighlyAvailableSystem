package com.dds.sms.main;

import java.rmi.RemoteException;

import com.dds.sms.ServerReplica.ServerReplica;
import com.dds.sms.dummy.FIFO;
import com.dds.sms.server.ClinicServer;

public class MainServerReplica {

	public static void main(String args []){
		
	      /*Creating objects of ClinicServer for 3 locations: Montreal (MTL), Laval (LVL) and DDO*/
			ClinicServer serverObjSR1[] = new ClinicServer[3];
//			ClinicServer serverObjSR2[] = null;
//			ClinicServer serverObjSR3[] = null;
			
			int replicaPorts[] = new int[2];
			replicaPorts[0] = 5000;
			replicaPorts[1] = 5100;
			
			
			FIFO dummyFifo = new FIFO();
			
			/*Creating Registry and assigning to respective objects*/  
			
			try {
				
				serverObjSR1[0] = new ClinicServer("MTL",5002, 2525);
				serverObjSR1[1] = new ClinicServer("LVL",5002, 3030);
				serverObjSR1[2] = new ClinicServer("DDO",5002, 3535);
				
				Thread MTLthread = new Thread(serverObjSR1[0]);
				Thread LVLthread = new Thread(serverObjSR1[1]);
				Thread DDOthread = new Thread(serverObjSR1[2]);
				
				MTLthread.start();
				LVLthread.start();
				DDOthread.start();
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ServerReplica serverReplicaObj = new ServerReplica(5200,replicaPorts,serverObjSR1, dummyFifo);
			Thread thread = new Thread(serverReplicaObj);
			thread.start();
			
//			
//			serverObjSR2[0] = new ClinicServer("MTL",5002, 2525);
//			serverObjSR2[1] = new ClinicServer("LVL",5002, 3030);
//			serverObjSR2[2] = new ClinicServer("DDO",5002, 3535);
			
	}
}
