package com.distributed.miraeasset.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "RequestEkyc")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EKYC {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_ekyc_id")
    private int requestEkycId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    private UserProfile customerId;

    @ManyToOne
    @JoinColumn(name = "staff_id", referencedColumnName = "staff_id")
    private Staff staffId;

    @Column(name = "status")
    private String status;

    @Column(name = "review")
    private String review;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "commit_time")
    private LocalDateTime commitTime;

    @Column(name = "rowguid", columnDefinition = "uniqueidentifier")
    private UUID rowguid;
}
