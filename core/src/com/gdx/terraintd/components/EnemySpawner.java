package com.gdx.terraintd.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gdx.terraintd.state_and_managers.StatsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnemySpawner implements Json.Serializable {
    private float spawnInterval;
    private float timeSinceLastSpawn;
    private int enemiesPerWave;
    public transient Pathfinder pathfinder;
    private float speed;
    private int health;
    private int scoreValue;
    private int goldValue;
    public transient GridSystem gridSystem;
    public transient StatsManager statsManager;
    public boolean spawningInProgress;
    private int enemiesSpawned;
    private List<int[]> path;
    private List<float[]> pathHexCenters;
    private List<String> pathTerrains;
    private String enemyId;
    private String texturePath;
    private final String[] enemyTextures = {
            "units/Spider.png",
            "units/Bandit.png",
            "units/Dragon.png",
    };
    private transient Random random;
    private String spawnerID;

    public EnemySpawner() {
        this.pathHexCenters = new ArrayList<>();
        this.pathTerrains = new ArrayList<>();
    }

    public EnemySpawner(StatsManager statsManager, GridSystem gridSystem, Pathfinder pathfinder, String spawnerID) {
        this();
        this.spawnerID = spawnerID;
        this.gridSystem = gridSystem;
        this.statsManager = statsManager;
        this.pathfinder = pathfinder;
        this.path = new ArrayList<>();
        this.pathHexCenters = new ArrayList<>();
        this.pathTerrains = new ArrayList<>();
        this.spawningInProgress = false;
        this.enemiesSpawned = 0;
        this.enemiesPerWave = 0;
        this.speed = 150f;
        this.health = 2;
        this.scoreValue = 1;
        this.goldValue = 1;
        this.spawnInterval = 2f;
        this.timeSinceLastSpawn = 0;
        this.random = new Random();
    }

    public void startNextWave(String[][] updatedGrid, Wave waveToSpawn) {
        if (!spawningInProgress) {
            if (path == null) {
                path = new ArrayList<>();
            } else {
                path.clear();
            }
            if (pathHexCenters == null) {
                pathHexCenters = new ArrayList<>();
            } else {
                pathHexCenters.clear();
            }
            if (pathTerrains == null) {
                pathTerrains = new ArrayList<>();
            } else {
                pathTerrains.clear();
            }
            path = pathfinder.findPath(updatedGrid);
            for (int[] coord : path) {
                Vector2 hexCenter = gridSystem.getHexCenter(coord[0], coord[1]);
                pathHexCenters.add(new float[]{hexCenter.x, hexCenter.y});
            }
            for (int[] coord : path) {
                String pathTerrain = gridSystem.getCellValue(coord[0], coord[1]);
                pathTerrains.add(pathTerrain);
            }
            enemiesPerWave =waveToSpawn.spawnCount;
            health = waveToSpawn.health;
            speed = waveToSpawn.speed;
            spawnInterval =waveToSpawn.spawnInterval;
            goldValue = waveToSpawn.goldValue;
            scoreValue = waveToSpawn.scoreValue;
            enemyId= waveToSpawn.enemyId;
            texturePath = waveToSpawn.texturePath;
            timeSinceLastSpawn = spawnInterval;
            enemiesSpawned = 0;
            spawningInProgress = true;
        }
    }

    public void update(float delta) {
        if (spawningInProgress && enemiesSpawned < enemiesPerWave) {
            timeSinceLastSpawn += delta;
        }
    }

    public boolean canSpawnEnemy() {
        return spawningInProgress && timeSinceLastSpawn >= spawnInterval && enemiesSpawned < enemiesPerWave;
    }

    public boolean hasCompletedSpawningEnemies() {
        return enemiesSpawned == enemiesPerWave;
    }

    public Enemy spawnEnemy() {
        String randomTexture = enemyTextures[random.nextInt(enemyTextures.length)];
        Enemy enemy = new Enemy(spawnerID, speed, path, pathHexCenters, pathTerrains, health, scoreValue, goldValue, texturePath, gridSystem.hexRBig);
        timeSinceLastSpawn = 0;
        enemiesSpawned++;
        if (enemiesSpawned == enemiesPerWave) {
            spawningInProgress = false;
        }
        return enemy;
    }

    @Override
    public void write(Json json) {
        json.writeValue("spawnInterval", spawnInterval);
        json.writeValue("timeSinceLastSpawn", timeSinceLastSpawn);
        json.writeValue("enemiesPerWave", enemiesPerWave);
        json.writeValue("speed", speed);
        json.writeValue("health", health);
        json.writeValue("scoreValue", scoreValue);
        json.writeValue("goldValue", goldValue);
        json.writeValue("spawningInProgress", spawningInProgress);
        json.writeValue("enemiesSpawned", enemiesSpawned);
        json.writeValue("path", path);
        json.writeValue("pathHexCenters", pathHexCenters);
        json.writeValue("pathTerrains", pathTerrains);
        json.writeValue("spawnerID", spawnerID);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        spawnInterval = jsonData.getFloat("spawnInterval");
        timeSinceLastSpawn = jsonData.getFloat("timeSinceLastSpawn");
        enemiesPerWave = jsonData.getInt("enemiesPerWave");
        speed = jsonData.getFloat("speed");
        health = jsonData.getInt("health");
        scoreValue = jsonData.getInt("scoreValue");
        goldValue = jsonData.getInt("goldValue");
        spawningInProgress = jsonData.getBoolean("spawningInProgress");
        enemiesSpawned = jsonData.getInt("enemiesSpawned");
        path = new ArrayList<>();
        path.addAll(json.readValue("path", List.class, int[].class, jsonData));
        pathHexCenters = new ArrayList<>();
        pathHexCenters.addAll(json.readValue("pathHexCenters", List.class, float[].class, jsonData));
        pathTerrains = new ArrayList<>();
        pathTerrains.addAll(json.readValue("pathTerrains", List.class, String.class, jsonData));
        spawnerID = jsonData.getString("spawnerID");
    }

    public void reconnect(GridSystem gridSystem, Pathfinder pathfinder, StatsManager statsManager) {
        this.gridSystem = gridSystem;
        this.pathfinder = pathfinder;
        this.statsManager =statsManager;
        this.random = new Random();
    }
}