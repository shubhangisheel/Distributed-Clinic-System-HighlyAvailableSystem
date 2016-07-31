package com.dds.sms.datatypes;

public class Record {
	private String type;
	private String recordID;

	/*Constructor*/
	public Record(String type,  String recordID){
		this.type = type;
		this.recordID = recordID;
	}

	/*Getters and Setters*/
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRecordID() {
		return recordID;
	}

	public void setRecordID(String recordID) {
		this.recordID = recordID;
	}

}

