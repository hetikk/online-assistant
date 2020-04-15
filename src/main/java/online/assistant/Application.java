package online.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    /*
        heroku commands:
        heroku logs -n 1500 --app gmi-testing-solver
     */

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
