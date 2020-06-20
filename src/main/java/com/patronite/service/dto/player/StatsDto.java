package com.patronite.service.dto.player;

import java.io.Serializable;

public class StatsDto implements Serializable {
    private static final long serialVersionUID = -1948420991263200424L;
    private int playerId;
    private String playerName;
    private int level;
    private int xp;
    private int xpTillNextLevel;
    private int hp;
    private int hpTotal;
    private int mp;
    private int mpTotal;
    private int attack;
    private int defense;
    private int agility;
    private int gold;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public int getXpTillNextLevel() {
        return xpTillNextLevel;
    }

    public void setXpTillNextLevel(int xpTillNextLevel) {
        this.xpTillNextLevel = xpTillNextLevel;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getHpTotal() {
        return hpTotal;
    }

    public void setHpTotal(int hpTotal) {
        this.hpTotal = hpTotal;
    }

    public int getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getMpTotal() {
        return mpTotal;
    }

    public void setMpTotal(int mpTotal) {
        this.mpTotal = mpTotal;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getAgility() {
        return agility;
    }

    public void setAgility(int agility) {
        this.agility = agility;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void damage(int damage) {
        hp = Math.max(hp - damage, 0);
    }

    public boolean isDead() {
        return hp <= 0;
    }
}
