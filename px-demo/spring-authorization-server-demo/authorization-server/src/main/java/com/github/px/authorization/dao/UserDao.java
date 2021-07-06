package com.github.px.authorization.dao;

import com.github.px.authorization.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, String> {
	User findByUsername(String username);
}
