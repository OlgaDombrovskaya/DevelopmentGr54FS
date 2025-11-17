package de.ait.training.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor //фреймворку джексон нужен этот конструктор
@Entity //класс имеющий в базе данных свою таблицу
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private double price;

    @Column(name = "engine_type")
    private String engineType;

    @Column(name = "year")
    private int year;

    @Column(name = "image_url")
    private String imageUrl;

    public Car(String color, String model, double price) {
        this.color = color;
        this.model = model;
        this.price = price;
    }

    public Car(Long id, String engineType, int year, String imageUrl) {
        this.id = id;
        this.engineType = engineType;
        this.year = year;
        this.imageUrl = imageUrl;
    }
}