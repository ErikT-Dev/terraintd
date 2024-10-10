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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gdx.terraintd.components.Shop;
import com.gdx.terraintd.components.ShopItem;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ShopMenu {
    private final Stage stage;
    private final Shop shop;
    private final BitmapFont largeFont;
    private Table shopMenu;
    private Actor shopMenuBackground;
    private Actor menuCloseDetector;
    private Consumer<ShopItem> onItemSelected;

    public ShopMenu(Stage stage, Shop shop, BitmapFont largeFont) {
        this.stage = stage;
        this.shop = shop;
        this.largeFont = largeFont;
        createShopMenu();
        createMenuCloseDetector();
    }

    private void createShopMenu() {
        float menuWidth = Gdx.graphics.getWidth() / 3f;
        float menuHeight = Gdx.graphics.getHeight();

        shopMenu = new Table();
        shopMenu.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("ui/shop_menu.png"))));
        shopMenu.setVisible(false);
        shopMenu.setSize(menuWidth, menuHeight);
        shopMenu.setPosition(Gdx.graphics.getWidth() - menuWidth, 0);

        TextButton.TextButtonStyle menuButtonStyle = new TextButton.TextButtonStyle();
        menuButtonStyle.font = largeFont;
        menuButtonStyle.fontColor = Color.WHITE;
        menuButtonStyle.downFontColor = Color.LIGHT_GRAY;
        menuButtonStyle.overFontColor = Color.YELLOW;

        List<String> options = shop.getItems().stream()
                .map(ShopItem::getName)
                .collect(Collectors.toList());

        for (String option : options) {
            String optionText = option + " $" + shop.getItemByName(option).cost;
            if(Objects.equals(option, "Grass")){
                optionText = "Grass $2 / Token";
            }
            if(Objects.equals(option, "Forest")){
                optionText = "Forest $2 / Token";
            }
            if(Objects.equals(option, "Sand")){
                optionText = "Sand - Token";
            }
            TextButton button = new TextButton(optionText, menuButtonStyle);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    ShopItem selectedItem = shop.getItemByName(option);
                    if (onItemSelected != null) {
                        onItemSelected.accept(selectedItem);
                    }
                    close();
                    event.stop();
                }
            });
            shopMenu.add(button).pad(10).fillX().expandX();
            shopMenu.row();
        }

        shopMenuBackground = new Actor() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.setColor(0, 0, 0, 0);
                batch.draw(new Texture("ui/pixel.png"),
                        shopMenu.getX(), shopMenu.getY(),
                        shopMenu.getWidth(), shopMenu.getHeight());
            }
        };
        shopMenuBackground.setBounds(shopMenu.getX(), shopMenu.getY(), shopMenu.getWidth(), shopMenu.getHeight());
        shopMenuBackground.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                close();
                return true;
            }
        });
        shopMenuBackground.setVisible(false);

        stage.addActor(shopMenuBackground);
        stage.addActor(shopMenu);
    }

    private void createMenuCloseDetector() {
        menuCloseDetector = new Actor() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                // This actor is invisible
            }
        };
        menuCloseDetector.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        menuCloseDetector.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (shopMenu.isVisible()) {
                    close();
                }
            }
        });
        menuCloseDetector.setVisible(false);
        stage.addActor(menuCloseDetector);
    }

    public void open() {
        if (shopMenuBackground != null) shopMenuBackground.setVisible(true);
        if (shopMenu != null) shopMenu.setVisible(true);
        if (menuCloseDetector != null) {
            menuCloseDetector.setVisible(true);
            menuCloseDetector.toFront();
        }
        if (shopMenu != null) shopMenu.toFront();
    }

    public void close() {
        if (shopMenuBackground != null) shopMenuBackground.setVisible(false);
        if (shopMenu != null) shopMenu.setVisible(false);
        if (menuCloseDetector != null) menuCloseDetector.setVisible(false);
    }

    public void setOnItemSelectedListener(Consumer<ShopItem> listener) {
        this.onItemSelected = listener;
    }
}