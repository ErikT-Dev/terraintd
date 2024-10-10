package com.gdx.terraintd.state_and_managers;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class StatsManager implements Json.Serializable {
    public int lives;
    public int score;
    public int gold;
    public int forestTokens;
    public int grassTokens;
    public int sandTokens;
    public int waveNumber;
    public Boolean waveInProgress;

    public StatsManager(int initialLives, int initialScore, int initialGold, int initialForestTokens, int initialGrassTokens, int initialSandTokens, int initialWaveNumber, Boolean initialWaveInProgress) {
        this.forestTokens = initialForestTokens;
        this.grassTokens = initialGrassTokens;
        this.sandTokens = initialSandTokens;
        this.lives = initialLives;
        this.score = initialScore;
        this.gold = initialGold;
        this.waveNumber = initialWaveNumber;
        this.waveInProgress = initialWaveInProgress;
    }
    public StatsManager(){}

    public void toggleWaveInProgress(){waveInProgress = !waveInProgress;}

    public void increaseWaveNumber(){waveNumber++;}

    public void useAForestToken() {
        forestTokens--;
    }

    public void useASandToken() {
        sandTokens--;
    }

    public void useAGrassToken(){
        grassTokens--;}

    public void loseLife() {
        lives--;
    }

    public void gainGold(int goldToGain) {
        gold += goldToGain;
    }

    public boolean canAfford(int cost) {
        return gold >= cost;
    }

    public void spend(int amount) {
        if (canAfford(amount)) {
            gold -= amount;
        }
    }

    public void gainScore(int scoreToGain) {
        score += scoreToGain;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public int getGold() {
        return gold;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    public boolean isGameWon(){return waveNumber>40;}

    @Override
    public void write(Json json) {
        json.writeValue("lives", lives);
        json.writeValue("score", score);
        json.writeValue("gold", gold);
        json.writeValue("forestTokens", forestTokens);
        json.writeValue("grassTokens", grassTokens);
        json.writeValue("sandTokens", sandTokens);
        json.writeValue("waveNumber", waveNumber);
        json.writeValue("waveInProgress", waveInProgress);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        lives = jsonData.getInt("lives");
        score = jsonData.getInt("score");
        gold = jsonData.getInt("gold");
        forestTokens = jsonData.getInt("forestTokens");
        grassTokens = jsonData.getInt("grassTokens");
        sandTokens = jsonData.getInt("sandTokens");
        waveNumber = jsonData.getInt("waveNumber");
        waveInProgress = jsonData.getBoolean("waveInProgress");
    }
}
