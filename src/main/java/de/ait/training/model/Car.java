package de.ait.training.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor //конструктор который будет принимать все четыре
public class Car {
    private long id;
    private String color;
    private String model;
    private double price;
}
