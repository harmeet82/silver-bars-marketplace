package com.cgi.silver.bar.marketplace.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
@Data
@Entity
@Table(name = "Order_Details")
public final class MarketOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;
    @Column(name = "User_Id")
    private long userId;
    @Column(name = "quantity")
    private Double quantity;
    @Column(name = "pricePerKg")
    private Double pricePerKg;
    @Column(name = "order_Type")
    private String type;
    @Column(name = "order_status")
    private String orderStatus;
    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdOn = LocalDate.now();
    @Column(name = "created_by")
    private String createdBy = "System";
    @Column(name = "updated_date")
    private LocalDate updatedOn;
    @Column(name = "updated_By")
    private String updatedBy;

    public MarketOrder(Double quantity, String type, Double pricePerKg) {
        this.quantity = quantity;
        this.pricePerKg = pricePerKg;
        this.type = type;
    }


}
