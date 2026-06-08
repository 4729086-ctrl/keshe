package com.shanxi.shopping.mapper;

import com.shanxi.shopping.model.entity.UserAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户收货地址 Mapper
 */
@Mapper
public interface UserAddressMapper {

    @Select("SELECT * FROM user_addresses WHERE user_id = #{userId} ORDER BY is_default DESC, created_at DESC")
    List<UserAddress> findByUserId(Long userId);

    @Select("SELECT * FROM user_addresses WHERE id = #{id}")
    UserAddress findById(Long id);

    @Insert("INSERT INTO user_addresses (user_id, receiver_name, receiver_phone, province, city, district, detail, is_default, created_at) " +
            "VALUES (#{userId}, #{receiverName}, #{receiverPhone}, #{province}, #{city}, #{district}, #{detail}, #{isDefault}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserAddress address);

    @Update("UPDATE user_addresses SET receiver_name=#{receiverName}, receiver_phone=#{receiverPhone}, " +
            "province=#{province}, city=#{city}, district=#{district}, detail=#{detail}, is_default=#{isDefault} WHERE id=#{id}")
    int update(UserAddress address);

    @Delete("DELETE FROM user_addresses WHERE id = #{id}")
    int deleteById(Long id);

    @Update("UPDATE user_addresses SET is_default = 0 WHERE user_id = #{userId}")
    int clearDefault(Long userId);

    @Update("UPDATE user_addresses SET is_default = 1 WHERE id = #{id}")
    int setDefault(Long id);
}
