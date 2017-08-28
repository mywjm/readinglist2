package com.example.readinglist2;

import org.apache.bcel.generic.BREAKPOINT;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Driver;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Fail.fail;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(
        classes = ReadingListApplication.class
)
@WebIntegrationTest(randomPort=true)
public class MongoTests {
    //配置浏览器驱动
    private static ChromeDriver browser;
    @Value("${local.server.port}")
    private int port;

    @Autowired
    private ReadingListRepository bookRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    @BeforeClass
    public static void openBrowser() {
        // 设置 chrome 的路径
        System.setProperty(
                "webdriver.chrome.driver",
                "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe");
        // 创建一个 ChromeDriver 的接口，用于连接 Chrome
        // 创建一个 Chrome 的浏览器实例
        browser = new ChromeDriver();
//        browser = new ChromeDriver();
        browser.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }
    @AfterClass
    public static void closeBrowser() {
        browser.quit();
    }
    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }
    @Test
    public void MongoConnectionTest() {
         Book book = new Book();
        book.setIsbn("123466");
        book.setDescription("这是一本好书");
        book.setAuthor("佚名");
        book.setTitle("读者");
//        bookRepository.save(book);
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

    @Test(expected = HttpClientErrorException.class)
    public void pageNotFound() {
        try {
            RestTemplate rest = new RestTemplate();
            rest.getForObject("http://localhost:8080/bogusPage", String.class);
            fail("Should result in http 404");

        }catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.NOT_FOUND, e.getStatusCode());
            throw e;
        }
    }

    @Test
    public void addBookToEmptyList() {
        String baseUrl = "http://localhost:"+port;
        browser.get(baseUrl);
        assertEquals("You have no books in your book list", browser.findElementByTagName("div").getText());
        browser.findElementByName("title").sendKeys("BOOK TITLE");
        browser.findElementByName("author").sendKeys("BOOK AUTHOR");
        browser.findElementByName("isbn").sendKeys("1234567890");
        browser.findElementByName("description").sendKeys("DESCRIPTION");
        browser.findElementByName("form").submit();

        WebElement dl = browser.findElementByCssSelector("dt.bookHeadline");
        assertEquals("BOOK TITLE BY BOOK AUTHOR (ISBN: 1234567890)", dl.getText());

        WebElement dt = browser.findElementByCssSelector("dd.bookDescription");
        assertEquals("DESCRIPTION", dt.getText());
    }


}
