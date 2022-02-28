package com.its4u.buildfactory;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.its4u.buildfactory.services.ServiceToKubernetes;

import picocli.CommandLine;

@SpringBootApplication
public class ClusterCapacityJob {

	public static void main(String[] args) {
		SpringApplication.run(ClusterCapacityJob.class, args);	
		int exitCode = new CommandLine(new ServiceToKubernetes()).execute(args);
	    System.exit(exitCode);
	}
	

}
