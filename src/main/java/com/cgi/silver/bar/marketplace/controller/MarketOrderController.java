package com.cgi.silver.bar.marketplace.controller;

import com.cgi.silver.bar.marketplace.model.MarketOrder;
import com.cgi.silver.bar.marketplace.model.OrderSummary;
import com.cgi.silver.bar.marketplace.service.MarketOrderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/order")
@Slf4j
public class MarketOrderController {

    private final MarketOrderService marketOrderService;

    public MarketOrderController(MarketOrderService marketOrderService) {
        this.marketOrderService = marketOrderService;
    }

    @ApiOperation(value = "View a list of all active orders as Buy in descending order and Sell in ascending order ", response = Iterable.class)
    @GetMapping("/summary")
    public ResponseEntity<OrderSummary> getAllActiveOrderSummary() {
        OrderSummary orderSummary = marketOrderService.getAllOrdersByStatus();
        return new ResponseEntity<>(orderSummary, HttpStatus.OK);
    }

    @ApiOperation(value = "View a list of all orders for a given date in format(yyyy-MM-dd)", response = Iterable.class)

    @GetMapping("/summary/{date}")
    public ResponseEntity<List<MarketOrder>> getOrderSummaryByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {

        log.info(" in controller  getOrderSummaryByDate >>  " + date);
        List<MarketOrder> orderSummary = marketOrderService.getAllOrdersByDate(date);
        if (orderSummary != null & !orderSummary.isEmpty()) {
            return new ResponseEntity<>(orderSummary, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(orderSummary, HttpStatus.NOT_FOUND);
        }

    }

    @ApiOperation(value = "To register a new order", response = Iterable.class)
    @PostMapping("/register")
    public ResponseEntity<MarketOrder> registerOrder(@RequestBody MarketOrder orderDetails) {
        log.info(" in controller >>  " + orderDetails.toString());

        MarketOrder order = marketOrderService.registerOrder(orderDetails);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @ApiOperation(value = "to cancel a particular order by id", response = Iterable.class)
    @PutMapping("/cancel/{id}")
    public ResponseEntity<String> cancelOrder(@PathVariable("id") int id) {
        log.info(" in controller  cancelOrder >>  " + id);
        boolean cancelOrderStatus = marketOrderService.cancelOrder(id);
        log.info(" cancelOrderStatus >>> " + cancelOrderStatus);
        if (cancelOrderStatus) {
            return new ResponseEntity<>("Order Canceled", HttpStatus.OK);
        } else {
            log.info("Order not found for cancel");
            return new ResponseEntity<>("Order Not Found for Id " + id, HttpStatus.NOT_FOUND);
        }
    }


}