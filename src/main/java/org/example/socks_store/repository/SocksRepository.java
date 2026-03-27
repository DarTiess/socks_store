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

    @Query("""
            SELECT SUM(s.quantity) FROM Sock s
            WHERE (:color IS NULL OR s.color = :color)
                        AND ((:cottonPercentageMin IS NULL AND :cottonPercentageMax IS NULL)
                        OR (:cottonPercentageMin IS NOT NULL AND :cottonPercentageMax IS NOT NULL
                                    AND s.cottonPercentage BETWEEN :cottonPercentageMin AND :cottonPercentageMax)
                                                OR (:cottonPercentage IS NOT NULL AND :operators IS NOT NULL
                                                AND ((:operators = "moreThan" AND s.cottonPercentage > :cottonPercentage) OR
                                                            (:operators = "lessThan" AND s.cottonPercentage < :cottonPercentage) OR
                                                                        (:operators = "equal" AND s.cottonPercentage = :cottonPercentage)
                                                                                    )))
            """)
    Optional<Long> sumQuantityByFilter(
            @Param("color") String color,
            @Param("cottonPercentage") Integer cottonPercentage,
            @Param("operators") String operators,
            @Param("cottonPercentageMin") Integer cottonPercentageMin,
            @Param("cottonPercentageMax") Integer cottonPercentageMax);
}
