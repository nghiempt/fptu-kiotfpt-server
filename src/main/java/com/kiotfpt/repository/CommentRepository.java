package com.kiotfpt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kiotfpt.model.Account;
import com.kiotfpt.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
	
	List<Comment> findAllByAccount(Account account);
}