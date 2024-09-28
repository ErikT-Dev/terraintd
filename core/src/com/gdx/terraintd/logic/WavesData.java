package com.gdx.terraintd.logic;

import com.badlogic.gdx.Gdx;
import com.gdx.terraintd.components.Wave;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class WavesData {
    private final List<Wave> waves;

    public WavesData() {
        waves = new ArrayList<>();
        initializeWavesData();
    }

    private void initializeWavesData() {
        int[] waveNumbers  = IntStream.rangeClosed(1, 40).toArray();
        String[] spawnerIds = {"S1", "S2", "S3"};
        for (String spawnerId : spawnerIds) {
            for (int waveNumber : waveNumbers) {
                Wave wave = createWaveFromSpawnerIdAndWaveNumber(spawnerId, waveNumber);
                if (wave != null) {
                    waves.add(wave);
                }
            }
        }
    }

    public Wave createWaveFromSpawnerIdAndWaveNumber(String spawnerId, int waveNumber) {
        Map<String, String> waveData = GameConstants.getSpawnersByWaveNumber(String.valueOf(waveNumber));
        if (waveData == null) {
            Gdx.app.error("WavesData", "Waves data not found for wave number " + waveNumber);
            return null;
        }

        String enemyIdString = "enemy" + spawnerId;
        String countString = "count" + spawnerId;
        String intervalString = "interval" + spawnerId;
        Map<String, String> enemy = GameConstants.getEnemyById(waveData.get(enemyIdString) + waveNumber);

        if (enemy == null) {
            Gdx.app.error("WavesData", "Enemies data not found for wave number " + waveNumber + " and spawner: " + spawnerId);
            return null;
        }
        return new Wave(
                waveNumber,
                spawnerId,
                enemy.get("id"),
                (int) parseFloatSafely(enemy.get("health"), 0f),
                parseFloatSafely(enemy.get("speed"), 0f),
                (int) parseFloatSafely(enemy.get("gold"), 0f),
                (int) parseFloatSafely(enemy.get("score"), 0f),
                "units/" + enemy.get("texPath") + ".png",
                (int) parseFloatSafely(waveData.get(countString), 0f),
                parseFloatSafely(waveData.get(intervalString), 0f)
        );
    }

    public Wave getWaveByWaveNumberAndSpawnerId(String spawnerId, int waveNumber) {
        for (Wave wave : waves) {
            if (wave.waveNumber == waveNumber && Objects.equals(wave.spawnerId, spawnerId)) {
                return wave;
            }
        }
        return null;
    }

    private float parseFloatSafely(String value, float defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            Gdx.app.error("Shop", "Failed to parse float: " + value, e);
            return defaultValue;
        }
    }
}