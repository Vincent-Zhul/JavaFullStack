package com.example.javafullstack;

import com.example.javafullstack.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

}

