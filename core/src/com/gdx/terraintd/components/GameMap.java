package com.gdx.terraintd.components;

public class GameMap {
    public String name;
    public int forests;
    public int seas;
    public int sands;
    public int hills;
    public int forestTokens;
    public int grassTokens;
    public int spawner1X;
    public int spawner1Y;
    public int spawner2X;
    public int spawner2Y;
    public int spawner3X;
    public int spawner3Y;
    public int endX;
    public int endY;

    public GameMap(String name, int forests, int seas, int sands, int hills, int forestTokens, int grassTokens, int spawner1X, int spawner1Y, int spawner2X, int spawner2Y, int spawner3X, int spawner3Y, int endX, int endY) {
        this.name = name;
        this.forests = forests;
        this.seas = seas;
        this.sands = sands;
        this.hills = hills;
        this.forestTokens = forestTokens;
        this.grassTokens = grassTokens;
        this.spawner1X = spawner1X;
        this.spawner1Y = spawner1Y;
        this.spawner2X = spawner2X;
        this.spawner2Y = spawner2Y;
        this.spawner3X = spawner3X;
        this.spawner3Y = spawner3Y;
        this.endX = endX;
        this.endY = endY;
    }

    public GameMap() {
    }

    public String getName() {
        return name;
    }
}