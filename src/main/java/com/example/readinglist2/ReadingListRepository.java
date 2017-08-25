package com.example.readinglist2;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReadingListRepository extends MongoRepository<Book, String> {
    List<Book> findByReader(String reader);
}
