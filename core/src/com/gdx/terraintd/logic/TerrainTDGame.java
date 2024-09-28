package com.gdx.terraintd.logic;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gdx.terraintd.screens_and_ui.GameScreen;
import com.gdx.terraintd.screens_and_ui.LoadingScreen;
import com.gdx.terraintd.screens_and_ui.MainMenuScreen;
import com.gdx.terraintd.state_and_managers.AppState;
import com.gdx.terraintd.state_and_managers.GameSaveManager;
import com.gdx.terraintd.state_and_managers.GameState;

public class TerrainTDGame extends Game {
	public SpriteBatch batch;
	private GameScreen currentGameScreen;
	private GameSaveManager gameSaveManager;
	private AppState appState;

	@Override
	public void create() {
		batch = new SpriteBatch();
		setScreen(new LoadingScreen());

		new Thread(new Runnable() {
			@Override
			public void run() {
				GameConstants.initializeFonts();
				GameConstants.loadGameData();
				gameSaveManager = new GameSaveManager();

				Gdx.app.postRunnable(new Runnable() {
					@Override
					public void run() {
						setScreen(new MainMenuScreen(TerrainTDGame.this));
					}
				});
			}
		}).start();
	}

	@Override
	public void pause() {
		super.pause();
		if (currentGameScreen != null) {
			GameState currentGameState = currentGameScreen.createGameState();
			gameSaveManager.saveGame(currentGameState);

			int currentScore = currentGameScreen.statsManager.getScore();
			String currentMapName = currentGameScreen.mapName;

			if (currentMapName != null) {
				getAppState().updateHighScore(currentMapName, currentScore);
				gameSaveManager.saveAppSettings(appState);
			}
		}
	}

	public void setScreen(Screen screen) {
		super.setScreen(screen);
		if (screen instanceof GameScreen) {
			currentGameScreen = (GameScreen) screen;
		} else {
			currentGameScreen = null;
		}
	}

	public GameSaveManager getGameSaveManager() {
		return gameSaveManager;
	}

	public AppState getAppState() {
		if (appState == null) {
			appState = gameSaveManager.loadAppSettings();
			if (appState == null) {
				appState = new AppState();
			}
		}
		return appState;
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		GameConstants.dispose();
		batch.dispose();
		if (getScreen() != null) {
			getScreen().dispose();
		}
		super.dispose();
	}
}
