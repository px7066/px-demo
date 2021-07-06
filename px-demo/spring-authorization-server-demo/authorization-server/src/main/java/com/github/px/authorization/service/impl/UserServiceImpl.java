package com.github.px.authorization.service.impl;

import com.github.px.authorization.dao.UserDao;
import com.github.px.authorization.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@Primary
public class UserServiceImpl implements UserService, UserDetailsService {
	@Autowired
	private UserDao userDao;

	private static PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public static void main(String[] args) {
		System.out.println(passwordEncoder.encode("123456"));
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userDao.findByUsername(username);
	}
}
