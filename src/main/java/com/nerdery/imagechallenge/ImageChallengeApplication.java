package com.nerdery.imagechallenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;

/**
 * @author Josh Klun (jklun@nerdery.com)
 */
@SpringBootApplication
public class ImageChallengeApplication extends WebMvcAutoConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(ImageChallengeApplication.class, args);
    }
}
