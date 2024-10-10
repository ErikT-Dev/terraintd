package com.gdx.terraintd.state_and_managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.gdx.terraintd.components.Enemy;
import com.gdx.terraintd.components.GameMap;
import com.gdx.terraintd.components.GridSystem;
import com.gdx.terraintd.components.Pathfinder;
import com.gdx.terraintd.components.Projectile;
import com.gdx.terraintd.components.Tower;
import com.gdx.terraintd.logic.GameConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameSaveManager {
    private static final String GAME_PREF_NAME = "SavedGame";
    private static final String APP_PREF_NAME = "AppSettings";
    private static final String KEY_GAME_STATE = "gameState";
    private static final String KEY_APP_STATE = "appState";
    private final Json json;

    public GameSaveManager() {
        json = new Json();
    }
    public void saveAppSettings(AppState appState) {
        Preferences prefs = Gdx.app.getPreferences(APP_PREF_NAME);
        String appStateJson = json.toJson(appState);
        prefs.putString(KEY_APP_STATE, appStateJson);
        prefs.flush();
    }

    public AppState loadAppSettings() {
        Preferences prefs = Gdx.app.getPreferences(APP_PREF_NAME);
        String appStateJson = prefs.getString(KEY_APP_STATE, null);
        if (appStateJson != null) {
            return json.fromJson(AppState.class, appStateJson);
        }
        return null;
    }

    public void saveGame(GameState gameState) {
        Preferences prefs = Gdx.app.getPreferences(GAME_PREF_NAME);
        String gameStateJson = json.toJson(gameState);
        prefs.putString(KEY_GAME_STATE, gameStateJson);
        prefs.flush();
    }

    public GameState loadGame() {
        Preferences prefs = Gdx.app.getPreferences(GAME_PREF_NAME);
        String gameStateJson = prefs.getString(KEY_GAME_STATE, null);
        if (gameStateJson != null) {
            GameState gameState = json.fromJson(GameState.class, gameStateJson);
            reconnectGameState(gameState);
            return gameState;
        }
        return null;
    }

    public boolean hasSavedGame() {
        Preferences prefs = Gdx.app.getPreferences(GAME_PREF_NAME);
        return prefs.contains(KEY_GAME_STATE);
    }

    public void clearSavedGame() {
        Preferences prefs = Gdx.app.getPreferences(GAME_PREF_NAME);
        prefs.remove(KEY_GAME_STATE);
        prefs.flush();
    }

    private void reconnectGameState(GameState gameState) {
        GridSystem gridSystem = new GridSystem(null, gameState.gridArray);
        GameMap gameMap  = GameConstants.findGameMapByName(gameState.mapName);
        assert gameMap != null;
        Pathfinder S1Pathfinder = new Pathfinder(gameMap.spawner1X, gameMap.spawner1Y, gameMap.endX, gameMap.endY);
        Pathfinder S2Pathfinder = new Pathfinder(gameMap.spawner2X, gameMap.spawner2Y, gameMap.endX, gameMap.endY);
        Pathfinder S3Pathfinder = new Pathfinder(gameMap.spawner3X, gameMap.spawner3Y, gameMap.endX, gameMap.endY);
        gameState.enemySpawner.reconnect(gridSystem, S1Pathfinder, gameState.statsManager);
        gameState.enemySpawner2.reconnect(gridSystem, S2Pathfinder, gameState.statsManager);
        gameState.enemySpawner3.reconnect(gridSystem, S3Pathfinder, gameState.statsManager);
        Map<Integer, Enemy> enemyMap = createEnemyMap(gameState.enemies);
        Map<Integer, Tower> towerMap = createTowerMap(gameState.towers);
        for (Projectile projectile : gameState.projectiles) {
            Enemy target = enemyMap.get(projectile.getTargetId());
            Tower owner = towerMap.get(projectile.getOwnerId());
            projectile.reconnect(target, owner);
        }
    }

    private Map<Integer, Enemy> createEnemyMap(List<Enemy> enemies) {
        Map<Integer, Enemy> map = new HashMap<>();
        for (Enemy enemy : enemies) {
            map.put(enemy.getId(), enemy);
        }
        return map;
    }

    private Map<Integer, Tower> createTowerMap(List<Tower> towers) {
        Map<Integer, Tower> map = new HashMap<>();
        for (Tower tower : towers) {
            map.put(tower.getId(), tower);
        }
        return map;
    }
}