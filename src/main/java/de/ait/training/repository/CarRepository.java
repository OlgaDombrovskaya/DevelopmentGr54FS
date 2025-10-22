package de.ait.training.repository;

import de.ait.training.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository  extends JpaRepository<Car, Long> {
    List<Car> findByColorIgnoreCase(String color);
    List<Car> findByPriceBetween(Double min, Double max);
    List<Car> findByPriceLessThanEqual(Double max);
    List<Car> findByPriceGreaterThanEqual(Double min);
}