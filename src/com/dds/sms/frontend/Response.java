package com.dds.sms.frontend;

import java.io.Serializable;

public class Response implements Serializable{
	
	
	private boolean doctorAdded;
	private boolean nurseAdded;
	private boolean recordEdited;
	private boolean recordTransfered;
	private int getCount;
	private String methodName;
	
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public boolean isDoctorAdded() {
		return doctorAdded;
	}
	public void setDoctorAdded(boolean doctorAdded) {
		this.doctorAdded = doctorAdded;
	}
	public boolean isNurseAdded() {
		return nurseAdded;
	}
	public void setNurseAdded(boolean nurseAdded) {
		this.nurseAdded = nurseAdded;
	}
	public boolean isRecordEdited() {
		return recordEdited;
	}
	public void setRecordEdited(boolean recordEdited) {
		this.recordEdited = recordEdited;
	}
	public boolean isRecordTransfered() {
		return recordTransfered;
	}
	public void setRecordTransfered(boolean recordTransfered) {
		this.recordTransfered = recordTransfered;
	}
	public int getGetCount() {
		return getCount;
	}
	public void setGetCount(int getCount) {
		this.getCount = getCount;
	}
	
	
	
}
