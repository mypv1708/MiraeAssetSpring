package com.distributed.miraeasset.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "CustomerDetail")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

//    @ManyToOne
//    @JoinColumn(name = "relatives_customer_first")
//    private RelativesCustomer relativesCustomerFirst;
//
//    @ManyToOne
//    @JoinColumn(name = "relatives_customer_second")
//    private RelativesCustomer relativesCustomerSecond;

    @ManyToOne
    @JoinColumn(name = "current_address_id")
    private BasicAddress currentAddress;

    @ManyToOne
    @JoinColumn(name = "branch_id", referencedColumnName = "branch_id")
    private Branch branch;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "job")
    private String job;

    @Column(name = "income")
    private BigDecimal income;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "rowguid", columnDefinition = "uniqueidentifier")
    private UUID rowguid;
}
