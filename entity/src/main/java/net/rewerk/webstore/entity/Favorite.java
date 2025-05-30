package net.rewerk.webstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import net.rewerk.webstore.entity.meta.DeletableEntityMeta;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Entity
@Table(name = "favorites")
@NoArgsConstructor
@AllArgsConstructor
public class Favorite extends DeletableEntityMeta {
    @JsonIgnore
    private UUID userId;
    private Integer productId;
}
