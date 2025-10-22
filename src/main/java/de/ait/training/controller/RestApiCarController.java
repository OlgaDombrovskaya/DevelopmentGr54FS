package de.ait.training.controller;

import de.ait.training.model.Car;
import de.ait.training.repository.CarRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "Cars", description = "Operation on cars")

@Slf4j
@RestController
@RequestMapping("/api/cars")
public class RestApiCarController {

    private CarRepository carRepository;

    public RestApiCarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

//    List<Car> cars = new ArrayList<>();

    /**
     * GET/api/cars
     * api /cars - будет get запрос к api а там уже к cars, Iterable - это интерфейс
     * когда не важно что возвращаем, это будет лист или коллекция (Set) даже стрим
     * где есть последовательность элементов, удобно что не нужно думать что это конкретно
     *
     * @return возвращает список всех автомобилей
     */
    @GetMapping
    Iterable<Car> getCars() {
        return carRepository.findAll();
    }

    /**
     * Возвращает список автомобилей, отфильтрованных по цвету
     *
     * @param color, цвет автомобиля, по которому выполняется фильтрация
     * @return список автомобилей или 404
     */
    @Operation(
            summary = "Get cars by color",
            description = "Returns a list of cars filtered by color",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cars found successfully"),
                    @ApiResponse(responseCode = "404", description = "If no cars found, returns Not Found")
            }
    )

    @GetMapping("/color/{color}")
    ResponseEntity<List<Car>> getCarsByColor(@PathVariable String color) {
        List<Car> resultCarsColor = carRepository.findCarByColorIgnoreCase(color)
                .stream()
                .filter(car -> car.getColor().equalsIgnoreCase(color))
                .toList();

        if (resultCarsColor.isEmpty()) {
            log.warn("No cars found for color: {}", color);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            log.info("Found {} cars with color: {}", resultCarsColor.size(), color);
            return new ResponseEntity<>(resultCarsColor, HttpStatus.OK);
        }
    }

    /**
     * получаем все автомобили, у которых price находится включительно между min и
     * max.
     * Если max < min — вернуть пустой список.
     * @param min
     * @param max
     * @return список автомобилей с ценой в указанном диапазоне или пустой список
     */
    @Operation(
            summary = "Get cars by price",
            description = "Returns a list of cars filtered by price between min and max value or empty list(if max < min)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cars found successfully"),
                    @ApiResponse(responseCode = "404", description = "If no cars found, returns empty list")
            }
    )

    @GetMapping("/price/between/{min}/{max}")
    ResponseEntity<List<Car>> getCarsByPriceBetween(@PathVariable Integer min, @PathVariable Integer max) {
        List<Car> resultByPrice = carRepository.findByPriceBetween(min,max)
                .stream()
                .filter(car -> car.getPrice() >=min && car.getPrice() <=max)
                .toList();
        if (min > max) {
            log.warn("Invalid price between {} and {}", min, max);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            log.info("Found {} cars with price between {} and {}", resultByPrice, max, min);
        }
        return new ResponseEntity<>(resultByPrice, HttpStatus.OK);
    }

    /**
     * получаем все автомобили, у которых price ниже max
     *
     * @param max
     * @return список автомобилей с ценой ниже максимальной
     */
    @Operation(
            summary = "Get cars by price under max",
            description = "Returns a list of cars filtered by price with price under max",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cars found successfully"),
                    @ApiResponse(responseCode = "404", description = "If no cars found, returns empty list")
            }
    )

    @GetMapping("/price/under/{max}")
    ResponseEntity<List<Car>> getCarsByPriceUnderMax(@PathVariable Integer max) {
        List<Car> resultByPriceUnderMax = carRepository.findByPriceLessThanEqual(max)
                .stream()
                .filter(car -> car.getPrice() <= max)
                .toList();
        if (max > resultByPriceUnderMax.size()) {
            log.warn("Invalid price under max {}", max);
        } else {
            log.info("Found {} cars with price under max", max);
        }
        return new ResponseEntity<>(resultByPriceUnderMax, HttpStatus.OK);
    }


    /**
     * получаем все автомобили, у которых price выше min
     *
     * @param min
     * @return список автомобилей с ценой выше минимальной
     */
    @Operation(
            summary = "Get cars by price over min",
            description = "Returns a list of cars filtered by price with price over min",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cars found successfully"),
                    @ApiResponse(responseCode = "404", description = "If no cars found, returns empty list")
            }
    )

    @GetMapping("/price/over/{min}")
    ResponseEntity <List<Car>> getCarsByPriceOverMin(@PathVariable Integer min) {
        List<Car> resultByPriceOverMin = carRepository.findByPriceGreaterThanEqual(min)
                .stream()
                .filter(car -> car.getPrice() >=min)
                .toList();
        if (min > resultByPriceOverMin.size()) {
            log.warn("Invalid price over min {}", min);
        }else  {
            log.info("Found {} cars with price over min", min);
        }
        return new ResponseEntity<>(resultByPriceOverMin, HttpStatus.OK);
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
    /** Говорит, что этот метод будет вызываться,
     когда на адрес http://<сервер>/api/cars придёт HTTP POST запрос.*/
    Car postCar(@RequestBody Car car) {
        /** Объявление метода postCar, который:возвращает объект типа Car; принимает
         * тело HTTP-запроса (JSON), которое автоматически конвертируется в объект Car
         * благодаря аннотации Spring Boot сам парсит JSON → создаёт объект Car
         * и передаёт его в параметр car.*/
        if (car.getId() <= 0) {
            log.error("Car id must be greater than zero");
            Car errorCar = new Car("000", "000", 9999);

            return errorCar;
            /**Spring Boot автоматически сериализует его в JSON и отправит как ответ на POST-запрос.
             возвращаем пустышку(заглушку) но можно было бы вернуть null*/
        }
        carRepository.save(car);
        log.info("Car posted successfully");
        return car;
    }

    /**
     * Замена существующего автомобиля, если id не найден то создаем новый (обновление через замену)
     *
     * @param id
     * @param car
     * @return созданный или найденный автомобиль
     */
    @PutMapping("/{id}")
    /** комбинируем два метода
     @PathVariable long id - возможность через строку передать параметр, через Putзапрос
     @RequestBody Car car -внутри самого запроса несем изменения целиком*/
    ResponseEntity<Car> putCar(@PathVariable long id, @RequestBody Car car) {
        /** задание: если car с этим Id не найден, то мы сохраняем его как новый с этим Id*/

        Car foundCar = carRepository.findById(id).orElse(null);

       if(foundCar == null) {
           log.info("Car not found with id {}", id);
       }else {
           log.info("Found car with id {}", id);
           carRepository.save(car);
       }

        return (foundCar == null)
                /** если carIndex не поменялся
                 ResponseEntity небольшой контейнер где мы можем модернизировать содержимое ответа и статус ответа
                 (а не просто один ответ в виде 200 или ОК)
                 если не нашел создает новый автомобиль через вызов метода postCar и передаем туда новый сar + статус
                 в противном случае (если он все-таки поменялся)возвращаем тот car который мы обновили*/
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
}