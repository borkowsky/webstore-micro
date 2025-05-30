package net.rewerk.webstore.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rewerk.webstore.entity.meta.DeletableEntityMeta;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "events")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event extends DeletableEntityMeta {
    private UUID userId;
    private String text;
}
