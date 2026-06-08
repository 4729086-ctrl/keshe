package com.shanxi.shopping.mapper;

import com.shanxi.shopping.model.entity.Seller;
import org.apache.ibatis.annotations.*;

/**
 * 卖家 Mapper
 */
@Mapper
public interface SellerMapper {

    @Select("SELECT * FROM sellers WHERE id = #{id}")
    Seller findById(Long id);

    @Select("SELECT * FROM sellers WHERE username = #{username}")
    Seller findByUsername(String username);

    @Select("SELECT * FROM sellers WHERE phone = #{phone}")
    Seller findByPhone(String phone);

    @Select("SELECT * FROM sellers WHERE email = #{email}")
    Seller findByEmail(String email);

    @Insert("INSERT INTO sellers (username, password, shop_name, phone, email, description, avatar, status, created_at) " +
            "VALUES (#{username}, #{password}, #{shopName}, #{phone}, #{email}, #{description}, #{avatar}, 1, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Seller seller);

    @Update("UPDATE sellers SET shop_name=#{shopName}, phone=#{phone}, email=#{email}, description=#{description}, avatar=#{avatar} WHERE id=#{id}")
    int update(Seller seller);

    @Update("UPDATE sellers SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE sellers SET last_login_at = NOW() WHERE id = #{id}")
    int updateLoginTime(Long id);
}
