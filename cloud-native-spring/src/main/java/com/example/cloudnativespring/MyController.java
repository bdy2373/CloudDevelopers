package com.example.cloudnativespring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

	private final MyConfig menu;

	public MyController(MyConfig menu) {
		this.menu = menu;
	}

    @GetMapping("/")
    public String hello() {
        return "Hello World!";
    }


	@GetMapping("/menu")
	public String restaurant() {
		return String.format("Today's special is: %s", menu.getSpecial());
	}

}
