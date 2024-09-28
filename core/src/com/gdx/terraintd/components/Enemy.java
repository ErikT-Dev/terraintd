package com.gdx.terraintd.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Enemy implements Json.Serializable {
    private int currentIndex;
    private Vector2 currentPos;
    private Vector2 nextPos;
    private float speed;
    private float originalSpeed;
    private List<int[]> path = new ArrayList<>();
    private List<float[]> pathHexCenters = new ArrayList<>();
    private List<String> pathTerrains = new ArrayList<>();
    private float moveProgress;
    public int health;
    public int originalHealth;
    public int scoreValue;
    public int goldValue;
    public float slowPercent = 0.0f;
    public String currentTerrain;
    public float terrainSpeedFactor = 1;
    public String texturePath;
    public float textureSize;
    private static int nextId = 0;
    private int id;
    private String spawnerId;
    public float slowFactor = 1f;
    private float slowDuration = 0f;

    public Enemy(String spawnerId, float speed, List<int[]> path, List<float[]> pathHexCenters, List<String> pathTerrains, int health, int scoreValue, int goldValue, String texturePath, float hexSize) {
        this.spawnerId = spawnerId;
        this.originalSpeed = speed;
        this.speed = originalSpeed;
        this.path = path;
        this.pathHexCenters = pathHexCenters;
        this.pathTerrains = pathTerrains;
        this.currentIndex = 0;
        this.moveProgress = 0f;
        this.originalHealth = health;
        this.health = health;
        this.scoreValue = scoreValue;
        this.goldValue = goldValue;
        this.texturePath = texturePath;
        this.textureSize = hexSize * 0.9f;
        this.id = nextId++;

        if (path != null && !path.isEmpty()) {
            float[] startHexCenter = pathHexCenters.get(0);
            this.currentPos = new Vector2(startHexCenter[0],startHexCenter[1]);
            if (path.size() > 1) {
                float[] nextHexCenter = pathHexCenters.get(1);
                this.nextPos = new Vector2(nextHexCenter[0],nextHexCenter[1]);
            } else {
                this.nextPos = this.currentPos;
            }
        }
    }

    public Enemy(){}

    public void applySlowEffect(float slowAmount, float duration) {
        this.slowFactor = 1f - slowAmount;
        this.slowDuration = duration;
        updateSpeed();
    }

    private void updateSpeed() {
        speed = originalSpeed * terrainSpeedFactor * slowFactor;
    }

    public void update(float delta) {
        if (slowDuration > 0) {
            slowDuration -= delta;
            if (slowDuration <= 0) {
                slowFactor = 1f;
                updateSpeed();
            }
        }
        if (currentIndex < path.size() - 1) {
            moveProgress += (speed * delta) / currentPos.dst(nextPos);
            if (moveProgress >= 1f) {
                currentIndex++;
                moveProgress = 0f;
                currentPos = nextPos;
                currentTerrain = pathTerrains.get(currentIndex);
                if(Objects.equals(currentTerrain, "Sand")){
                    terrainSpeedFactor = 0.5f;
                }
                if(Objects.equals(currentTerrain, "Grass")){
                    terrainSpeedFactor = 1f;
                }
                speed = originalSpeed * terrainSpeedFactor;
                if (currentIndex < path.size() - 1) {
                    float[] nextHexCenter = pathHexCenters.get(currentIndex + 1);
                    nextPos = new Vector2(nextHexCenter[0],nextHexCenter[1]);
                }
            }
        }
    }

    public boolean hasReachedEndPoint() {
        return currentIndex == path.size() - 1;
    }

    public boolean hasDied() {
        return health <= 0;
    }

    public float getVisualScreenX() {
        return currentPos.x + (nextPos.x - currentPos.x) * moveProgress;
    }

    public float getVisualScreenY() {
        return currentPos.y + (nextPos.y - currentPos.y) * moveProgress;
    }

    public Vector2 getDrawPosition() {
        return currentPos.cpy().lerp(nextPos, moveProgress);
    }

    public float getHealthPercentage() {
        return (float) health / originalHealth;
    }

    public String getSpawnerId() {
        return spawnerId;
    }
    public int getId() {
        return id;
    }

    @Override
    public void write(Json json) {
        json.writeValue("currentIndex", currentIndex);
        json.writeValue("currentPos", currentPos);
        json.writeValue("nextPos", nextPos);
        json.writeValue("speed", speed);
        json.writeValue("originalSpeed", originalSpeed);
        json.writeValue("path", path);
        json.writeValue("pathHexCenters", pathHexCenters);
        json.writeValue("pathTerrains", pathTerrains);
        json.writeValue("moveProgress", moveProgress);
        json.writeValue("health", health);
        json.writeValue("originalHealth", originalHealth);
        json.writeValue("scoreValue", scoreValue);
        json.writeValue("goldValue", goldValue);
        json.writeValue("slowPercent", slowPercent);
        json.writeValue("currentTerrain", currentTerrain);
        json.writeValue("terrainSpeedFactor", terrainSpeedFactor);
        json.writeValue("texturePath", texturePath);
        json.writeValue("textureSize", textureSize);
        json.writeValue("spawnerID", spawnerId);
        json.writeValue("id", id);
        json.writeValue("nextId", nextId);
        json.writeValue("slowFactor", slowFactor);
        json.writeValue("slowDuration", slowDuration);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        currentIndex = jsonData.getInt("currentIndex");
        currentPos = json.readValue(Vector2.class, jsonData.get("currentPos"));
        nextPos = json.readValue(Vector2.class, jsonData.get("nextPos"));
        speed = jsonData.getFloat("speed");
        originalSpeed = jsonData.getInt("originalSpeed");
        path.clear();
        path.addAll(json.readValue(List.class, int[].class, jsonData.get("path")));
        pathHexCenters.clear();
        pathHexCenters.addAll(json.readValue(List.class, float[].class, jsonData.get("pathHexCenters")));
        pathTerrains.clear();
        pathTerrains.addAll(json.readValue(List.class, String.class, jsonData.get("pathTerrains")));
        moveProgress = jsonData.getFloat("moveProgress");
        health = jsonData.getInt("health");
        originalHealth = jsonData.getInt("originalHealth");
        scoreValue = jsonData.getInt("scoreValue");
        goldValue = jsonData.getInt("goldValue");
        slowPercent = jsonData.getFloat("slowPercent");
        currentTerrain = jsonData.getString("currentTerrain");
        terrainSpeedFactor = jsonData.getFloat("terrainSpeedFactor");
        texturePath = jsonData.getString("texturePath");
        textureSize = jsonData.getFloat("textureSize");
        spawnerId = jsonData.getString("spawnerId");
        id = jsonData.getInt("id");
        nextId = jsonData.getInt("nextId");
        slowFactor = jsonData.getFloat("slowFactor", 1f);
        slowDuration = jsonData.getFloat("slowDuration", 0f);
    }
}