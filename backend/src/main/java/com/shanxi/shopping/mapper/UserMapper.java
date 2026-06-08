package com.shanxi.shopping.mapper;

import com.shanxi.shopping.model.entity.User;
import org.apache.ibatis.annotations.*;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM users WHERE email = #{email}")
    User findByEmail(String email);

    @Select("SELECT * FROM users WHERE phone = #{phone}")
    User findByPhone(String phone);

    @Insert("INSERT INTO users (username, password, phone, email, created_at) " +
            "VALUES (#{username}, #{password}, #{phone}, #{email}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE users SET phone = #{phone}, email = #{email}, avatar = #{avatar} WHERE id = #{id}")
    int update(User user);

    @Update("UPDATE users SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE users SET last_login_at = NOW() WHERE id = #{id}")
    int updateLoginTime(Long id);
}
