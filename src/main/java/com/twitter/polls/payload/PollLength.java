package com.twitter.polls.payload;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class PollLength {

    @NotNull
    @Max(7)
    private Integer days;

    @NotNull
    @Max(23)
    private Integer hours;
}
