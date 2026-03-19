package org.example.socks_store.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "socks_table")
public class Sock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "color")
    @NotNull(message = "You must set color")
    private String color;

    @Column(name = "cotton_percentage")
    @NotNull(message = "You must set cotton percentage")
    @Min(value = 0, message = "cotton percentage can't be less 0%")
    @Max(value = 100, message = "cotton percentage can't be bigger then 100%")
    private int cottonPercentage;

    @Column(name = "quantity")
    @NotNull(message = "You must set quantity")
    @Min(value = 0, message = "Set correct quantity, it can't be less 0")
    private int quantity;
}
