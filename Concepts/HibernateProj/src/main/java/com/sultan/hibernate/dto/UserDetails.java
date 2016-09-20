package com.sultan.hibernate.dto;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity //(name="User_Details") name of the entity itself
@Table (name="User_Details")	//table name only
public class UserDetails {
	@Id @GeneratedValue(strategy=GenerationType.AUTO) //to auto generate sequence 
	@Column (name="User_ID")
	private int userID;
	@Column (name="User_Name")
	@Basic 		//Default value for the column
	private String userName;
	@Temporal (TemporalType.DATE) //To push on Hibernate to add date only not time stamp
	private Date joinedDate;
	@Transient 	//the Address is hidden for the hibernate (will not be added)
	private String Address;
	@Lob    //Larg object
	private String Description;
	
	public Date getJoinedDate() {
		return joinedDate;
	}
	public void setJoinedDate(Date joinedDate) {
		this.joinedDate = joinedDate;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName + " from Getter";
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

}
