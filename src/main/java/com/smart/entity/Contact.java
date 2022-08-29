 package com.smart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Contact")
public class Contact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cid;

	@Column
	private String firstName;

	@Column
	private String secondName;

	@Column
	private String work;

	@Column
	private String email;

	@Column
	private String phone;

	@Column
	private String image ;

	@Column(length = 5000)
	private String description;

	@ManyToOne
	@JsonIgnore
	private User user;

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Contact(int cid, String firstName, String secondName, String work, String email, String phone, String image,
			String description, User user) {
		super();
		this.cid = cid;
		this.firstName = firstName;
		this.secondName = secondName;
		this.work = work;
		this.email = email;
		this.phone = phone;
		this.image = image;
		this.description = description;
		this.user = user;
	}

	@Override
	public String toString() {
		return "Contact [cid=" + cid + ", firstName=" + firstName + ", secondName=" + secondName + ", work=" + work
				+ ", email=" + email + ", phone=" + phone + ", image=" + image + ", description=" + description
				+ ", user=" + user + "]";
	}


}
