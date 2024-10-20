package com.distributed.miraeasset.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "CitizenIdentification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CitizenIdentification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "citizen_id")
    private Integer citizenId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private UserProfile customerId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "date_of_birth")
    private Date Dob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "front_citizen_image")
    private String frontCitizenImage;

    @Column(name = "back_citizen_image")
    private String backCitizenImage;

    @Column(name = "nationality")
    private String nationality;

    @ManyToOne
    @JoinColumn(name = "place_of_origin_id")
    private BasicAddress placeOfpOriginId;

    @ManyToOne
    @JoinColumn(name = "place_of_residence_id")
    private BasicAddress placeOfResidenceId;

    @Column(name = "issued_at")
    private String issuedAt;

    @Column(name = "issued_date")
    private Date issuedDate;

    @Column(name = "card_validity")
    private Date cardValidity;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "rowguid", updatable = false)
    private UUID rowguid;
}
