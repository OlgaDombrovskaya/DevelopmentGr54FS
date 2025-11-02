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

    public Car(String color, String model, double price) {
        this.color = color;
        this.model = model;
        this.price = price;
    }
}