package com.example.readinglist2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTests {

    @Autowired
    private ReadingListRepository bookRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }
    @Test
    public void MongoConnectionTest() {
       /* Book book = new Book();
        book.setIsbn("123466");
        book.setDescription("这是一本好书");
        book.setAuthor("佚名");
        book.setTitle("读者");
        bookRepository.save(book);*/
        for(Book b : bookRepository.findAll()) {
            System.out.println(b.toString());
        }

    }

    @Test
    public void updateBook() {
        Book updateBook = bookRepository.findOne("599fbe6c2531bd01946bded8");
        updateBook.setIsbn("1238888");
        bookRepository.save(updateBook);
    }

    @Test
    public void homePage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/readingList"))
                .andExpect(status().isOk())
                .andExpect(view().name("readingList"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", is(empty())));

    }

    @Test
    public void postBook() throws Exception{
        mockMvc.perform(post("/readingList").contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .param("title", "BOOK TITLE")
        .param("author", "BOOK AUTHOR")
        .param("isbn", "1234567890")
        .param("description", "DESCRIPTION"))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", "/readingList"));

        //配置期望的图书
        Book expectedBook = new Book();
        expectedBook.setIsbn("599fe7e02531bd2e5014507f");
        expectedBook.setReader("craig");
        expectedBook.setTitle("BOOK TITLE");
        expectedBook.setAuthor("BOOK AUTHOR");
        expectedBook.setIsbn("1234567890");
        expectedBook.setDescription("DESCRIPTION");

        //执行GET
        mockMvc.perform(get("/readingList"))
                .andExpect(status().isOk())
                .andExpect(view().name("readingList"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("books", hasSize(4)))
                .andExpect(model().attribute("books", contains(samePropertyValuesAs(expectedBook))));
    }
}
