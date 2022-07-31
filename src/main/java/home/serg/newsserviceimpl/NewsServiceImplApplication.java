package home.serg.newsserviceimpl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NewsServiceImplApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsServiceImplApplication.class, args);
    }

}
