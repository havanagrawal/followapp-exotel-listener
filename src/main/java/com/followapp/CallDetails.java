package com.followapp;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class that represents the JSON object equivalent
 * of the parameters that must be sent on the "call" endpoint
 * 
 * TODO: Add verification of parameters
 */
@XmlRootElement
public class CallDetails {

    private String phoneNumber;
    private String guardianName;
    private String childName;
    private String vaccineName;
    private String dateForVaccine;		// Must be of the form dd mon YYYY

    private String vaccineDay;
    private String vaccineMonth;
    private String vaccineYear;
    
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
	setVaccineDayComponents(dateForVaccine);
    }
    
    private void setVaccineDayComponents(String dateForVaccine) {
	String[] dateComponents = dateForVaccine.split(" ");
	this.vaccineDay = dateComponents[0];
	this.vaccineMonth = dateComponents[1];
	this.vaccineYear = dateComponents[2];
    }
    
    public String getVaccineDay() {
        return vaccineDay;
    }

    public String getVaccineMonth() {
        return vaccineMonth;
    }

    public String getVaccineYear() {
        return vaccineYear;
    }
}
