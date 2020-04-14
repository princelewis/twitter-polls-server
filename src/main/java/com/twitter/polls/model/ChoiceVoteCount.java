package com.twitter.polls.model;

import lombok.Data;

@Data
public class ChoiceVoteCount {

    private Long choiceId;
    private Long voteCount;

    public ChoiceVoteCount(Long choiceId, Long voteCount) {
        this.choiceId = choiceId;
        this.voteCount = voteCount;
    }
}
