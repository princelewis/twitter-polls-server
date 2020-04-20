package com.twitter.polls.service;

import com.twitter.polls.exception.BadRequestException;
import com.twitter.polls.model.ChoiceVoteCount;
import com.twitter.polls.model.Poll;
import com.twitter.polls.model.User;
import com.twitter.polls.model.Vote;
import com.twitter.polls.payload.PagedResponse;
import com.twitter.polls.payload.PollResponse;
import com.twitter.polls.repository.PollRepository;
import com.twitter.polls.repository.UserRepository;
import com.twitter.polls.repository.VoteRepository;
import com.twitter.polls.security.UserPrincipal;
import com.twitter.polls.util.AppConstants;
import com.twitter.polls.util.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PollService {
    @Autowired
    PollRepository pollRepository;

    @Autowired
    VoteRepository voteRepository;

    @Autowired
    UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(PollService.class);

    public PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size){
        validatePageNumberAndSize(page, size);


        //Retrieve Polls
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC,"createdAt");

        Page<Poll> polls = pollRepository.findAll(pageable);

        if(polls.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), polls.getNumber(),
                    polls.getSize(),polls.getTotalElements(), polls.getTotalPages(), polls.isLast() );
        }

        //Map polls to PollResponses containing vote counts and poll creator details
        List<Long> pollIds = polls.map(Poll::getId).getContent();
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);

        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);

        Map<Long, User> creatorMap = getPollCreatorMap(polls.getContent());

        List<PollResponse> pollResponseList = polls
                .map(poll -> ModelMapper.mapPollToPollResponse(poll,
                choiceVoteCountMap,
                creatorMap.get(poll.getCreatedBy()),
                        pollUserVoteMap == null ? null : pollUserVoteMap
                                .getOrDefault(poll.getId(), null))).getContent();

        return new PagedResponse<>(pollResponseList, polls.getNumber(), polls.getSize(),
                polls.getTotalElements(), polls.getTotalPages(), polls.isLast());
    }

    private Map<Long, User> getPollCreatorMap(List<Poll> polls){
        //Get poll Creator details of the given list of polls

        List<Long> creatorIds = polls.stream()
                .map(Poll::getCreatedBy)
                .distinct()
                .collect(Collectors.toList());

        List<User> creators = userRepository.findByIdIn(creatorIds);
        Map<Long, User> creatorMap = creators.stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return creatorMap;
    }


    private Map<Long, Long> getPollUserVoteMap(UserPrincipal currentUser, List<Long> pollIds){
        //Retrieve Votes done by the logged in user to the given pollIds

        Map<Long, Long> pollUserVoteMap = null;
        if(currentUser != null){
            List<Vote> userVotes = voteRepository.findByUserIdAndPollIdIn(currentUser.getId(), pollIds);

            pollUserVoteMap = userVotes.stream()
                    .collect(Collectors.toMap(vote -> vote.getPoll().getId(), vote -> vote.getChoice().getId()));
        }
        return pollUserVoteMap;
    }


    private Map<Long, Long> getChoiceVoteCountMap(List<Long> pollIds){

        //Retrieve Vote Counts of every Choice belonging to the given pollIds
        List<ChoiceVoteCount> votes = voteRepository.countByPollIdInGroupByChoiceId(pollIds);

        //Map choice Id to total vote count corresponding to the choice ID
        Map<Long, Long> choiceVotesMap = votes.stream().collect(Collectors.toMap(ChoiceVoteCount::getChoiceId,
                ChoiceVoteCount::getVoteCount));
        return choiceVotesMap;
    }

    private void validatePageNumberAndSize(int page, int size){
        if(page < 0){
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE){
            throw new BadRequestException("Page size must not be greater than" + AppConstants.MAX_PAGE_SIZE);
        }
    }
    
}
