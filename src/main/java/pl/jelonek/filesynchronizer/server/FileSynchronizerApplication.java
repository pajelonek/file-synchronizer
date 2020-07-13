package pl.jelonek.filesynchronizer.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FileSynchronizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileSynchronizerApplication.class, args);
	}

}
