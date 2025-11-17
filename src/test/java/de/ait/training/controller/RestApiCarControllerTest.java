//package de.ait.training.controller;
//
//import de.ait.training.model.Car;
//import de.ait.training.repository.CarRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = RestApiCarController.class)
//class RestApiCarControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockitoBean
//    private CarRepository carRepository;
//
//    @Test
//    @DisplayName("price between 10000 and 30000, 3 cars found")
//    void testFindCarsPriceBetweenSuccess() throws Exception {
//        given(carRepository.findByPriceBetween(10000.0, 30000.0))
//                .willReturn(List.of(new Car("black", "BMW x5", 25000),
//                        new Car("green", "Audi A4", 15000),
//                        new Car("white", "MB A220", 18000)));
//
//        mockMvc.perform(get("/api/cars/price/between/{min}/{max}", 10000,30000))
//                .andExpect(status().isOk())
//        .andExpect(jsonPath("$.length()").value(3))
//        .andExpect(jsonPath("$[?(@.model == 'BMW x5')]").exists())
//        .andExpect(jsonPath("$[?(@.model == 'Audi A4')]").exists())
//        .andExpect(jsonPath("$[?(@.model == 'MB A220')]").exists());
//    }
//
//    @Test
//    @DisplayName("price between 10000 and 30000, 0 cars found, status Bad Request")
//    void testFindCarsPriceBetweenFailure() throws Exception {
//        mockMvc.perform(get("/api/cars/price/between/{min}/{max}", 30000,10000))
//                .andExpect(status().isBadRequest())
//        .andExpect(jsonPath("$.length()").value(0));
//    }
//
//    @Test
//    @DisplayName("price under then 16000, 1 car found, status OK")
//    void testFindCarLessThanEqualSuccess() throws Exception {
//        given(carRepository.findByPriceLessThanEqual(16000.0))
//                .willReturn(List.of(new Car("green","Audi A4", 15000)));
//
//        mockMvc.perform(get("/api/cars/price/under/{max}", 16000))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(1))
//                .andExpect(jsonPath("$[?(@.model == 'Audi A4')]").exists());
//    }
//}
