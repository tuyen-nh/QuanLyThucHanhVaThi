package com.example.demo.service;

import com.example.demo.Model.User;
import com.example.demo.Model.UserPrinciple;
import com.example.demo.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Mydetailservice implements UserDetailsService {

    @Autowired
    UserRepository repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repo.findByUserName(username);
       if (!user.isPresent()){
           System.out.println("user not found");
           throw new UsernameNotFoundException("user not found");

       }
       User user1 =user.get();
        return new UserPrinciple(user1) ;
    }
}
