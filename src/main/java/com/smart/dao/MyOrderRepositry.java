package com.smart.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.entity.MyOrder;

public interface MyOrderRepositry extends JpaRepository<MyOrder, Long> {

	public MyOrder findByorderId(String orderId);
}
