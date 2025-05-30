package net.rewerk.webstore.entity;

import jakarta.persistence.*;
import lombok.*;
import net.rewerk.webstore.entity.meta.EntityMeta;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order extends EntityMeta {
    public enum Status {
        CREATED,
        PAID,
        ACCEPTED,
        REJECTED,
        DELIVERY,
        DELIVERED,
        RECEIVED
    }

    private UUID userId;
    @OneToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.PERSIST
    )
    @JoinColumn(
            name = "order_id"
    )
    private List<OrdersProducts> products;
    @OneToOne(
            fetch = FetchType.EAGER,
            cascade = {
                    CascadeType.PERSIST
            }
    )
    @JoinColumn(
            name = "payment_id",
            nullable = false,
            updatable = false
    )
    private Payment payment;
    private Integer addressId;
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private Status status;
}
