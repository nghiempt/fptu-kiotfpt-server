package com.kiotfpt.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "kiotfpt_shop_category")
public class ShopCategory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shop_category_id")
	private int id;

	@ManyToOne()
	@JoinColumn(name = "shop_id")
	@JsonIgnore
	private Shop shop;

	@ManyToOne()
	@JoinColumn(name = "status_id")
	private Status status;
	
	@ManyToOne()
	@JoinColumn(name = "category_id")
	private Category category;

}
