package com.gustavo.springsecurity.controller;

import com.gustavo.springsecurity.dto.FeedDTO;
import com.gustavo.springsecurity.dto.FeedItemDTO;
import com.gustavo.springsecurity.dto.TweetDTO;
import com.gustavo.springsecurity.entities.Role;
import com.gustavo.springsecurity.entities.Tweet;
import com.gustavo.springsecurity.entities.User;
import com.gustavo.springsecurity.repository.TweetRepository;
import com.gustavo.springsecurity.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
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

    @GetMapping
    public ResponseEntity<FeedDTO> findAllToFeed(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        var tweets = tweetRepository.findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp"))
                .map(tweet -> new FeedItemDTO(tweet.getTweetId(), tweet.getContent(), tweet.getUser().getUsername()));

        FeedDTO feed = new FeedDTO(tweets.stream().toList(), page, pageSize, tweets.getTotalPages(), tweets.getNumberOfElements());

        return ResponseEntity.ok().body(feed);
    }

    @PostMapping
    @Transactional
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id, JwtAuthenticationToken token) {

        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));

        Optional<Tweet> tweet = Optional.ofNullable(tweetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));

        var isAdmin = user.get()
                .getRoles()
                .stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name().toLowerCase()));

        if (isAdmin || tweet.get().getUser().getUserId().equals(UUID.fromString(token.getName()))) {
            tweetRepository.deleteById(id);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.noContent().build();
    }

}
