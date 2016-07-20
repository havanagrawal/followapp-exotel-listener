package com.followapp;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CallDetails {

	private String phoneNumber;
	private URL guardianNameAudio;
	private URL childNameAudio;
	private URL vaccineNameAudio;
	private URL dateForVaccineAudio;

	public CallDetails() {
	}

	public CallDetails(String phoneNumber, String guardianNameAudioUrl, String childNameAudioUrl,
			String vaccineNameAudioUrl, String dateForVaccineAudioUrl) throws MalformedURLException {
		super();
		this.phoneNumber = phoneNumber;
		this.guardianNameAudio = new URL(guardianNameAudioUrl);
		this.childNameAudio = new URL(childNameAudioUrl);
		this.vaccineNameAudio = new URL(vaccineNameAudioUrl);
		this.dateForVaccineAudio = new URL(dateForVaccineAudioUrl);
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public URL getGuardianNameAudio() {
		return guardianNameAudio;
	}

	public void setGuardianNameAudio(String guardianNameAudioUrl) throws MalformedURLException {
		this.guardianNameAudio = new URL(guardianNameAudioUrl);
	}

	public URL getChildNameAudio() {
		return childNameAudio;
	}

	public void setChildNameAudio(URL childNameAudio) {
		this.childNameAudio = childNameAudio;
	}

	public URL getVaccineNameAudio() {
		return vaccineNameAudio;
	}

	public void setVaccineNameAudio(String vaccineNameAudioUrl) throws MalformedURLException {
		this.vaccineNameAudio = new URL(vaccineNameAudioUrl);
	}

	public URL getDateForVaccineAudio() {
		return dateForVaccineAudio;
	}

	public void setDateForVaccineAudio(String dateForVaccineAudioUrl) throws MalformedURLException {
		this.dateForVaccineAudio = new URL(dateForVaccineAudioUrl);
	}
}
