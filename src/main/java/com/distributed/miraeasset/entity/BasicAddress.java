package com.distributed.miraeasset.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "BasicAddress")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer addressId;

    @Nationalized
    @Column(name = "city")
    private String city;

    @Nationalized
    @Column(name = "district")
    private String district;

    @Nationalized
    @Column(name = "ward")
    private String ward;

    @Column(name = "city_code")
    private String cityCode;

    @Column(name = "district_code")
    private String districtCode;

    @Column(name = "ward_code")
    private String wardCode;
}
