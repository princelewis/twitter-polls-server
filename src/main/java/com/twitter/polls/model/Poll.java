package com.twitter.polls.model;

import com.twitter.polls.model.audit.UserDateAudit;
import lombok.Data;
import lombok.Generated;
import lombok.NonNull;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "polls")
public class Poll extends UserDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String question;

    //
    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "poll",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Size(min = 2, max = 6)

    //This annotation helps to optimize the Hibernate generated
    // select statement,so that it can be as efficient as possible.
    //For @Fetch(FetchMode.SELECT) this means that the property should
    //be fetched lazily
    @Fetch(FetchMode.SELECT)
    @BatchSize(size=30)
    private List<Choice> choices = new ArrayList<>();

    @NonNull
    private Instant expirationDateTime;

    public void addChoice (Choice choice){
        choices.add(choice);
        choice.setPoll(this);
    }

    public void removeChoice(Choice choice){
        choices.remove(choice);
        choice.setPoll(null);
    }

    public Poll (){

    }
}
