package de.ait.training.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity //класс который уже имеет в базе данных свою таблицу
@Table(name = "cars") //хотим такое имя
public class Car {
    @Id //это создает уникальный Id для автомобиля
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)//при попытке сохранить в базу данных с null он отсекает
    private String color;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private double price;

    public Car(String color, String model, double price) {
        this.color = color;
        this.model = model;
        this.price = price;
    }
}
