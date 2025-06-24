package org.example;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@ComponentScan(basePackages = {"org.example", "org.audio", "org.textsearch", "org.playlists", "org.recommendation"})
public class Main {

    @RequestMapping("/")
    String home() {
        return "Vibe!";
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}