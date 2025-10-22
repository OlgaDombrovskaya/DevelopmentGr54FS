package de.ait.training.repository;

import de.ait.training.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

  List<Car> findCarByColorIgnoreCase(String color);
  List<Car> findByPriceBetween(Integer min, Integer max);
  List<Car> findByPriceLessThanEqual(Integer max);
  List<Car> findByPriceGreaterThanEqual(Integer min);
}
