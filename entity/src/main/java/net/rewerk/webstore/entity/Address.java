package net.rewerk.webstore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import net.rewerk.webstore.entity.meta.DeletableEntityMeta;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address extends DeletableEntityMeta {
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String region;
    private Integer postalCode;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false, updatable = false)
    private UUID userId;
}
