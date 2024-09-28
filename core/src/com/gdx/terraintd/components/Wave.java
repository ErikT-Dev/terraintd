package com.gdx.terraintd.components;

public class Wave {
    public int waveNumber;
    public String spawnerId;
    public String enemyId;
    public int health;
    public float speed;
    public int goldValue;
    public int scoreValue;
    public String texturePath;
    public int spawnCount;
    public float spawnInterval;

    public Wave(int waveNumber,String spawnerId, String enemyId, int health,  float speed, int goldValue, int scoreValue,  String texturePath, int spawnCount,float spawnInterval ){
        this.waveNumber = waveNumber;
        this.spawnerId = spawnerId;
        this.enemyId = enemyId;
        this.health =health;
        this.speed=speed;
        this.goldValue = goldValue;
        this.scoreValue = scoreValue;
        this.texturePath = texturePath;
        this.spawnCount = spawnCount;
        this.spawnInterval=spawnInterval;
    }
}
