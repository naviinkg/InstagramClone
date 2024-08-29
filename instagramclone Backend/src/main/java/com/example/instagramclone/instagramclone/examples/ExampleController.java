package com.example.instagramclone.instagramclone.examples;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class ExampleController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/example")
    public Example savExample(@RequestBody Example employee) {
        return employeeRepository.save(employee);
    }

    @GetMapping("example/{id}")
    public Example getExample(@PathVariable("id") int employeeId) {
        return employeeRepository.getEmployeeById(employeeId);
    }

}
