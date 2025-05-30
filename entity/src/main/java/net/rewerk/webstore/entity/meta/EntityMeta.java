package net.rewerk.webstore.entity.meta;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SoftDelete
public class EntityMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer id;
    @CreatedDate
    @Column(updatable = false)
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
}
