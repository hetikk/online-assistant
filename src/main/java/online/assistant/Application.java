package online.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    /*
        heroku commands:
        heroku logs -n 150 --app online--assistant

        git commands:
        git add .
        git commit -am "довавил Богдаеву Д. в список"
        git push -u origin master
     */

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
