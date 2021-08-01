package com.cgi.silver.bar.marketplace.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class OrderSummary {
    private Map<BigDecimal, Double> buyOrder;
    private Map<BigDecimal, Double> sellOrder;
}
