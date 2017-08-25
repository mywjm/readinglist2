package com.example.readinglist2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTests {

    @Autowired
    private ReadingListRepository bookRepository;
    @Test
    public void MongoConnectionTest() {
        Book book = new Book();
        book.setIsbn("123466");
        book.setDescription("这是一本好书");
        book.setAuthor("佚名");
        book.setTitle("读者");
        bookRepository.save(book);
        for(Book b : bookRepository.findAll()) {
            System.out.println(b.toString());
        }

    }
}
