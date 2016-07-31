package com.dds.sms.datatypes;

public class DoctorRecord extends Record {

	private String firstName;
	private String lastName;
	private String address;
	private String phone;
	private String specialization;
	private String location;

	/*Constructor*/
	public DoctorRecord(String fName, String lName, String add, String phn, String spclztn, String loc, String recordID){
		super("DR", recordID);
		firstName = fName;
		lastName = lName;
		address = add;
		phone = phn;
		specialization = spclztn;
		location = loc;	
	}

	/*Getter and Setters for Class data members*/
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String newValue) {
		this.phone = newValue;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}



}
