package com.cos.security1.repository;

import com.cos.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//CRUD함수를 JpaRepository를 들고 있음
// @Repository라는 어노테이션이 없어도
public interface UserRepository extends JpaRepository<User, Integer> {
    // findBy는 규칙, Username 부분은 문법
    // select * from user where username=?
    public User findByUsername(String username);
}
