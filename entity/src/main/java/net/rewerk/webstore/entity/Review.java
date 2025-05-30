package net.rewerk.webstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import net.rewerk.webstore.entity.meta.EntityMeta;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reviews")
public class Review extends EntityMeta {
    private Integer rating;
    private String text;
    private UUID userId;
    private Integer productId;
    private List<String> images;
    private Integer orderId;
}
