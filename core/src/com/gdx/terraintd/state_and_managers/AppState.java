package com.gdx.terraintd.state_and_managers;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.gdx.terraintd.components.GameMap;
import com.gdx.terraintd.logic.GameConstants;
import java.util.HashMap;
import java.util.Map;

public class AppState implements Json.Serializable {
    public GameMap selectedMap;
    public Map<String, Integer> highScores;

    public AppState() {
        initializeHighScores();
    }


    private void initializeHighScores() {
        highScores = new HashMap<>();
        for (GameMap map : GameConstants.GAME_MAPS) {
            highScores.put(map.getName(), 0);
        }
    }

    public void updateHighScore(String mapName, int score) {
        if (highScores.containsKey(mapName) && score > highScores.get(mapName)) {
            highScores.put(mapName, score);
        }
    }

    @Override
    public void write(Json json) {
        json.writeValue("selectedMap", selectedMap);
        json.writeValue("highScores", highScores);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        selectedMap = json.readValue("selectedMap", GameMap.class, jsonData);
        highScores = json.readValue("highScores", HashMap.class, Integer.class, jsonData);

        for (GameMap map : GameConstants.GAME_MAPS) {
            if (!highScores.containsKey(map.getName())) {
                highScores.put(map.getName(), 0);
            }
        }
    }
}