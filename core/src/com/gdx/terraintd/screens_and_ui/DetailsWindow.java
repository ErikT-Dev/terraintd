package com.gdx.terraintd.screens_and_ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.gdx.terraintd.components.Tower;
import com.gdx.terraintd.logic.GameConstants;

import java.util.HashMap;
import java.util.Map;

public class DetailsWindow {
    private final Stage stage;
    private final BitmapFont font;
    private Table detailsMenu;
    private Actor detailsWindowBackground;
    private Actor menuCloseDetector;
    private final Tower currentTower;
    private final float clickX;
    private final GameScreen gameScreen;
    private final Map<String, Label> detailLabels;

    public DetailsWindow(Stage stage, Tower tower, float clickX, GameScreen gameScreen) {
        this.stage = stage;
        this.font = GameConstants.getFont(50, Color.WHITE);
        this.currentTower = tower;
        this.clickX = clickX;
        this.gameScreen = gameScreen;
        this.detailLabels = new HashMap<>();
        createDetailsWindow(tower);
        createWindowCloseDetector();
    }

    private void createDetailsWindow(Tower tower) {
        float windowWidth = Gdx.graphics.getWidth() / 2f;
        float windowHeight = Gdx.graphics.getHeight()*5/8f;

        detailsMenu = new Table();
        detailsMenu.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("ui/details_menu.png"))));
        detailsMenu.setSize(windowWidth, windowHeight);

        float windowX = clickX > Gdx.graphics.getWidth() / 2f ? 0 : Gdx.graphics.getWidth() - windowWidth;
        detailsMenu.setPosition(windowX, 0);

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        detailsMenu.add(new Label(tower.towerName, labelStyle)).colspan(2).padBottom(20).row();
        addEffectParagraph(detailsMenu, tower.effect, labelStyle);
        addDetailRow(detailsMenu, "Damage:", String.valueOf(tower.dmg), labelStyle, "damage");
        addDetailRow(detailsMenu, "Attack Speed:", String.format("%.2f", tower.as), labelStyle, "attackSpeed");
        addDetailRow(detailsMenu, "Max dps:", String.format("%.2f", tower.maxDps), labelStyle, "maxDps");
        addDetailRow(detailsMenu, "Range:", String.format("%.2f", tower.range), labelStyle, "range");
        addDetailRow(detailsMenu, "Terrain:", tower.terrain, labelStyle, "terrain");

        detailsWindowBackground = new Actor() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.setColor(0, 0, 0, 0.1f);
                batch.draw(new Texture("ui/pixel.png"),
                        0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        };
        detailsWindowBackground.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        detailsWindowBackground.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                close();
                return true;
            }
        });

        stage.addActor(detailsWindowBackground);
        stage.addActor(detailsMenu);
    }

    private void addEffectParagraph(Table table, String effectText, Label.LabelStyle style) {
        Label effectLabel = new Label(effectText, style);
        effectLabel.setWrap(true);
        effectLabel.setAlignment(Align.left);

        table.add(effectLabel).colspan(2).width(table.getWidth() * 0.9f).align(Align.left).padTop(10).row();
        detailLabels.put("effect", effectLabel);
    }

    private void addDetailRow(Table table, String labelText, String value, Label.LabelStyle style, String key) {
        table.add(new Label(labelText, style)).padRight(10).align(Align.left);
        Label valueLabel = new Label(value, style);
        table.add(valueLabel).align(Align.left).row();
        detailLabels.put(key, valueLabel);
    }

    private void updateDetails() {
        detailLabels.get("damage").setText(String.valueOf(currentTower.dmg));
        detailLabels.get("attackSpeed").setText(String.format("%.2f", currentTower.as));
        detailLabels.get("range").setText(String.format("%.2f", currentTower.range));
        detailLabels.get("maxDps").setText(String.valueOf(currentTower.maxDps));
    }

    public void open() {
        detailsWindowBackground.setVisible(true);
        detailsMenu.setVisible(true);
        menuCloseDetector.setVisible(true);
        menuCloseDetector.toFront();
        detailsMenu.toFront();
    }

    public void close() {
        detailsWindowBackground.setVisible(false);
        detailsMenu.setVisible(false);
        menuCloseDetector.setVisible(false);
        if (currentTower != null) {
            currentTower.isSelected = false;
        }
    }

    private void createWindowCloseDetector() {
        menuCloseDetector = new Actor();
        menuCloseDetector.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        menuCloseDetector.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (detailsMenu.isVisible()) {
                    close();
                }
            }
        });
        stage.addActor(menuCloseDetector);
    }
}