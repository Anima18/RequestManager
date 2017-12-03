package com.ut.requestmanagerdemo.entity;

public class Address {

	private String address;
	private Street street;

	public Address(String address, Street street) {
		this.address = address;
		this.street = street;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Street getStreet() {
		return street;
	}

	public void setStreet(Street street) {
		this.street = street;
	}
	
	
}
