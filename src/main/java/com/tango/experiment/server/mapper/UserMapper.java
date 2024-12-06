package com.tango.experiment.server.mapper;

import com.tango.experiment.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    List<User> getAllUser();

    User getUserByUsername(@Param("username") String username);

    Integer insertUser(@Param("username") String username, @Param("password") String password, @Param("role") String role);

    Integer deleteUser(@Param("userId") Integer userId);

    Integer updateUser(@Param("userId") Integer userId, @Param("username") String username, @Param("password") String password, @Param("role") String role);

    List<User> getUserByLike(@Param("usernameLike") String usernameLike);
}
