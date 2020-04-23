package com.twitter.polls.service;

import com.twitter.polls.exception.BadRequestException;
import com.twitter.polls.exception.ResourceNotFoundException;
import com.twitter.polls.model.*;
import com.twitter.polls.payload.PagedResponse;
import com.twitter.polls.payload.PollRequest;
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

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
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

    //This is for getting all the polls the authenticated user has interfaced - interacted - with
    public PagedResponse<PollResponse> getAllPolls(UserPrincipal currentUser, int page, int size){
        validatePageNumberAndSize(page, size);


        //Retrieve Polls
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC,"createdAt");

        Page<Poll> polls = pollRepository.findAll(pageable);

        if(polls.getNumberOfElements() == 0){
            return new PagedResponse<>(Collections.emptyList(), polls.getNumber(),
                    polls.getSize(),polls.getTotalElements(), polls.getTotalPages(), polls.isLast() );
        }

        //Map polls to PollResponses containing vote counts and poll creator's details
        //Basically what is going on here is that we need to convert the collection of polls which is in page data type
        //to list of pollIds. The way to convert a collection in Page data type is to use the .getContent method on the
        //paged collection.
        List<Long> pollIds = polls.map(Poll::getId).getContent();

        //This is to get a map of the choiceIds and their corresponding vote counts for all pollIds
        Map<Long, Long> choiceVoteCountMap = getChoiceVoteCountMap(pollIds);

        //To get a map of all the pollIds and the corresponding vote choice the authenticated user made
        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, pollIds);

        //To get the userId (creatorId) and the corresponding User details as stored in the database
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


    public Poll createPoll(PollRequest pollRequest){
        Poll poll = new Poll();

        poll.setQuestion(pollRequest.getQuestion());

        pollRequest.getChoices().forEach(choiceRequest -> {
            poll.addChoice(new Choice(choiceRequest.getText()));
        });

        Instant now = Instant.now();

        Instant expirationDateTime = now.plus(Duration.ofDays(pollRequest.getPollLength().getDays()))
                .plus(Duration.ofHours(pollRequest.getPollLength().getHours()));

        poll.setExpirationDateTime(expirationDateTime);

        return pollRepository.save(poll);
    }


    public PollResponse getPollById(Long pollId, UserPrincipal currentUser){

        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new ResourceNotFoundException("Poll","id", pollId));


        //Trial section - It could be written in this way.
//        Map<Long, Long> pollUserVoteMap = getPollUserVoteMap(currentUser, Collections.singletonList(pollId));
//        Map<Long, Long> choiceVoteMap = getChoiceVoteCountMap(Collections.singletonList(pollId));

//        Map<Long, User> pollCreatorMap = getPollCreatorMap(Collections.singletonList(poll));
//        User pollCreator = poll.

        //Retrieve Vote Counts of every choice belonging to the current poll

        List<ChoiceVoteCount> votes = voteRepository.countByPollIdGroupByChoiceId(pollId);

        Map<Long, Long> choiceVotesMap = votes.stream()
                .collect(Collectors.toMap(ChoiceVoteCount::getChoiceId, ChoiceVoteCount::getVoteCount));

        //Retrieve poll creator details
        User creator = userRepository.findById(poll.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User","Id", poll.getCreatedBy()));

        //Retrieve vote done by logged in user
        Vote userVote = null;

        if(currentUser != null){
            userVote = voteRepository.findByUserIdAndPollId(currentUser.getId(), pollId);
        }

        return ModelMapper.mapPollToPollResponse(poll, choiceVotesMap,
                creator, userVote != null ? userVote.getChoice().getId() : null);
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
