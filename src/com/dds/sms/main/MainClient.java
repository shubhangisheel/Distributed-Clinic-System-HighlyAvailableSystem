package com.dds.sms.main;

import java.util.Properties;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import com.dds.sms.client.ManagerClient;

public class MainClient {

	public static void main(String[] args) {
		ORB orb;
		NamingContextExt ncRef = null;
		try{
			Properties objPP = System.getProperties();
			// create and initialize the ORB
			orb = ORB.init(args, objPP);

			// get the root naming context
			org.omg.CORBA.Object objRef = 
					orb.resolve_initial_references("NameService");
			// Use NamingContextExt instead of NamingContext. This is 
			// part of the Interoperable naming Service.  
			ncRef = NamingContextExtHelper.narrow(objRef);


		} catch (Exception e) {
			System.out.println("ERROR : " + e) ;
			e.printStackTrace(System.out);
		}
	
		/*Creating Manager Client object threads to call its getMenu()*/
		ManagerClient a = new ManagerClient(ncRef);

		for(int i = 1; i<10; i++){
			Thread clientThread = new Thread(a);
			clientThread.start();
		}
	}
}
