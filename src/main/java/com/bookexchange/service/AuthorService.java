package com.bookexchange.service;

import com.bookexchange.dto.request.AuthorRequest;
import com.bookexchange.dto.response.AuthorResponse;
import com.bookexchange.entity.Author;
import com.bookexchange.mapper.AuthorMapper;
import com.bookexchange.repository.AuthorRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthorService {

    AuthorRepository authorRepository;
    AuthorMapper authorMapper;

    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(authorMapper::toAuthorResponse)
                .collect(Collectors.toList());
    }

    public void deleteAuthor(Long authorId) {
        Optional<Author> authorOptional = authorRepository.findById(authorId);
        if (authorOptional.isPresent()) {
            authorRepository.deleteById(authorId);
            log.info("Deleted author with id: {}", authorId);
        } else {
            log.warn("Author with id {} not found", authorId);
            throw new RuntimeException("Author not found");
        }
    }

    public AuthorResponse updateAuthor(Long authorId, AuthorRequest request) {
        Author author = authorRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));

        author.setName(request.getName());
        author.setImageUrl(request.getImageUrl());

        Author updatedAuthor = authorRepository.save(author);
        return authorMapper.toAuthorResponse(updatedAuthor);
    }
}
