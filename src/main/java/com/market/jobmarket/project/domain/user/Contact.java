package com.market.jobmarket.project.domain.user;

public class Contact {

	private Address billingAddress;

	private Address mailingAddress;

	private Phone mobilePhone;

	private Phone workPhone;

	public Address getBillingAddress() {
		return billingAddress;
	}

	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}

	public Address getMailingAddress() {
		return mailingAddress;
	}

	public void setMailingAddress(Address mailingAddress) {
		this.mailingAddress = mailingAddress;
	}

	public Phone getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(Phone mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Phone getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(Phone workPhone) {
		this.workPhone = workPhone;
	}

	class Phone {

		private String phoneNumber;

		private String extension;

		private String countryCode;

		public String getPhoneNumber() {
			return phoneNumber;
		}

		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		public String getExtension() {
			return extension;
		}

		public void setExtension(String extension) {
			this.extension = extension;
		}

		public String getCountryCode() {
			return countryCode;
		}

		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}
	}

	class Address {

		private String primaryStreet;

		private String secondaryStreet;

		private String houseNum;

		private String city;

		private String state;

		private String country;

		private String zipCode;

		public String getPrimaryStreet() {
			return primaryStreet;
		}

		public void setPrimaryStreet(String primaryStreet) {
			this.primaryStreet = primaryStreet;
		}

		public String getSecondaryStreet() {
			return secondaryStreet;
		}

		public void setSecondaryStreet(String secondaryStreet) {
			this.secondaryStreet = secondaryStreet;
		}

		public String getHouseNum() {
			return houseNum;
		}

		public void setHouseNum(String houseNum) {
			this.houseNum = houseNum;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getZipCode() {
			return zipCode;
		}

		public void setZipCode(String zipCode) {
			this.zipCode = zipCode;
		}
	}

}
