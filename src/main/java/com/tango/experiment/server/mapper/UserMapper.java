package com.tango.experiment.server.mapper;

import com.tango.experiment.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    List<User> getAllUser();

    User getUserByUsername(@Param("username") String username);

    Integer insertUser(@Param("username") String username, @Param("password") String password);
}
