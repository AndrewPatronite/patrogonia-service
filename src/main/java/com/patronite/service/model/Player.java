package com.patronite.service.model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "player", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column private int id;
    @Column private String name;
    @Column private String username;
    @Column private String password;
    //TODO add foreign key constraint:
    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Location location;
    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Stats stats;
    @ElementCollection
    private Set<String> visited;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Set<String> getVisited() {
        return visited;
    }

    public void setVisited(Set<String> visited) {
        this.visited = visited;
    }
}
