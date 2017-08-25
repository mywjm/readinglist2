package com.example.readinglist2;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReaderRepository extends MongoRepository<Reader, String> {
}
