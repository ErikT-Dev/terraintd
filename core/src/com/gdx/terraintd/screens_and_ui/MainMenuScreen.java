package com.gdx.terraintd.screens_and_ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.terraintd.logic.GameConstants;
import com.gdx.terraintd.logic.TerrainTDGame;
import com.gdx.terraintd.state_and_managers.AppState;
import com.gdx.terraintd.components.GameMap;
import com.gdx.terraintd.state_and_managers.GameSaveManager;
import com.gdx.terraintd.state_and_managers.GameState;

public class MainMenuScreen implements Screen {
    private final TerrainTDGame game;
    private final Stage stage;
    private final Texture backgroundTexture;
    private final Sprite backgroundSprite;
    private final SpriteBatch batch;
    private GameMap selectedMap;
    private TextButton mapSelectButton;
    private boolean isDropdownVisible = false;
    private final GameSaveManager gameSaveManager;
    private AppState appState;
    private final Table mainTable;
    private Table dropdownTable;
    private Table highScoresTable;
    private TextButton highScoresButton;

    public MainMenuScreen(final TerrainTDGame game){
        this.game = game;
        this.gameSaveManager=new GameSaveManager();

        this.appState = gameSaveManager.loadAppSettings();
        if (this.appState == null) {
            this.appState = new AppState();
            this.appState.selectedMap = GameConstants.GAME_MAPS.get(0);
        }
        selectedMap = this.appState.selectedMap;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("images/mainmenu.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();

        BitmapFont menuFont = GameConstants.getFont(70, Color.WHITE);
        Label.LabelStyle titleStyle = new Label.LabelStyle(menuFont, Color.CHARTREUSE);
        Label titleLabel = new Label("TERRAIN TD", titleStyle);

        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = menuFont;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.downFontColor = Color.YELLOW;
        textButtonStyle.overFontColor = Color.YELLOW;
        textButtonStyle.disabledFontColor = Color.LIGHT_GRAY;

        TextButton newGameButton = new TextButton("New Game", textButtonStyle);
        TextButton continueGameButton = new TextButton("Continue Game", textButtonStyle);
        TextButton exitButton = new TextButton("Exit", textButtonStyle);

        createMapSelectButton(textButtonStyle);
        createDropdownMenu(textButtonStyle);

        createHighScoresButton(textButtonStyle);
        createHighScoresTable(textButtonStyle);

        mainTable.add(titleLabel).padBottom(60).row();
        mainTable.add(newGameButton).padBottom(40).row();
        mainTable.add(continueGameButton).padBottom(40).row();
        mainTable.add(mapSelectButton).padBottom(40).row();
        mainTable.add(highScoresButton).padBottom(40).row();
        mainTable.add(exitButton);

        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startNewGame();
            }
        });

        continueGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                continueGame();
            }
        });
        continueGameButton.setDisabled(!gameSaveManager.hasSavedGame());

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

    }

    private void createHighScoresButton(TextButton.TextButtonStyle style) {
        highScoresButton = new TextButton("High Scores", style);
        highScoresButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleHighScores();
            }
        });
    }

    private void createHighScoresTable(TextButton.TextButtonStyle style) {
        highScoresTable = new Table();
        highScoresTable.setFillParent(true);
        highScoresTable.setVisible(false);
        stage.addActor(highScoresTable);

        Table innerTable = new Table();
        for (GameMap map : GameConstants.GAME_MAPS) {
            String mapName = map.getName();
            int score = appState.highScores.get(mapName); // We can safely assume the entry exists
            Label scoreLabel = new Label(mapName + " - " + score + " points", new Label.LabelStyle(style.font, Color.WHITE));
            innerTable.add(scoreLabel).padBottom(20).row();
        }

        TextButton backButton = new TextButton("Back", style);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println(appState.highScores.size());
                toggleHighScores();
            }
        });

        innerTable.add(backButton).padTop(40);
        highScoresTable.add(innerTable);
    }

    private void toggleHighScores() {
        System.out.println("Toggling high scores"); // Add this for debugging
        boolean showHighScores = !highScoresTable.isVisible();
        highScoresTable.setVisible(showHighScores);
        mainTable.setVisible(!showHighScores);

        if (showHighScores) {
            highScoresTable.toFront();
        } else {
            mainTable.toFront();
        }
    }

    private void createMapSelectButton(TextButton.TextButtonStyle style) {
        mapSelectButton = new TextButton("-> "+ selectedMap.getName() + " <-", style);
        mapSelectButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleDropdown();
            }
        });
    }

    private void createDropdownMenu(TextButton.TextButtonStyle style) {
        dropdownTable = new Table();
        dropdownTable.setFillParent(true);
        dropdownTable.setVisible(false);
        stage.addActor(dropdownTable);

        Table innerTable = new Table();
        for (final GameMap map : GameConstants.GAME_MAPS) {
            final TextButton mapOption = new TextButton(getMapButtonText(map), style);
            mapOption.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectedMap = map;
                    appState.selectedMap = map;
                    gameSaveManager.saveAppSettings(appState);
                    updateMapButtonTexts();
                    mapSelectButton.setText(getMapButtonText(map));
                    toggleDropdown();
                }
            });
            innerTable.add(mapOption).padBottom(20).row();
        }

        dropdownTable.add(innerTable);
    }

    private String getMapButtonText(GameMap map) {
        return map.equals(selectedMap) ? "-> " + map.getName() + " <-" : map.getName();
    }

    private void updateMapButtonTexts() {
        for (com.badlogic.gdx.scenes.scene2d.Actor actor : ((Table)dropdownTable.getChild(0)).getChildren()) {
            if (actor instanceof TextButton) {
                TextButton button = (TextButton) actor;
                for (GameMap map : GameConstants.GAME_MAPS) {
                    if (button.getText().toString().contains(map.getName())) {
                        button.setText(getMapButtonText(map));
                        break;
                    }
                }
            }
        }
    }

    private void toggleDropdown() {
        isDropdownVisible = !isDropdownVisible;
        dropdownTable.setVisible(isDropdownVisible);
        mainTable.setVisible(!isDropdownVisible);

        if (isDropdownVisible) {
            dropdownTable.toFront();
        } else {
            mainTable.toFront();
        }
    }

    public void startNewGame() {
        gameSaveManager.clearSavedGame();
        if (game.getScreen() instanceof GameScreen) {
            game.getScreen().dispose();
        }
        GameScreen newGameScreen = new GameScreen(game, selectedMap, null);
        game.setScreen(newGameScreen);
        dispose();
    }

    public void continueGame() {
        try {
            GameState loadedState = gameSaveManager.loadGame();
            if (loadedState != null) {
                GameScreen continuedGameScreen = new GameScreen(game,null, loadedState);
                game.setScreen(continuedGameScreen);
                dispose();
            } else {
                System.out.println("No saved game found!");
            }
        } catch (Exception e) {
            System.err.println("Error loading saved game: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.808f, 0.878f, 0.616f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        backgroundSprite.draw(batch);
        batch.end();

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        backgroundSprite.setSize(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        gameSaveManager.saveAppSettings(appState);
        stage.dispose();
        backgroundTexture.dispose();
        batch.dispose();
    }
}