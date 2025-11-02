package com.gustavo.springsecurity.controller;

import com.gustavo.springsecurity.dto.TweetDTO;
import com.gustavo.springsecurity.entities.Tweet;
import com.gustavo.springsecurity.entities.User;
import com.gustavo.springsecurity.repository.TweetRepository;
import com.gustavo.springsecurity.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "/tweets")
public class TweetController {

    @Autowired
    private final TweetRepository tweetRepository;

    @Autowired
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Void> insert(@RequestBody TweetDTO tweetDTO, JwtAuthenticationToken token) {
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));

        Tweet tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(tweetDTO.content());

        tweetRepository.save(tweet);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tweet.getTweetId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

}
