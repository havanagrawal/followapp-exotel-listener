package com.followapp;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CallDetails {

	private String phoneNumber;
	private String guardianName;
	private String childName;
	private String vaccineName;
	private String dateForVaccine;
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getGuardianName() {
		return guardianName;
	}
	public void setGuardianName(String guardianName) {
		this.guardianName = guardianName;
	}
	public String getChildName() {
		return childName;
	}
	public void setChildName(String childName) {
		this.childName = childName;
	}
	public String getVaccineName() {
		return vaccineName;
	}
	public void setVaccineName(String vaccineName) {
		this.vaccineName = vaccineName;
	}
	public String getDateForVaccine() {
		return dateForVaccine;
	}
	public void setDateForVaccine(String dateForVaccine) {
		this.dateForVaccine = dateForVaccine;
	}
}
