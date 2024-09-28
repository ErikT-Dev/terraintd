package com.gdx.terraintd.screens_and_ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.gdx.terraintd.logic.TerrainTDGame;

public class LoadingScreen implements Screen {
    private final SpriteBatch batch;
    private final Texture backgroundTexture;
    private final Texture dotTexture;
    private final float[] dotAlphas;
    private float animationTime;

    public LoadingScreen() {
        this.batch = new SpriteBatch();
        this.backgroundTexture = new Texture(Gdx.files.internal("images/loadingscreen.png"));
        this.dotTexture = new Texture(Gdx.files.internal("ui/white_dot.png"));
        this.dotAlphas = new float[]{1f, 0.6f, 0.3f};
        this.animationTime = 0f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        animationTime += delta;
        for (int i = 0; i < dotAlphas.length; i++) {
            dotAlphas[i] = Math.abs((float) Math.sin((animationTime - (i * 0.3f)) * 1.5f));
        }

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        float dotSize = 20f;
        float spacing = 20f;
        float totalWidth = (dotSize * 3) + (spacing * 2);
        float startX = Gdx.graphics.getWidth()*0.92f;
        float y = Gdx.graphics.getHeight() *0.05f;

        for (int i = 0; i < dotAlphas.length; i++) {
            float x = startX + (i * (dotSize + spacing));
            batch.setColor(1, 1, 1, dotAlphas[i]);
            batch.draw(dotTexture, x, y, dotSize, dotSize);
        }

        batch.setColor(Color.WHITE);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        dotTexture.dispose();
    }
}