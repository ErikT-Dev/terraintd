package com.gdx.terraintd.state_and_managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gdx.terraintd.components.EnemySpawner;
import com.gdx.terraintd.components.GameMap;
import com.gdx.terraintd.components.GridSystem;
import com.gdx.terraintd.components.Pathfinder;
import com.gdx.terraintd.components.Shop;
import com.gdx.terraintd.logic.WavesData;
import com.gdx.terraintd.logic.GameConstants;
import com.gdx.terraintd.screens_and_ui.GameScreen;

import java.util.ArrayList;

public class GameInitializer {
    private final GameScreen gameScreen;
    public int topBarHeight;
    public GameInitializer(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void initializeGame(GameState loadedState) {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        this.topBarHeight = (int) (Gdx.graphics.getHeight() * GameConstants.VERTICAL_PADDING_PERCENT) / 2;
        gameScreen.camera = new OrthographicCamera();
        gameScreen.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameScreen.shapeRenderer = new ShapeRenderer();

        if (loadedState == null) {
            GameMap gameMap  = gameScreen.map;
            Pathfinder S1Pathfinder = new Pathfinder(gameMap.spawner1X, gameMap.spawner1Y, gameMap.endX, gameMap.endY);
            Pathfinder S2Pathfinder = new Pathfinder(gameMap.spawner2X, gameMap.spawner2Y, gameMap.endX, gameMap.endY);
            Pathfinder S3Pathfinder = new Pathfinder(gameMap.spawner3X, gameMap.spawner3Y, gameMap.endX, gameMap.endY);
            gameScreen.gridSystem = new GridSystem(gameScreen.map, null);
            gameScreen.statsManager = new StatsManager(10, 0, 12, gameScreen.map.forestTokens, gameScreen.map.grassTokens,0, 0, false);
            gameScreen.towers = new ArrayList<>();
            gameScreen.enemies = new ArrayList<>();
            gameScreen.projectiles = new ArrayList<>();
            gameScreen.enemySpawner = new EnemySpawner(gameScreen.statsManager, gameScreen.gridSystem, S1Pathfinder, "S1");
            gameScreen.enemySpawner2 = new EnemySpawner(gameScreen.statsManager, gameScreen.gridSystem, S2Pathfinder, "S2");
            gameScreen.enemySpawner3 = new EnemySpawner(gameScreen.statsManager, gameScreen.gridSystem, S3Pathfinder, "S3");
        } else {
            gameScreen.gridSystem = new GridSystem(null, loadedState.gridArray);
            gameScreen.statsManager = loadedState.statsManager;
            gameScreen.towers = loadedState.towers;
            gameScreen.enemies = loadedState.enemies;
            gameScreen.projectiles = loadedState.projectiles;
            gameScreen.enemySpawner = loadedState.enemySpawner;
            gameScreen.enemySpawner2 = loadedState.enemySpawner2;
            gameScreen.enemySpawner3 = loadedState.enemySpawner3;
        }
        gameScreen.shop = new Shop();
        gameScreen.wavesData = new WavesData();
    }

    public void resize(int width, int height) {
        gameScreen.camera.viewportWidth = width;
        gameScreen.camera.viewportHeight = height;
        gameScreen.camera.position.set(width / 2f, height / 2f, 0);
        gameScreen.camera.update();
        gameScreen.uiManager.resize(width, height);
        this.topBarHeight = (int) (height * GameConstants.VERTICAL_PADDING_PERCENT) / 2;
    }

    public void dispose() {
        gameScreen.shapeRenderer.dispose();
    }
}
