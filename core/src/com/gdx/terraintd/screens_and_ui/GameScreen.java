package com.gdx.terraintd.screens_and_ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.gdx.terraintd.components.Enemy;
import com.gdx.terraintd.components.EnemySpawner;
import com.gdx.terraintd.components.GridSystem;
import com.gdx.terraintd.components.Projectile;
import com.gdx.terraintd.components.Shop;
import com.gdx.terraintd.components.Tower;
import com.gdx.terraintd.logic.WavesData;
import com.gdx.terraintd.logic.GameConstants;
import com.gdx.terraintd.state_and_managers.GameRenderer;
import com.gdx.terraintd.state_and_managers.GameUpdater;
import com.gdx.terraintd.logic.InputHandler;
import com.gdx.terraintd.logic.TerrainTDGame;
import com.gdx.terraintd.state_and_managers.GameInitializer;
import com.gdx.terraintd.state_and_managers.TextureManager;
import com.gdx.terraintd.state_and_managers.AppState;
import com.gdx.terraintd.state_and_managers.CollisionHandler;
import com.gdx.terraintd.components.GameMap;
import com.gdx.terraintd.state_and_managers.GameSaveManager;
import com.gdx.terraintd.state_and_managers.GameState;
import com.gdx.terraintd.state_and_managers.StatsManager;

import java.util.List;

public class GameScreen implements Screen {
    public final TerrainTDGame game;
    public OrthographicCamera camera;
    public ShapeRenderer shapeRenderer;
    public GameInitializer gameInitializer;
    public GameRenderer gameRenderer;
    public GameUpdater gameUpdater;
    public InputHandler inputHandler;
    public UIManager uiManager;
    public GridSystem gridSystem;
    public EnemySpawner enemySpawner;
    public EnemySpawner enemySpawner2;
    public EnemySpawner enemySpawner3;
    public List<Enemy> enemies;
    public List<Tower> towers;
    public List<Projectile> projectiles;
    public StatsManager statsManager;
    public CollisionHandler collisionHandler;
    public Shop shop;
    private final Texture backgroundTexture;
    private final Sprite backgroundSprite;
    private final SpriteBatch batch;
    public GameMap map;
    public GameSaveManager gameSaveManager;
    public AppState appState;
    public WavesData wavesData;
    public TextureManager textureManager;
    public String mapName;

    public GameScreen(final TerrainTDGame game, final GameMap map, GameState loadedState) {
        this.game = game;
        this.map = map;
        if(map == null){
            this.mapName = loadedState.mapName;
            this.map = GameConstants.findGameMapByName(this.mapName);
        } else {
            mapName = map.name;
        }
        this.gameSaveManager = game.getGameSaveManager();
        this.appState = game.getAppState();
        textureManager = new TextureManager();
        gameInitializer = new GameInitializer(this);
        gameInitializer.initializeGame(loadedState);
        gameRenderer = new GameRenderer(this);
        gameUpdater = new GameUpdater(this);
        uiManager = new UIManager(this);
        inputHandler = new InputHandler(this);
        collisionHandler = new CollisionHandler(this);
        backgroundTexture = new Texture(Gdx.files.internal("images/gamescreen.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch = new SpriteBatch();
    }

    public GameState createGameState() {
        GameState gameState = new GameState();
        gameState.statsManager = statsManager;
        gameState.gridArray = gridSystem.gridArray;
        gameState.towers = towers;
        gameState.enemies = enemies;
        gameState.projectiles = projectiles;
        gameState.enemySpawner = enemySpawner;
        gameState.enemySpawner2 = enemySpawner2;
        gameState.enemySpawner3 = enemySpawner3;
        gameState.mapName = mapName;
        return gameState;
    }

    public void exitToMainMenu() {
        GameState currentGameState = createGameState();
        gameSaveManager.saveGame(currentGameState);

        int currentScore = statsManager.getScore();
        String currentMapName = mapName;

        if (currentMapName != null) {
            appState.updateHighScore(currentMapName, currentScore);
            appState.selectedMap = map;
            gameSaveManager.saveAppSettings(appState);
        }

        game.setScreen(new MainMenuScreen(game));
        dispose();
    }

    public void loseGame() {
        if (appState == null) {
            appState = gameSaveManager.loadAppSettings();
            if (appState == null) {
                appState = new AppState();
            }
        }

        int currentScore = statsManager.getScore();
        String currentMapName = mapName;

        if (currentMapName != null) {
            appState.updateHighScore(currentMapName, currentScore);
            gameSaveManager.saveAppSettings(appState);
        }

       /* GameState currentGameState = createGameState();
        gameSaveManager.saveGame(currentGameState);*/

        gameSaveManager.clearSavedGame();

        game.setScreen(new GameOverScreen(game, statsManager, gameSaveManager));
        dispose();
    }

    public void winGame() {
        if (appState == null) {
            appState = gameSaveManager.loadAppSettings();
            if (appState == null) {
                appState = new AppState();
            }
        }

        int currentScore = statsManager.getScore();
        String currentMapName = mapName;

        if (currentMapName != null) {
            appState.updateHighScore(currentMapName, currentScore);
            gameSaveManager.saveAppSettings(appState);
        }
       /* GameState currentGameState = createGameState();
        gameSaveManager.saveGame(currentGameState);*/

        gameSaveManager.clearSavedGame();

        game.setScreen(new GameWonScreen(game, statsManager, gameSaveManager));
        dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.183f, 0.349f, 0.239f, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        backgroundSprite.draw(batch);
        batch.end();

        gameUpdater.update(delta);
        gameRenderer.render();

        game.batch.begin();
        uiManager.drawUI(game.batch);
        game.batch.end();

        uiManager.draw();
        inputHandler.handleInput();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {
        gameInitializer.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        gameInitializer.dispose();
        backgroundTexture.dispose();
        uiManager.dispose();
        gameRenderer.dispose();
        //textureManager.dispose();
    }
}
