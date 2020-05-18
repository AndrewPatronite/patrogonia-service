package com.patronite.service.repository;

import com.patronite.service.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    @Query("select player from Player player where player.username = :username")
    Player findByUsername(@Param("username") String username);

    @Query("select player from Player player where player.username = :username and player.password = :password")
    Player findByLogin(@Param("username") String username, @Param("password") String password);

    @Query("select player from Player player where player.location.mapName = :mapName")
    List<Player> findByLocation(@Param("mapName") String mapName);
}
