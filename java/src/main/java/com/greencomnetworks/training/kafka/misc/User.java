package com.greencomnetworks.training.kafka.misc;

import java.util.Date;

public class User {

	private String firstName;
	private String lastname;
	private String placeOfBirth;
	
	
	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	public String getLastname() {
		return lastname;
	}


	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	

	public String getPlaceOfBirth() {
		return placeOfBirth;
	}


	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}


	public String toString() {
		return firstName + " " + lastname;
	}
}
