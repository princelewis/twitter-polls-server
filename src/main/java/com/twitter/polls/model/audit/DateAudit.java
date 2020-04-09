package com.twitter.polls.model.audit;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass // This designates a class whose mapping
//information is applied to the entities that inherits from it
@Data
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class) //This is used to specify
//listener classes which will listen for the event of the entities through some annotated methods
public class DateAudit {

//    @CreationTimestamp
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

//    @UpdateTimestamp
    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
}
