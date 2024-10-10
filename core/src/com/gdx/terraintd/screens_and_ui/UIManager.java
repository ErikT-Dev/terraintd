package com.gdx.terraintd.screens_and_ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gdx.terraintd.components.ShopItem;
import com.gdx.terraintd.components.Wave;
import com.gdx.terraintd.logic.GameConstants;

import java.util.Objects;

public class UIManager {
    private final GameScreen gameScreen;
    public final Stage stage;
    private final BitmapFont uiFont = GameConstants.getFont(60, Color.WHITE);
    private ImageButton upgradeButton;
    private ImageButton sellButton;
    private ImageButton sendWaveButton;
    private ImageButton undoButton;
    private final int topBarHeight;
    private final ShopMenu shopMenu;
    public ShopItem selectedShopItem;
    public boolean buildMode = false;
    public boolean upgradeMode = false;
    public boolean sellMode = false;
    private final Texture goldIconTexture;
    private final Texture livesIconTexture;
    private final Texture scoreIconTexture;
    private final Texture grassTokenTexture;
    private final Texture forestTokenTexture;
    private final Texture sandTokenTexture;
    private ImageButton buildTowerButton;
    private Texture buildButtonTexture;
    private Texture buildButtonDisabledTexture;
    private Texture buildButtonPressedTexture;
    private Texture cancelBuildButtonTexture;
    private Texture cancelBuildButtonPressedTexture;
    private Texture upgradeButtonTexture;
    private Texture upgradeButtonDisabledTexture;
    private Texture upgradeButtonPressedTexture;
    private Texture cancelUpgradeButtonTexture;
    private Texture cancelUpgradeButtonPressedTexture;
    private Texture undoButtonTexture;
    private Texture undoButtonDisabledTexture;
    private Texture undoButtonPressedTexture;
    private Texture cancelSellButtonTexture;
    private Texture cancelSellButtonPressedTexture;
    private Texture sellButtonTexture;
    private Texture sellButtonDisabledTexture;
    private Texture sellButtonPressedTexture;
    private Texture sendWaveTexture;
    private Texture sendWavePressedTexture;
    private Texture sendWaveButtonDisabledTexture;
    private Texture shopButtonTexture;
    private Texture shopButtonPressedTexture;
    private ImageButton shopButton;
    private Label shopButtonLabel;
    private static final float SHOP_BUTTON_PADDING = 40f;
    private static final float BUTTON_WIDTH = 320;
    private static final float BUTTON_HEIGHT = 128.37f;

    public UIManager(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.topBarHeight = gameScreen.gameInitializer.topBarHeight;
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        goldIconTexture = new Texture(Gdx.files.internal("ui/gold.png"));
        livesIconTexture = new Texture(Gdx.files.internal("ui/lives.png"));
        scoreIconTexture = new Texture(Gdx.files.internal("ui/score.png"));
        grassTokenTexture = new Texture(Gdx.files.internal("ui/grass_token.png"));
        forestTokenTexture = new Texture(Gdx.files.internal("ui/forest_token.png"));
        sandTokenTexture = new Texture(Gdx.files.internal("ui/sand_token.png"));

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = uiFont;
        textButtonStyle.fontColor = Color.WHITE;
        textButtonStyle.downFontColor = Color.LIME;
        textButtonStyle.disabledFontColor = Color.LIGHT_GRAY;

        loadButtonTextures();
        createMainMenuButton(textButtonStyle);
        createUndoButton();
        createBuildTowerButton();
        createUpgradeButton();
        createSellTowersButton();
        createShopButton();
        shopButton.setVisible(false);
        createSendWaveButton();

        shopMenu = new ShopMenu(stage, gameScreen.shop, uiFont);
        shopMenu.setOnItemSelectedListener(this::onShopItemSelected);
    }

    private TextureRegionDrawable createDrawable(Texture texture) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);
        drawable.setMinWidth(BUTTON_WIDTH);
        drawable.setMinHeight(BUTTON_HEIGHT);
        return drawable;
    }

    private ImageButton createButton(ImageButton.ImageButtonStyle style) {
        ImageButton button = new ImageButton(style);
        button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        return button;
    }

    private void loadButtonTextures() {
        sendWaveTexture = new Texture(Gdx.files.internal("ui/send_wave_button.png"));
        sendWavePressedTexture = new Texture(Gdx.files.internal("ui/send_wave_button_pressed.png"));
        sendWaveButtonDisabledTexture = new Texture(Gdx.files.internal("ui/send_wave_button_disabled.png"));
        sellButtonTexture = new Texture(Gdx.files.internal("ui/sell_button.png"));
        sellButtonPressedTexture = new Texture(Gdx.files.internal("ui/sell_button_pressed.png"));
        sellButtonDisabledTexture = new Texture(Gdx.files.internal("ui/sell_button_disabled.png"));
        cancelSellButtonTexture = new Texture(Gdx.files.internal("ui/cancel_sell_button.png"));
        cancelSellButtonPressedTexture = new Texture(Gdx.files.internal("ui/cancel_sell_button_pressed.png"));
        upgradeButtonTexture = new Texture(Gdx.files.internal("ui/upgrade_button.png"));
        upgradeButtonPressedTexture = new Texture(Gdx.files.internal("ui/upgrade_button_pressed.png"));
        upgradeButtonDisabledTexture = new Texture(Gdx.files.internal("ui/upgrade_button_disabled.png"));
        cancelUpgradeButtonTexture = new Texture(Gdx.files.internal("ui/cancel_upgrade_button.png"));
        cancelUpgradeButtonPressedTexture = new Texture(Gdx.files.internal("ui/cancel_upgrade_button_pressed.png"));
        undoButtonTexture = new Texture(Gdx.files.internal("ui/undo_button.png"));
        undoButtonPressedTexture = new Texture(Gdx.files.internal("ui/undo_button_pressed.png"));
        undoButtonDisabledTexture = new Texture(Gdx.files.internal("ui/undo_button_disabled.png"));
        buildButtonTexture = new Texture(Gdx.files.internal("ui/build_button.png"));
        buildButtonDisabledTexture = new Texture(Gdx.files.internal("ui/build_button_disabled.png"));
        buildButtonPressedTexture = new Texture(Gdx.files.internal("ui/build_button_pressed.png"));
        cancelBuildButtonTexture = new Texture(Gdx.files.internal("ui/cancel_build_button.png"));
        cancelBuildButtonPressedTexture = new Texture(Gdx.files.internal("ui/cancel_build_button_pressed.png"));
        shopButtonTexture = new Texture(Gdx.files.internal("ui/shop_button.png"));
        shopButtonPressedTexture = new Texture(Gdx.files.internal("ui/shop_button_pressed.png"));
    }

    private void createMainMenuButton(TextButton.TextButtonStyle textButtonStyle) {
        TextButton mainMenuButton = new TextButton("Main Menu", textButtonStyle);
        mainMenuButton.setPosition(Gdx.graphics.getWidth() - 350, Gdx.graphics.getHeight() - topBarHeight * 1.5f);
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameScreen.exitToMainMenu();
            }
        });
        stage.addActor(mainMenuButton);
    }

    private void createUndoButton() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = createDrawable(undoButtonTexture);
        style.imageDown = createDrawable(undoButtonPressedTexture);
        style.imageDisabled = createDrawable(undoButtonDisabledTexture);
        undoButton = createButton(style);

        undoButton.setPosition(500, 0);
        undoButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!undoButton.isDisabled()) {
                    gameScreen.inputHandler.undo();
                }
            }
        });
        stage.addActor(undoButton);
    }

    public void updateUndoButton(boolean isHistoryEmpty) {
        if (undoButton != null) {
            undoButton.setDisabled(isHistoryEmpty);
        }
    }

    private void createBuildTowerButton() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = createDrawable(buildButtonTexture);
        style.imageDown = createDrawable(buildButtonPressedTexture);
        style.imageDisabled = createDrawable(buildButtonDisabledTexture);

        buildTowerButton = createButton(style);

        buildTowerButton.setPosition(Gdx.graphics.getWidth() - 350, 0);

        buildTowerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!buildTowerButton.isDisabled()) {
                    if (buildMode) {
                        // Change to normal build button
                        style.imageUp = createDrawable(buildButtonTexture);
                        style.imageDown = createDrawable(buildButtonPressedTexture);
                        buildTowerButton.setStyle(style);
                        toggleBuildMode();
                    } else {
                        shopMenu.open();
                    }
                    if (upgradeMode) {
                        toggleUpgradeMode();
                        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                        style.imageUp = createDrawable(upgradeButtonTexture);
                        style.imageDown = createDrawable(upgradeButtonPressedTexture);
                        upgradeButton.setStyle(style);
                    }
                    if (sellMode) {
                        toggleSellMode();
                        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                        style.imageUp = createDrawable(sellButtonTexture);
                        style.imageDown = createDrawable(sellButtonPressedTexture);
                        style.imageDisabled = createDrawable(sellButtonDisabledTexture);
                        sellButton.setStyle(style);
                    }

                }
            }
        });
        stage.addActor(buildTowerButton);
    }

    public void toggleBuildMode() {
        if (buildMode) {
            buildMode = false;
            gameScreen.gridSystem.cleanUpGrid();
            shopButton.setVisible(false);
            upgradeButton.setVisible(true);
        } else {

            gameScreen.gridSystem.cleanUpGrid();
            upgradeButton.setVisible(false);
            buildMode = true;
            gameScreen.gridSystem.displayCriticalCells(
                    gameScreen.enemySpawner.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray),
                    gameScreen.enemySpawner2.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray),
                    gameScreen.enemySpawner3.pathfinder.findCriticalNodes(gameScreen.gridSystem.gridArray));
            shopButton.setVisible(true);
        }
    }

    private void createUpgradeButton() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = createDrawable(upgradeButtonTexture);
        style.imageDown = createDrawable(upgradeButtonPressedTexture);
        style.imageDisabled = createDrawable(upgradeButtonDisabledTexture);
        upgradeButton = createButton(style);

        upgradeButton.setPosition(Gdx.graphics.getWidth() - 2 * BUTTON_WIDTH - 100, 0);
        upgradeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleUpgradeMode();
                if (buildMode) {
                    toggleBuildMode();
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                    style.imageUp = createDrawable(buildButtonTexture);
                    style.imageDown = createDrawable(buildButtonPressedTexture);
                    style.imageDisabled = createDrawable(buildButtonDisabledTexture);
                    buildTowerButton.setStyle(style);
                }
                if (sellMode) {
                    toggleSellMode();
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                    style.imageUp = createDrawable(sellButtonTexture);
                    style.imageDown = createDrawable(sellButtonPressedTexture);
                    style.imageDisabled = createDrawable(sellButtonDisabledTexture);
                    sellButton.setStyle(style);
                }
            }
        });
        stage.addActor(upgradeButton);
    }

    public void toggleUpgradeMode() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        if (upgradeMode) {
            style.imageUp = createDrawable(upgradeButtonTexture);
            style.imageDown = createDrawable(upgradeButtonPressedTexture);
            upgradeMode = false;
        } else {
            style.imageUp = createDrawable(cancelUpgradeButtonTexture);
            style.imageDown = createDrawable(cancelUpgradeButtonPressedTexture);
            upgradeMode = true;
        }
        upgradeButton.setStyle(style);
    }

    private void createSellTowersButton() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = createDrawable(sellButtonTexture);
        style.imageDown = createDrawable(sellButtonPressedTexture);
        style.imageDisabled = createDrawable(sellButtonDisabledTexture);
        sellButton = createButton(style);

        sellButton.setPosition(Gdx.graphics.getWidth() - 1200, 0);
        sellButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!sellButton.isDisabled()) {
                    toggleSellMode();
                    if (buildMode) {
                        toggleBuildMode();
                        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                        style.imageUp = createDrawable(buildButtonTexture);
                        style.imageDown = createDrawable(buildButtonPressedTexture);
                        style.imageDisabled = createDrawable(buildButtonDisabledTexture);
                        buildTowerButton.setStyle(style);
                    }
                    if (upgradeMode) {
                        toggleUpgradeMode();
                        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                        style.imageUp = createDrawable(upgradeButtonTexture);
                        style.imageDown = createDrawable(upgradeButtonPressedTexture);
                        upgradeButton.setStyle(style);
                    }
                }
            }
        });
        stage.addActor(sellButton);
    }

    public void toggleSellMode() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        if (sellMode) {
            style.imageUp = createDrawable(sellButtonTexture);
            style.imageDown = createDrawable(sellButtonPressedTexture);
            sellMode = false;
        } else {
            style.imageUp = createDrawable(cancelSellButtonTexture);
            style.imageDown = createDrawable(cancelSellButtonPressedTexture);
            sellMode = true;
        }
        sellButton.setStyle(style);
    }

    private void createSendWaveButton() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = createDrawable(sendWaveTexture);
        style.imageDown = createDrawable(sendWavePressedTexture);
        style.imageDisabled = createDrawable(sendWaveButtonDisabledTexture);
        sendWaveButton = createButton(style);

        sendWaveButton.setPosition(50, 0);
        sendWaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (buildMode) {
                    toggleBuildMode();
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                    style.imageUp = createDrawable(buildButtonTexture);
                    style.imageDown = createDrawable(buildButtonPressedTexture);
                    style.imageDisabled = createDrawable(buildButtonDisabledTexture);
                    buildTowerButton.setStyle(style);
                }
                if (upgradeMode) {
                    toggleUpgradeMode();
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                    style.imageUp = createDrawable(upgradeButtonTexture);
                    style.imageDown = createDrawable(upgradeButtonPressedTexture);
                    upgradeButton.setStyle(style);
                }
                if (sellMode) {
                    toggleSellMode();
                    ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
                    style.imageUp = createDrawable(sellButtonTexture);
                    style.imageDown = createDrawable(sellButtonPressedTexture);
                    style.imageDisabled = createDrawable(sellButtonDisabledTexture);
                    sellButton.setStyle(style);
                }
                if (!gameScreen.statsManager.waveInProgress) {
                    gameScreen.statsManager.increaseWaveNumber();
                    if (gameScreen.statsManager.isGameWon()) {
                        gameScreen.winGame();
                        return;
                    }
                    gameScreen.statsManager.toggleWaveInProgress();

                    int waveToSend = gameScreen.statsManager.waveNumber;

                    Wave waveOfSpawner1 = gameScreen.wavesData.getWaveByWaveNumberAndSpawnerId("S1", waveToSend);
                    Wave waveOfSpawner2 = gameScreen.wavesData.getWaveByWaveNumberAndSpawnerId("S2", waveToSend);
                    Wave waveOfSpawner3 = gameScreen.wavesData.getWaveByWaveNumberAndSpawnerId("S3", waveToSend);

                    gameScreen.enemySpawner.startNextWave(gameScreen.gridSystem.gridArray, waveOfSpawner1);
                    gameScreen.enemySpawner2.startNextWave(gameScreen.gridSystem.gridArray, waveOfSpawner2);
                    gameScreen.enemySpawner3.startNextWave(gameScreen.gridSystem.gridArray, waveOfSpawner3);

                    gameScreen.inputHandler.clearCommandHistory();
                }
            }
        });
        stage.addActor(sendWaveButton);
    }

    public void updateSendWaveButton(boolean waveInProgress) {
        sendWaveButton.setDisabled(waveInProgress);
        buildTowerButton.setDisabled(waveInProgress);
        sellButton.setDisabled(waveInProgress);
    }

    private void createShopButton() {
        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(shopButtonTexture);
        style.imageDown = new TextureRegionDrawable(shopButtonPressedTexture);

        shopButton = new ImageButton(style);

        Label.LabelStyle labelStyle = new Label.LabelStyle(uiFont, Color.WHITE);
        shopButtonLabel = new Label("Select Building", labelStyle);

        float buttonWidth = shopButtonLabel.getPrefWidth() + 100f;

        shopButton.setSize(buttonWidth, BUTTON_HEIGHT);

        setDrawableSize((TextureRegionDrawable) style.imageUp, buttonWidth, BUTTON_HEIGHT);
        setDrawableSize((TextureRegionDrawable) style.imageDown, buttonWidth, BUTTON_HEIGHT);

        Table buttonTable = new Table();
        buttonTable.add(shopButtonLabel);
        buttonTable.setFillParent(true);
        buttonTable.center();

        shopButton.addActor(buttonTable);

        float buttonX = Gdx.graphics.getWidth() - 1.5f * BUTTON_WIDTH - 0.5f * buttonWidth - 100;
        float buttonY = 0;
        shopButton.setPosition(buttonX, buttonY);

        shopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                shopMenu.open();
                buildMode = false;
            }
        });
        stage.addActor(shopButton);
    }

    private void setDrawableSize(TextureRegionDrawable drawable, float width, float height) {
        drawable.setMinWidth(width);
        drawable.setMinHeight(height);
    }

    private void onShopItemSelected(ShopItem item) {
        selectedShopItem = item;
        String newText = item.getName() + " $" + item.cost;
        if (Objects.equals(selectedShopItem.name, "Grass")) {
            newText = "Grass $2 / T";
        }
        if (Objects.equals(selectedShopItem.name, "Forest")) {
            newText = "Forest $2 / T";
        }
        if (Objects.equals(selectedShopItem.name, "Sand")) {
            newText = "Sand - T";
        }
        shopButtonLabel.setText(newText);

        float newButtonWidth = shopButtonLabel.getPrefWidth() + 100f;
        shopButton.setSize(newButtonWidth, BUTTON_HEIGHT);

        setDrawableSize((TextureRegionDrawable) shopButton.getStyle().imageUp, newButtonWidth, BUTTON_HEIGHT);
        setDrawableSize((TextureRegionDrawable) shopButton.getStyle().imageDown, newButtonWidth, BUTTON_HEIGHT);

        float newButtonX = Gdx.graphics.getWidth() - 1.5f * BUTTON_WIDTH - 0.5f * newButtonWidth - 100;
        shopButton.setPosition(newButtonX, shopButton.getY());

        toggleBuildMode();

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = createDrawable(cancelBuildButtonTexture);
        style.imageDown = createDrawable(cancelBuildButtonPressedTexture);
        buildTowerButton.setStyle(style);
    }

    public void drawUI(SpriteBatch batch) {
        int screenWidth = (int) (Gdx.graphics.getWidth() / 1.4f);
        int sectionWidth = screenWidth / 6;
        int y = (int) (Gdx.graphics.getHeight() - topBarHeight);
        int padding = 10;

        float textY = y + uiFont.getCapHeight() / 2;
        float iconSize = uiFont.getCapHeight() * 2.0f;
        float iconY = textY - iconSize + uiFont.getCapHeight() / 2;

        String scoreText = String.valueOf(gameScreen.statsManager.getScore());
        float scoreIconSize = uiFont.getCapHeight() * 2.2f;
        float scoreWidth = uiFont.draw(batch, scoreText, 0, 0).width;
        float scoreIconX = sectionWidth * 0.5f - (scoreWidth + scoreIconSize + padding) / 2f;
        batch.draw(scoreIconTexture, scoreIconX, iconY, scoreIconSize, scoreIconSize);
        uiFont.draw(batch, scoreText, scoreIconX + scoreIconSize + padding, textY);

        String livesText = String.valueOf(gameScreen.statsManager.getLives());
        float livesWidth = uiFont.draw(batch, livesText, 0, 0).width;
        float livesIconX = sectionWidth * 1.5f - (livesWidth + iconSize + padding) / 2f;
        batch.draw(livesIconTexture, livesIconX, iconY, iconSize, iconSize);
        uiFont.draw(batch, livesText, livesIconX + iconSize + padding, textY);

        String goldText = String.valueOf(gameScreen.statsManager.gold);
        float goldWidth = uiFont.draw(batch, goldText, 0, 0).width;
        float goldIconX = sectionWidth * 2.5f - (goldWidth + iconSize + padding) / 2f;
        batch.draw(goldIconTexture, goldIconX, iconY, iconSize, iconSize);
        uiFont.draw(batch, goldText, goldIconX + iconSize + padding, textY);

        String waveText = "Wave: " + gameScreen.statsManager.waveNumber;
        float waveWidth = uiFont.draw(batch, waveText, 0, 0).width;
        uiFont.draw(batch, waveText, sectionWidth * 3.5f - waveWidth / 2f, textY);

        String tokensText = "Tokens:";
        float tokensTextWidth = uiFont.draw(batch, tokensText, 0, 0).width;
        uiFont.draw(batch, tokensText, sectionWidth * 4.8f - tokensTextWidth / 2f, textY);

        String grassTokensText = String.valueOf(gameScreen.statsManager.grassTokens);
        float grassTokensWidth = uiFont.draw(batch, grassTokensText, 0, 0).width;
        float grassTokenX = sectionWidth * 5.5f - (grassTokensWidth + iconSize + padding) / 2f;
        batch.draw(grassTokenTexture, grassTokenX, iconY, iconSize, iconSize);
        uiFont.draw(batch, grassTokensText, grassTokenX + iconSize + padding, textY);

        String forestTokensText = String.valueOf(gameScreen.statsManager.forestTokens);
        float forestTokensWidth = uiFont.draw(batch, forestTokensText, 0, 0).width;
        float forestTokenX = sectionWidth * 6.0f - (forestTokensWidth + iconSize + padding) / 2f;
        batch.draw(forestTokenTexture, forestTokenX, iconY, iconSize, iconSize);
        uiFont.draw(batch, forestTokensText, forestTokenX + iconSize + padding, textY);

        String sandTokensText = String.valueOf(gameScreen.statsManager.sandTokens);
        float sandTokensWidth = uiFont.draw(batch, sandTokensText, 0, 0).width;
        float sandTokenX = sectionWidth * 6.5f - (sandTokensWidth + iconSize + padding) / 2f;
        batch.draw(sandTokenTexture, sandTokenX, iconY, iconSize, iconSize);
        uiFont.draw(batch, sandTokensText, sandTokenX + iconSize + padding, textY);
    }

    public void draw() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        goldIconTexture.dispose();
        livesIconTexture.dispose();
        buildButtonTexture.dispose();
        buildButtonDisabledTexture.dispose();
        buildButtonPressedTexture.dispose();
        cancelBuildButtonTexture.dispose();
        cancelBuildButtonPressedTexture.dispose();
    }
}