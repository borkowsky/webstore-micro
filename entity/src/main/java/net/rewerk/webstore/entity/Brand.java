package net.rewerk.webstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import net.rewerk.webstore.entity.meta.DeletableEntityMeta;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "brands")
public class Brand extends DeletableEntityMeta {
    private String name;
    private String image;
}
