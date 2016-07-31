package com.dds.sms.datatypes;

public class NurseRecord extends Record{

	private String firstName;
	private String lastName;
	private String designation;
	private String status_Date;
	private String status;

	/*Constructor*/
	public NurseRecord(String fName, String lName, String desig, String stat_Date, String stat, String recordID){
		super("NR", recordID);
		firstName = fName;
		lastName = lName;
		designation = desig;
		status_Date = stat_Date;
		status = stat;
	}

	/*Getters and Setters*/
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getStatus_Date() {
		return status_Date;
	}

	public void setStatus_Date(String status_Date) {
		this.status_Date = status_Date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
