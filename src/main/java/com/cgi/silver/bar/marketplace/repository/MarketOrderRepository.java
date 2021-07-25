package com.cgi.silver.bar.marketplace.repository;

import com.cgi.silver.bar.marketplace.model.MarketOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MarketOrderRepository extends JpaRepository<MarketOrder, Integer> {

    @Modifying
    @Query(value = "UPDATE MarketOrder SET orderStatus=:status,updatedOn=:updatedDate WHERE id=:orderId")
    int cancelOrderById(@Param("orderId") int orderId, @Param("status") String status, @Param("updatedDate") LocalDate updatedDate);

    List<MarketOrder> findAllByOrderStatus(String orderStatus);

    List<MarketOrder> findAllOrderByCreatedOn(LocalDate date);


}