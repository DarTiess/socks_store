package org.example.socks_store.repository;

import org.example.socks_store.model.Sock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocksRepository extends JpaRepository<Sock, Long> {
   Optional<Sock> findByColorAndCottonPercentage(String color, int cottonPercentage);

}
