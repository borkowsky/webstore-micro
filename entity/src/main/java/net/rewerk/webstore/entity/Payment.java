package net.rewerk.webstore.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import net.rewerk.webstore.entity.meta.EntityMeta;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment extends EntityMeta {
    public enum Status {
        CREATED,
        APPROVED,
        REJECTED
    }
    @JsonIgnore
    private UUID userId;
    private Double sum;
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Status status;
}
