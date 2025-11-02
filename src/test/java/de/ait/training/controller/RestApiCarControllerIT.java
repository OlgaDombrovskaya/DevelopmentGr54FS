package de.ait.training.controller;

import de.ait.training.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RestApiCarControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    /*Test scenario 3 GET/api/cars/price/between/{min}/{max}*/

    @Test
    @DisplayName("price between 10000 and 30000, 3 cars were found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testPriceBetween10000And30000() throws Exception {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/10000/30000"),
                Car[].class);
        //assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(3);
        assertThat(cars.get(0).getModel()).isEqualTo("BMW x5");
    }

    @Test
    @DisplayName("price under 16000, 1 car was found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void testPriceUnder16000Success() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/under/16000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(1);
        assertThat(cars.get(0).getModel()).isEqualTo("Audi A4");
    }

    /*Test scenario 3 GET/api/cars/price/between/{min}/{max}*/

    @Test
    @DisplayName("wrong min and max price, 0 cars ware found, status 400 BadRequest")
    void testMinMaxPricesWrongFail() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/30000/10000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Car[] result = response.getBody();
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }

    /*Test scenario 1 GET/api/cars*/

    @Test
    @DisplayName("List of all cars, all cars were found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void shouldReturnAllCars() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("No Data, 0 cars were found, status OK и []")
    @Sql(scripts = {"classpath:sql/clear.sql"})
    void shouldReturnEmptyListWhenNoCars() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(0);
    }

    /*Test scenario 2 GET/api/cars/color/{color}*/

    @Test
    @DisplayName("Color found (case insensitive), cars with color red or RED were found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void shouldReturnCarsWhenColorFoundIgnoreCase() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/color/ReD"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(1);
        assertThat(cars.get(0).getModel()).isEqualTo("Ferrari");
    }

    @Test
    @DisplayName("Color not found, return empty array, status 404 NOT_FOUND")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void shouldReturn404WhenColorNotFound() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/color/purple"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(0);
    }

    /*Test scenario 3 GET/api/cars/price/between/{min}/{max}*/

    @Test
    @DisplayName("There no matches prices, 0 cars ware found, status 404 NOT_FOUND")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void shouldReturnEmptyListWhenNoCarsWithMatchesPrices() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/between/100/500"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Сars with price exactly min and max are included in result, 3 cars return, status OK ")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void shouldIncludeCarsWithExactMinAndMaxPrice() throws Exception {

        ResponseEntity<Car[]> response = restTemplate.getForEntity(
                url("/api/cars/price/between/15000/25000"), Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());

        assertThat(cars.stream().anyMatch(c -> c.getPrice() == 15000.0)).isTrue();
        assertThat(cars.stream().anyMatch(c -> c.getPrice() == 25000.0)).isTrue();
        assertThat(cars.size()).isEqualTo(3);
    }

    /*Test scenario 4 GET/api/cars/price/under/{max}*/

    @Test
    @DisplayName("price under 20000, 2 car was found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void shouldReturnCarsWithPriceUnder20000Success()throws Exception {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/under/20000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Price less than 1000, 0 cars found, status 404 NOT_FOUND")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void shouldReturnsNotFoundWhenPriceUnder1000() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(
                url("/api/cars/price/under/1000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Car[] result = response.getBody();
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }

    /*Test scenario 5 GET/api/cars/price/over/{min}*/

    @Test
    @DisplayName("price under 25000, 2 car was found, status OK")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void shouldReturnCarsWithPriceOver25000Success() throws Exception{
        ResponseEntity<Car[]> response = restTemplate.getForEntity(url("/api/cars/price/over/25000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Price less than 1000000, 0 cars found, status 404 NOT_FOUND")
    @Sql(scripts = {"classpath:sql/clear.sql", "classpath:sql/seed_cars.sql"})
    void shouldReturnsNotFoundWhenPriceOver1000000() {
        ResponseEntity<Car[]> response = restTemplate.getForEntity(
                url("/api/cars/price/over/1000000"),
                Car[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        Car[] result = response.getBody();
        List<Car> cars = Arrays.asList(response.getBody());
        assertThat(cars.isEmpty()).isEqualTo(true);
    }
}