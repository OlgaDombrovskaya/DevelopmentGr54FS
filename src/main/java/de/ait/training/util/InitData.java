package de.ait.training.util;

import de.ait.training.model.Car;
import de.ait.training.repository.CarRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitData {
    @Bean
    CommandLineRunner init(CarRepository carRepository) {
        return args -> {
            if(carRepository.count() == 0) {
                carRepository.save(new Car("black","BMW x5", 25000));
                carRepository.save(new Car("green","Audi A4", 15000));
                carRepository.save(new Car("white","MB A220", 18000));
                carRepository.save(new Car("red","Ferrari", 250000));
            }
        };
    }

}