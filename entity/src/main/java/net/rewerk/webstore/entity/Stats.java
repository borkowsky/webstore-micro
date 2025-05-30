package net.rewerk.webstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import net.rewerk.webstore.entity.meta.DeletableEntityMeta;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stats")
public class Stats extends DeletableEntityMeta {
    private Long users;
    private Long orders;
    private Long reviews;
    private Double paid;
}
