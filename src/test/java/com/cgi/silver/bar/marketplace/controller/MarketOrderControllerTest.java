package com.cgi.silver.bar.marketplace.controller;


import com.cgi.silver.bar.marketplace.model.MarketOrder;
import com.cgi.silver.bar.marketplace.model.OrderSummary;
import com.cgi.silver.bar.marketplace.repository.MarketOrderRepository;
import com.cgi.silver.bar.marketplace.service.MarketOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;

import static com.cgi.silver.bar.marketplace.service.MarketOrderService.BUY;
import static com.cgi.silver.bar.marketplace.service.MarketOrderService.SELL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class MarketOrderControllerTest {

    @Autowired
   private  Jackson2ObjectMapperBuilder mapperBuilder;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MarketOrderService marketOrderService;
    @Autowired
    private MarketOrderRepository marketOrderRepository;


    @AfterEach
    void setupTests() {
        marketOrderRepository.deleteAll();
    }


    @Test
    public void testGetOrderSummary() throws Exception {
        OrderSummary orderSummary = new OrderSummary();
        ObjectMapper objectMapper = mapperBuilder.build();


        MvcResult mvcResult = this.mockMvc.perform(get("/order/summary")).andDo(print()).andExpect(status().isOk()).andReturn();
        String actualResponseBody = mvcResult.getResponse().getContentAsString();
        assertThat(actualResponseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(orderSummary));
    }


    @Test
    public void testRegisterOrder() throws Exception {
        ObjectMapper objectMapper = mapperBuilder.build();
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/order/register")
                .content(objectMapper.writeValueAsString(new MarketOrder(200d, SELL,new BigDecimal("20"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/register")
                .content(objectMapper.writeValueAsString(new MarketOrder(100d, SELL, new BigDecimal("20"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        mockMvc.perform(MockMvcRequestBuilders.post("/order/register")
                .content(objectMapper.writeValueAsString(new MarketOrder(1000d, BUY, new BigDecimal("100"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/order/register")
                .content(objectMapper.writeValueAsString(new MarketOrder(3000d, BUY, new BigDecimal("100"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        mockMvc.perform(MockMvcRequestBuilders.post("/order/register")
                .content(objectMapper.writeValueAsString(new MarketOrder(2000d, BUY, new BigDecimal("20"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        OrderSummary orderSummary = marketOrderService.getAllOrdersByStatus();
        assertThat(orderSummary.getSellOrder()).isNotEmpty();
        assertThat(orderSummary.getSellOrder().get(20d)).isEqualTo(300d);

        assertThat(orderSummary.getBuyOrder().get(100d)).isEqualTo(4000d);
        assertThat(orderSummary.getBuyOrder().get(20d)).isEqualTo(2000d);
    }


    @Test
    public void testOrderCancellation() throws Exception {
        ObjectMapper objectMapper = mapperBuilder.build();
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/order/register")
                .content(objectMapper.writeValueAsString(new MarketOrder(1000d, BUY, new BigDecimal("100"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated()).andReturn();

        MarketOrder marketOrder = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MarketOrder.class);

        mockMvc.perform(MockMvcRequestBuilders.put("/order/cancel/" + marketOrder.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        mockMvc.perform(MockMvcRequestBuilders.post("/order/register")
                .content(objectMapper.writeValueAsString(new MarketOrder(2000d, BUY, new BigDecimal("100"))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        OrderSummary orderSummary = marketOrderService.getAllOrdersByStatus();
        assertThat(orderSummary.getBuyOrder().get(100d)).isEqualTo(2000d);
    }

    @Test
    public void testInvalidOrderCancellation() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/order/cancel/" + 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();

    }
}