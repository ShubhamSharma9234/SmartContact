package com.smart.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="Orders")
public class MyOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long myorderId ;
	
	@Column
	private String orderId ;
	
	@Column 
	private String amount ;
	
	@Column
	private String receipt ;
	
	@Column
	private String status ;
	
	@ManyToOne
	private User user ;
	
	@Column
	private String paymentId ;

	public long getMyorderId() {
		return myorderId;
	}

	public void setMyorderId(long myorderId) {
		this.myorderId = myorderId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getReceipt() {
		return receipt;
	}

	public void setReceipt(String receipt) {
		this.receipt = receipt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public MyOrder(long myorderId, String orderId, String amount, String receipt, String status, User user,
			String paymentId) {
		super();
		this.myorderId = myorderId;
		this.orderId = orderId;
		this.amount = amount;
		this.receipt = receipt;
		this.status = status;
		this.user = user;
		this.paymentId = paymentId;
	}

	public MyOrder() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}
