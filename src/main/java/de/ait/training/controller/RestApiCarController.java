package de.ait.training.controller;

import de.ait.training.model.Car;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
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

    Car carOne = new Car(1, "black", "BMW", 25000);
    Car carTwo = new Car(2, "green", "AUDI A4", 15000);
    Car carThree = new Car(3, "white", "MB A220", 18000);
    Car carFour = new Car(4, "red", "Ferrari", 250000);

    List<Car> cars = new ArrayList<>();

    /**
     * конструктор который будет полюбому вызываться при старте приложения,
     * но как только остановится все данные будут потеряны
     */
    public RestApiCarController() {
        cars.add(carOne);
        cars.add(carTwo);
        cars.add(carThree);
        cars.add(carFour);
    }

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
        return cars;
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

    /**
     * GET /api/cars/{color}
     *
     * @return Возвращает список всех автомобилей заданного цвета
     */
    @GetMapping("/{color}")
    ResponseEntity<List<Car>> getCarsByColor(@PathVariable String color) {
        List<Car> resultCarsColor = cars.stream()
                .filter(car -> car.getColor().equals(color))
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
            Car errorCar = new Car(9999, "000", "000", 9999);

            return errorCar;
            /**Spring Boot автоматически сериализует его в JSON и отправит как ответ на POST-запрос.
             возвращаем пустышку(заглушку) но можно было бы вернуть null*/
        }
        cars.add(car);
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
        int carIndex = -1;
        for (Car carInList : cars) {
            if (carInList.getId() == id) {
                /**тогда когда нашли (например с индексом 25)нужно зафиксировать индекс автомобиля
                 чтобы не путать Id и Index, по Id нашли -> смотрим индекс(они могут отличаться на 1)
                 и записываем уже по индексу на это место*/
                carIndex = cars.indexOf(carInList);
                /** берем лист cars и вызываем метод set: carIndex - это на какой позиции стоит наш элемент,
                 car - это что записать на эту позицию вместо того что там есть*/
                cars.set(carIndex, car);
                log.info("Car id " + carInList.getId() + " has been updated");
            }
        }

        return (carIndex == -1)
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
        /** предикат: удалить если внутри совпал Id*/
        cars.removeIf(car -> car.getId() == id);
    }
}