package com.gdx.terraintd.screens_and_ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gdx.terraintd.logic.GameConstants;
import com.gdx.terraintd.logic.TerrainTDGame;
import com.gdx.terraintd.state_and_managers.GameSaveManager;
import com.gdx.terraintd.state_and_managers.StatsManager;

public class GameWonScreen implements Screen {
    final TerrainTDGame game;
    Stage stage;
    private final Texture backgroundTexture;
    private final Sprite backgroundSprite;
    private final SpriteBatch batch;

    public GameWonScreen(final TerrainTDGame game, StatsManager statsManager, GameSaveManager gameSaveManager) {
        this.game = game;
        int finalScore = statsManager.score;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        BitmapFont titleFont = GameConstants.getFont(100, Color.CHARTREUSE);
        BitmapFont scoreFont = GameConstants.getFont(80, Color.SKY);
        BitmapFont font = GameConstants.getFont(70, Color.WHITE);

        backgroundTexture = new Texture(Gdx.files.internal("images/gamewonscreen.png"));
        backgroundSprite = new Sprite(backgroundTexture);
        backgroundSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        batch = new SpriteBatch();

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.CHARTREUSE);
        Label.LabelStyle scoreStyle = new Label.LabelStyle(scoreFont, Color.WHITE);

        Label titleLabel = new Label("Game Won!", titleStyle);
        Label scoreLabel = new Label("Score: " + finalScore, scoreStyle);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

        textButtonStyle.font = font;

        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.downFontColor = Color.YELLOW;
        textButtonStyle.overFontColor = Color.YELLOW;

        TextButton mainMenuButton = new TextButton("Main Menu", textButtonStyle);
        TextButton exitButton = new TextButton("Exit", textButtonStyle);

        table.add(titleLabel).padBottom(60).row();
        table.add(scoreLabel).padBottom(60).row();
        table.add(mainMenuButton).padBottom(40).row();
        table.add(exitButton);

        mainMenuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
                gameSaveManager.clearSavedGame();
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0, 0, 1);
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
        stage.dispose();
        backgroundTexture.dispose();
        batch.dispose();
    }
}