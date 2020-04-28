package com.twitter.polls.model;


import com.twitter.polls.model.audit.DateAudit;
import lombok.Data;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

@Entity
@Table(
        name = "votes", uniqueConstraints = {
                @UniqueConstraint(columnNames = {
                        "poll_id",
                        "user_id"
                })
}
)
@Data
public class Vote extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//The user who voted
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "choice_id", nullable = false)
    private Choice choice;
}
