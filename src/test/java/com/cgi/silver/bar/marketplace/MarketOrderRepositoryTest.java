package com.cgi.silver.bar.marketplace;

import com.cgi.silver.bar.marketplace.model.MarketOrder;
import com.cgi.silver.bar.marketplace.repository.MarketOrderRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import static com.cgi.silver.bar.marketplace.service.MarketOrderService.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class MarketOrderRepositoryTest {

    @Autowired
    private MarketOrderRepository marketOrderRepository;

    @AfterEach
    void setupTests() {
        marketOrderRepository.deleteAll();
    }

    @Test
    void testMarketOrderRepository() throws ParseException {
        MarketOrder marketOrder = new MarketOrder(2000d, BUY, 20d);
        marketOrder.setOrderStatus(ACTIVE);

        marketOrder = marketOrderRepository.save(marketOrder);
        List<MarketOrder> marketOrders = marketOrderRepository.findAllByOrderStatus(ACTIVE);
        assertThat(marketOrders).isNotEmpty();
        assertThat(marketOrders.get(0).getQuantity()).isEqualTo(2000d);

        MarketOrder todayMarketOrder = new MarketOrder(305d, BUY, 30d);
        todayMarketOrder.setOrderStatus(ACTIVE);
        todayMarketOrder.setCreatedOn(LocalDate.now());
         marketOrderRepository.save(todayMarketOrder);

        List<MarketOrder> marketOrdersByDate = marketOrderRepository.findAllOrderByCreatedOn(LocalDate.now());
        assertThat(marketOrdersByDate).isNotEmpty();
        assertThat(marketOrdersByDate).hasSize(2);

        marketOrderRepository.cancelOrderById(marketOrder.getId(), CANCEL, LocalDate.now());
        marketOrders = marketOrderRepository.findAllByOrderStatus(ACTIVE);
        assertThat(marketOrders).hasSize(1);

    }
}