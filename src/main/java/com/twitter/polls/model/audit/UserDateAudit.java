package com.twitter.polls.model.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.Instant;

@MappedSuperclass
@Data
@Getter(AccessLevel.PROTECTED)
@Setter(AccessLevel.PROTECTED)
@JsonIgnoreProperties(
        value = {"createdBy", "updatedBy"},
        allowGetters = true
        )
public class UserDateAudit extends DateAudit {

    @CreatedBy
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Long updatedBy;

}
