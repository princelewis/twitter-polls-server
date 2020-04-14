package com.twitter.polls.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;
import java.util.List;


@Data
public class PollResponse {

    private Long id;
    private String question;
    private List<ChoiceRequest> choices;
    private UserSummary createdBy;
    private Instant creationDateTime;
    private Instant expirationDateTime;
    private Boolean isExpired;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long selectedChoice;
    private Long totalVotes;
}
