package de.ait.training.controller;

import de.ait.training.model.Car;
import de.ait.training.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestApiCarControllerTestITPostgres {

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    CarRepository repository;

    @Test
    public void getAllCarsSuccess() {

        /*
        http-запрос состоит из тела, заголовков, а также имеет тип (метод)
        Тело - содержит информацию, которую мы отправляем на сервер
        (например, объект автомобиля для сохранения в БД)
        Заголовки - содержат служебную информацию о запросе
        (например, информацию об авторизации, куки и др.)
        Тип запроса (метод) - GET, POST, PUT, DELETE

        Создаём объект заголовков запроса.
        Хотя нам пока нечего вкладывать в заголовки, их лучше всё равно создать,
        хотя бы пустые, потому что некоторые веб-сервера могут вернуть ошибку,
        если в запросе совсем не будет заголовков
         */
        HttpHeaders headers = new HttpHeaders();

        // Создаём объект http-запроса
        // Так как нам ничего не нужно вкладывать в тело запроса,
        // параметризуем запрос типом Void
        HttpEntity<Void> request = new HttpEntity<>(headers);

        /*
        Здесь мы отправляем на наше тестовое приложение реальный http-запрос
        и получаем реальный http-ответ. Это и делает метод exchange(при кастомной авторизации
        или какие-то настроенные заголовки вложить в запрос).
        Четыре аргумента метода:
        1. Эндпоинт, на который отправляется запрос.
        2. Тип (метод) запроса.
        3. Объект запроса (с вложенными в него заголовками и телом)
        4. Тип данных, который мы ожидаем получить с сервера.
         */
        // Проблема: Iterable<Car>.class в качестве четвёртого аргумента не работает,
        // это нарушение синтаксиса.
        // Решение 1: использовать массив - Car[]
        ResponseEntity<Car[]> response = restTemplate.exchange(
                "/api/cars", HttpMethod.GET, request, Car[].class
        );

        // Решение 2: преобразовать полученный массив в лист
        List<Car> cars = Arrays.asList(response.getBody());

        // Решение 3: использование класса ParameterizedTypeReference
        // В этом случае никакие преобразования уже не нужны, сразу получаем список
        ResponseEntity<List<Car>> response1 = restTemplate.exchange(
                "/api/cars", HttpMethod.GET, request, new ParameterizedTypeReference<List<Car>>() {}
        );

        // Здесь мы проверяем, действительно ли от сервера пришёл ответ с правильным статусом
        // ВАЖНО! В метод assertEquals нужно передавать сначала ожидаемое значение,
        // потом действительное. НЕ НАОБОРОТ!
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Unexpected http status");

        // Получим тело ответа из самого объекта ответа
        Car[] body = response.getBody();

        // Проверяем, а есть ли вообще тело в ответе от сервера
        assertNotNull(body, "Response body should not be null");

        for (Car car : body) {
            assertNotNull(car.getId(), "Car ID should not be null");
            assertNotNull(car.getColor(), "Car color should not be null");
            assertNotNull(car.getModel(), "Car model should not be null");
            // Допускаем, что цена может равняться нулю для случаев, когда автомобиль
            // ещё не оценён и не выставлен на продажу
            assertTrue(car.getPrice() >= 0, "Car price cannot be negative");
        }
    }

    @Test
    public void postNewCarSuccess() {

        HttpHeaders headers = new HttpHeaders();

        // Поскольку мы тестируем сохранение автомобиля в базу данных, то нам нужно
        // создать тестовый объект, который мы и будем отправлять на сервер
        Car testCar = new Car("Test color", "Test model", 77777.77);

        // В этом случае мы отправляем автомобиль в теле запроса, поэтому
        // сам запрос параметризуем типом Car и вкладываем объект автомобиля
        // в объект запроса.
        HttpEntity<Car> request = new HttpEntity<>(testCar, headers);

        ResponseEntity<Car> response = restTemplate.exchange(
                "/api/cars", HttpMethod.POST, request, Car.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Unexpected http status");

        Car savedCar = response.getBody();

        assertNotNull(savedCar, "Saved car should not be null");
        assertNotNull(savedCar.getId(), "Saved car ID should not be null");
        assertEquals(testCar.getColor(), savedCar.getColor(), "Saved car color is incorrect");
        assertEquals(testCar.getModel(), savedCar.getModel(), "Saved car model is incorrect");
        assertEquals(testCar.getPrice(), savedCar.getPrice(), "Saved car price is incorrect");
    }

    @Test
    public void putCarSuccess() {
        Car car = new Car("Red", "Test_BMW", 50000.0);
        Car saved = repository.save(car);
        Long id = saved.getId();
        assertNotNull(id, "Saved car id must not be null");

        Car updatedCar = new Car("Black", "Test_BMW X5", 75000.0);
        updatedCar.setId(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Car> request = new HttpEntity<>(updatedCar, headers);

        ResponseEntity<Car> response = restTemplate.exchange(
                "/api/cars/" + id,
                HttpMethod.PUT,
                request,
                Car.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Unexpected http status");

        Car body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals(id, body.getId(), "Car id must stay the same after update");
        assertEquals("Black", body.getColor(), "Car color was not updated");
        assertEquals("Test_BMW X5", body.getModel(), "Car model was not updated");
        assertEquals(75000.0, body.getPrice(), "Car price was not updated");
    }

    @Test
    public void putCarCreatesWhenNotFound() {
        long TestCarId = 99999;

        Car newCar = new Car("Yellow", "Test_Audi", 12345.0);
        newCar.setId(TestCarId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Car> request = new HttpEntity<>(newCar, headers);

        ResponseEntity<Car> response = restTemplate.exchange(
                "/api/cars/" + TestCarId,
                HttpMethod.PUT,
                request,
                Car.class
        );

        assertEquals(HttpStatus.CREATED, response.getStatusCode(), "Unexpected http status");

        Car body = response.getBody();
        assertNotNull(body, "Response body should not be null");
        assertEquals("Yellow", body.getColor(), "Car color is incorrect");
        assertEquals("Test_Audi", body.getModel(), "Car model is incorrect");
        assertEquals(12345.0, body.getPrice(), "Car price is incorrect");
    }

    public void getAllCarsWithAuthExample() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("test_login", "111");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Car[]> response = restTemplate.exchange(
                "/api/cars", HttpMethod.GET, request, Car[].class
        );

        String token = "$cdncdlksnclksmdclksmcklm23232";
        HttpHeaders headers1 = new HttpHeaders();

        headers1.add(HttpHeaders.COOKIE, "Token = " + token);

        HttpEntity<Void> request1 = new HttpEntity<>(headers1);

    }
}