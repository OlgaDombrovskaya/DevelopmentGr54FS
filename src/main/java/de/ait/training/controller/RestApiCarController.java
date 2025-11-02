package de.ait.training.controller;

import de.ait.training.model.Car;
import de.ait.training.repository.CarRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Cars", description = "Operation on cars")
@Slf4j
@RequestMapping("/api/cars")
@RestController
public class RestApiCarController {

    private final CarRepository carRepository;

    /*Car carOne = new Car(1, "black", "BMW x5", 25000);
    Car carTwo = new Car(2, "green", "Audi A4", 15000);
    Car carThree = new Car(3, "white", "MB A220", 18000);
    Car carFour = new Car(4, "red", "Ferrari", 250000);

    List<Car> cars = new ArrayList<>();

    public RestApiCarController() {
        cars.add(carOne);
        cars.add(carTwo);
        cars.add(carThree);
        cars.add(carFour);
    }*/

    public RestApiCarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }


    /**
     * GET /api/cars
     *
     * @return возвращает список всех автомобилей
     */
    @GetMapping
    Iterable<Car> getCars() {
        return carRepository.findAll();
    }

    /**
     * Создает новый автомобиль и добавляет его в лист
     *
     * @param car
     * @return созданный автомобиль
     */
    @Operation(
            summary = "Create car",
            description = "Create a new car",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Created")
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Car postCar(@RequestBody Car car) {
        car.setId(null);
//        if (car.getId() <= 0) {
//            log.error("Car id must be greater than zero");
//            Car errorCar = new Car("000", "000", 9999);
//            return errorCar;
//        }
        carRepository.save(car);
        log.info("Car posted successfully");
        return car;
    }

    /**
     * Замена существующего автомобиля, если id не найден то создаем новый
     *
     * @param id
     * @param car
     * @return созданный или найденный автомобиль
     */
    @PutMapping("/{id}")
    ResponseEntity<Car> putCar(@PathVariable long id, @RequestBody Car car) {

        Car foundCar = carRepository.findById(id).orElse(null);

        if (foundCar == null) {
            log.info("Car not found");
        } else {
            log.info("Car {} was found", id);
            carRepository.save(car);
        }

        return (foundCar == null)
                ? new ResponseEntity<>(postCar(car), HttpStatus.CREATED)
                : new ResponseEntity<>(car, HttpStatus.OK);
    }

    /**
     * удаляем автомобиль по id
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    void deleteCar(@PathVariable long id) {
        log.info("Delete car with id {}", id);
        carRepository.deleteById(id);
    }

    /**
     * GET /api/cars/color/{color}
     *
     * @return Если в списке отсутствует заданый цвет - возвращаем пустой список, если найден - Возвращает список всех автомобилей заданного цвета
     */
    @Operation(summary = "Get cars by color",
            description = "Returns a list of cars filtered by color",
            responses = @ApiResponse(responseCode = "200", description = "Found cars with color"))
    @GetMapping("/color/{color}")
    public ResponseEntity<List<Car>> getCarsByColor(@PathVariable String color) {
        List<Car> filteredCars = carRepository.findByColorIgnoreCase(color);


        if (filteredCars.isEmpty()) {
            log.warn("No cars found for color: {}", color);
            return new ResponseEntity<>(filteredCars, HttpStatus.NOT_FOUND);
        } else {

            log.info("Found {} cars with color: {}", filteredCars.size(), color);
            return new ResponseEntity<>(filteredCars, HttpStatus.OK);
        }

    }

    @GetMapping("/price/between/{min}/{max}") //GET 2500/500
    public ResponseEntity<List<Car>> getCarsByPriceBetween(@PathVariable Double min, @PathVariable Double max) {
        if(max < min) {
            log.error("Max price must be greater than min");
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
        List<Car> filteredCars = carRepository.findByPriceBetween(min, max);
        if (filteredCars.isEmpty()) {
            log.info("No cars found for price between {} and {}", min, max);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        log.info("Found {} cars with price between {} and {}", filteredCars.size(), min, max);
        return new ResponseEntity<>(filteredCars, HttpStatus.OK);
    }

    @GetMapping("/price/under/{max}")
    public ResponseEntity<List<Car>> getCarsByPriceLessThanEqual(@PathVariable Double max) {
        List<Car> filteredCars = carRepository.findByPriceLessThanEqual(max);
        if (filteredCars.isEmpty()) {
            log.info("No cars found for price less than {}", max);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        log.info("Found {} cars with price less than {}", filteredCars.size(), max);
        return new ResponseEntity<>(filteredCars, HttpStatus.OK);
    }

    @GetMapping("/price/over/{min}")
    public ResponseEntity<List<Car>> getCarsByPriceGreaterThan(@PathVariable Double min) {
        List<Car> filteredCars = carRepository.findByPriceGreaterThanEqual(min);
        if (filteredCars.isEmpty()) {
            log.info("No cars found for price greater than {}", min);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        log.info("Found {} cars with price greater than {}", filteredCars.size(), min);
        return new ResponseEntity<>(filteredCars, HttpStatus.OK);
    }
}