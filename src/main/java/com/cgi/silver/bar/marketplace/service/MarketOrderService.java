package com.cgi.silver.bar.marketplace.service;

import com.cgi.silver.bar.marketplace.model.MarketOrder;
import com.cgi.silver.bar.marketplace.model.OrderSummary;
import com.cgi.silver.bar.marketplace.repository.MarketOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MarketOrderService {

    public static final String BUY = "BUY";
    public static final String SELL = "SELL";
    public static final String CANCEL = "CANCEL";
    public static final String ACTIVE = "ACTIVE";


    private MarketOrderRepository marketOrderRepository;

    public MarketOrderService(MarketOrderRepository marketOrderRepository) {
        this.marketOrderRepository = marketOrderRepository;
    }

    public MarketOrder registerOrder(MarketOrder orderDetails) {
        orderDetails.setOrderStatus(ACTIVE);
        return marketOrderRepository.save(orderDetails);
    }

    @Transactional
    public boolean cancelOrder(int orderId) {
        boolean orderCancelled = false;
        Optional<MarketOrder> orderFound = marketOrderRepository.findById(orderId);
        log.info(orderFound.toString());
        if (orderFound.isPresent()) {
            orderCancelled = true;
            int updateStatus = marketOrderRepository.cancelOrderById(orderId, CANCEL, LocalDate.now());
            log.info("Order Updated with cancel status " + updateStatus);
        }
        return orderCancelled;
    }

    public OrderSummary getAllOrdersByStatus() {
        List<MarketOrder> activeOrderList = marketOrderRepository.findAllByOrderStatus(ACTIVE);

        OrderSummary orderSummary = new OrderSummary();
        if (activeOrderList.isEmpty())
            return orderSummary;

        List<MarketOrder> orderStatusSellList = activeOrderList.stream().filter(order -> SELL.equalsIgnoreCase(order.getType())).collect(Collectors.toList());
        List<MarketOrder> orderStatusBuyList = activeOrderList.stream().filter(order -> BUY.equalsIgnoreCase(order.getType())).collect(Collectors.toList());

        Map<String, Map<Double, Double>> groupSellOrderByPrice = orderStatusSellList
                .stream().sorted(Comparator.comparingDouble(MarketOrder::getPricePerKg))
                .collect(Collectors.groupingBy(MarketOrder::getType, Collectors.groupingBy(MarketOrder::getPricePerKg, Collectors.summingDouble(MarketOrder::getQuantity))));

        if (groupSellOrderByPrice.containsKey(SELL)) {
            orderSummary.setSellOrder(new TreeMap(groupSellOrderByPrice.get(SELL)));
        }

        Map<String, Map<Double, Double>> groupBuyOrderByPrice = orderStatusBuyList
                .stream()
                .collect(Collectors.groupingBy(MarketOrder::getType, Collectors.groupingBy(MarketOrder::getPricePerKg, Collectors.summingDouble(MarketOrder::getQuantity))));
        if (groupBuyOrderByPrice.containsKey(BUY)) {
            Map<Double, Double> sortedBuyMap = new TreeMap(Collections.reverseOrder());
            sortedBuyMap.putAll(groupBuyOrderByPrice.get(BUY));
            orderSummary.setBuyOrder(sortedBuyMap);
        }

        return orderSummary;
    }

    public List<MarketOrder> getAllOrdersByDate(LocalDate date) {

        return marketOrderRepository.findAllOrderByCreatedOn(date);

    }
}
