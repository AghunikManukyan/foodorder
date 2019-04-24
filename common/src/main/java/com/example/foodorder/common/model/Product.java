package com.example.foodorder.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private double price;

    @Column
    private Date date;

    @ManyToOne
    private Menu menu;

    @ManyToOne
    private Subcategory subcategory;

    @Column(name = "pic_url")
    private String picUrl;
}
