package com.shanxi.shopping.mapper;

import com.shanxi.shopping.model.entity.Order;
import com.shanxi.shopping.model.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper {

    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order findById(Long id);

    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    Order findByOrderNo(String orderNo);

    /** 买家订单分页列表 */
    List<OrderVO> findUserOrders(@Param("userId") Long userId,
                                  @Param("status") Integer status,
                                  @Param("offset") int offset,
                                  @Param("limit") int limit);

    long countUserOrders(@Param("userId") Long userId, @Param("status") Integer status);

    /** 卖家订单分页列表 */
    List<OrderVO> findSellerOrders(@Param("sellerId") Long sellerId,
                                    @Param("status") Integer status,
                                    @Param("offset") int offset,
                                    @Param("limit") int limit);

    long countSellerOrders(@Param("sellerId") Long sellerId, @Param("status") Integer status);

    /** 订单详情 */
    OrderVO findOrderDetail(Long orderId);

    @Insert("INSERT INTO orders (order_no, user_id, seller_id, total_amount, status, " +
            "receiver_name, receiver_phone, receiver_address, created_at) " +
            "VALUES (#{orderNo}, #{userId}, #{sellerId}, #{totalAmount}, #{status}, " +
            "#{receiverName}, #{receiverPhone}, #{receiverAddress}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    @Update("UPDATE orders SET status = #{status} WHERE id = #{id} AND status = #{fromStatus}")
    int updateStatus(@Param("id") Long id, @Param("fromStatus") int fromStatus, @Param("status") int status);

    @Update("UPDATE orders SET logistics_company=#{logisticsCompany}, logistics_no=#{logisticsNo}, " +
            "shipped_at=NOW(), status=#{status} WHERE id=#{id} AND status=#{fromStatus}")
    int ship(@Param("id") Long id, @Param("fromStatus") int fromStatus,
             @Param("status") int status,
             @Param("logisticsCompany") String logisticsCompany,
             @Param("logisticsNo") String logisticsNo);

    @Update("UPDATE orders SET received_at=NOW(), status=#{status} WHERE id=#{id} AND status=#{fromStatus}")
    int markReceived(@Param("id") Long id, @Param("fromStatus") int fromStatus, @Param("status") int status);
}
