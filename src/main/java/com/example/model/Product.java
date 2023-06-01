package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name= "products")
public class Product {

	@Id
	private Long productId;

	private String productName;

	private Long quantity;
	
	
	

	public Product() {
		super();
	}

	public Product(String productName, Long quantity) {
		super();
		this.productName = productName;
		this.quantity = quantity;
	}
	
	
	public Product(Long productId, String productName, Long quantity) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.quantity = quantity;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	@Override
	public String toString() {
		return "Product{" +
				"productId=" + productId +
				", productName='" + productName + '\'' +
				", quantity=" + quantity +
				'}';
	}
}
