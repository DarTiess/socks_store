package org.example.socks_store.repository;

import org.example.socks_store.model.Sock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocksRepository extends JpaRepository<Sock, Long> {
    Optional<Sock> findByColorAndCottonPercentage(String color, int cottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color " +
            "AND s.cottonPercentage > :cottonPercentage")
    Long sumQuantityByColorAndCottonPercentageGreaterThan(
            @Param("color") String color,
            @Param("cottonPercentage") int cottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color " +
            "AND s.cottonPercentage < :cottonPercentage")
    Long sumQuantityByColorAndCottonPercentageLessThan(
            @Param("color") String color,
            @Param("cottonPercentage") int cottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color " +
            "AND s.cottonPercentage = :cottonPercentage")
    Long sumQuantityByColorAndCottonPercentageEquals(
            @Param("color") String color,
            @Param("cottonPercentage") int cottonPercentage);
}
