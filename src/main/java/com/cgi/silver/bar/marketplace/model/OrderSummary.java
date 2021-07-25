package com.cgi.silver.bar.marketplace.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class OrderSummary {
    private Map<Double, Double> buyOrder;
    private Map<Double, Double> sellOrder;
}
