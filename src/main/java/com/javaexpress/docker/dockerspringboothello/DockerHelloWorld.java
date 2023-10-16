package com.javaexpress.docker.dockerspringboothello;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/docker")
public class DockerHelloWorld {

	@GetMapping
	public String getName() {
		
		return "Welcome to deployment in docker";
	}
	@GetMapping(path = "/author")
	public String authorName()
	{
		return "Nasser G Khaled";
	}
	@GetMapping(path = "/version")
	public String authorName()
	{
		return "1";
	}
}
