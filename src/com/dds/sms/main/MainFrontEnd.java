package com.dds.sms.main;

import com.dds.sms.frontend.FrontEnd;

public class MainFrontEnd {

	public static void main(String[] args) {
		
		FrontEnd frontEndObj = new FrontEnd("MTL");
		boolean r1 = frontEndObj.createDRecord("MTL1001", "Shubhangi", "Sheel", "asd", "123", "sdf", "MTL");
		boolean r2 =frontEndObj.createNRecord("LVL1001", "Patel", "Patel", "asd", "123", "MTL");
		boolean r3 = frontEndObj.createDRecord("MTL1002", "Shubham", "Singh", "asd", "123", "sdf", "MTL");
		boolean r4 = frontEndObj.createNRecord("LVL1001", "Azaad", "Shams", "asd", "123", "MTL");
		boolean r5 = frontEndObj.editRecord("MTL1001", "DR10001", "firstName", "Anish");
		int r6 = frontEndObj.getCount("MTL1001", "DR");
		boolean r7 = frontEndObj.transferRecord("MTL10001", "DR10001", "LVL");
		if(r1){
			System.out.println("shubhangi is added");
		}
		if(r2){
			System.out.println("Parth is added");
		}
		if(r3){
			System.out.println("Shubham is added");
		}
		if(r4){
			System.out.println("Shams is added");
		}
		if(r5){
			System.out.println("record edited with new name");
		}
		if(r7){
			System.out.println("record successfully transfered to Laval");
		}
		System.out.println("Count should be 4 ..it is : "+r6);
		
	}

}
