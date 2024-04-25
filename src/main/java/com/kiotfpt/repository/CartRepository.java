package com.kiotfpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kiotfpt.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {

}
