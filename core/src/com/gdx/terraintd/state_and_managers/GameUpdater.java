package com.gdx.terraintd.state_and_managers;

import com.gdx.terraintd.components.Enemy;
import com.gdx.terraintd.components.Projectile;
import com.gdx.terraintd.components.Tower;
import com.gdx.terraintd.screens_and_ui.GameScreen;

public class GameUpdater {
    private final GameScreen gameScreen;

    public GameUpdater(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void update(float delta) {
        gameScreen.enemySpawner.update(delta);
        if (gameScreen.enemySpawner.canSpawnEnemy()) {
            Enemy newEnemy = gameScreen.enemySpawner.spawnEnemy();
            if (newEnemy != null) {
                gameScreen.enemies.add(newEnemy);
            }
        }

        gameScreen.enemySpawner2.update(delta);
        if (gameScreen.enemySpawner2.canSpawnEnemy()) {
            Enemy newEnemy = gameScreen.enemySpawner2.spawnEnemy();
            if (newEnemy != null) {
                gameScreen.enemies.add(newEnemy);
            }
        }

        gameScreen.enemySpawner3.update(delta);
        if (gameScreen.enemySpawner3.canSpawnEnemy()) {
            Enemy newEnemy = gameScreen.enemySpawner3.spawnEnemy();
            if (newEnemy != null) {
                gameScreen.enemies.add(newEnemy);
            }
        }

        for (Enemy enemy : gameScreen.enemies) {
            enemy.update(delta);
        }

        if(gameScreen.towers !=null){
            for (Tower tower : gameScreen.towers) {
                tower.update(delta);
                if (tower.canFire()) {
                    Enemy target = tower.findNearestEnemy(gameScreen.enemies);
                    if (target != null) {
                        tower.fire();
                        Projectile projectile = tower.addProjectile(target);
                        gameScreen.projectiles.add(projectile);
                        tower.timeSinceLastShot = 0f;
                    }
                }
            }
        }

        for (int i = gameScreen.projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = gameScreen.projectiles.get(i);
            projectile.update(delta);
        }

        gameScreen.uiManager.updateSendWaveButton(gameScreen.statsManager.waveInProgress);
        gameScreen.collisionHandler.handleCollisions();
        gameScreen.collisionHandler.handleEnemyRemoval();
    }
}