package com.poli.covid19.services;


import com.poli.covid19.domain.User;

import java.util.List;

public interface UserService {

    List <User> consultUser(String userName, String password);
    User setUser(User user);

}
