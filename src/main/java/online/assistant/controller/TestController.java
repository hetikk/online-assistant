package online.assistant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import online.assistant.service.TestService;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = RequestMethod.POST)
@RestController
public class TestController {

    @Autowired
    private TestService service;

    @PostMapping(value = "/test", produces = "text/html")
    public String solve(@RequestBody String body) {
        return service.solveTest(body);
    }

}