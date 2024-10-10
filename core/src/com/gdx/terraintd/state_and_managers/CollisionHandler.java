package com.gdx.terraintd.state_and_managers;

import com.badlogic.gdx.math.Vector2;
import com.gdx.terraintd.components.Enemy;
import com.gdx.terraintd.components.Projectile;
import com.gdx.terraintd.screens_and_ui.GameScreen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CollisionHandler {
    private final GameScreen gameScreen;

    public CollisionHandler(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void handleCollisions() {
        Iterator<Projectile> projectileIterator = gameScreen.projectiles.iterator();
        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            if (projectile.hasReachedTarget()) {
                projectileIterator.remove();
                Enemy target = projectile.target;
                applyDamage(target, projectile.owner.dmg);

                if (projectile.owner.towerName.equals("Bastion")) {
                    applyCatapultAoEDamage(target, projectile.owner.dmg);
                }

                if (projectile.owner.towerName.equals("Alchemist")) {
                    target.applySlowEffect(0.5f, 1f);
                }
            }
        }
    }

    private void applyDamage(Enemy enemy, int damage) {
        enemy.health -= damage;
    }

    private void applyCatapultAoEDamage(Enemy primaryTarget, int damage) {
        float aoERange = 150f;
        Vector2 targetPos = primaryTarget.getDrawPosition();
        List<Enemy> nearbyEnemies = new ArrayList<>();

        for (Enemy enemy : gameScreen.enemies) {
            if (enemy != primaryTarget) {
                float distance = enemy.getDrawPosition().dst(targetPos);
                if (distance <= aoERange) {
                    nearbyEnemies.add(enemy);
                }
            }
        }

        nearbyEnemies.sort(Comparator.comparingDouble(e -> e.getDrawPosition().dst(targetPos)));

        int additionalTargets = Math.min(nearbyEnemies.size(), 4);
        for (int i = 0; i < additionalTargets; i++) {
            applyDamage(nearbyEnemies.get(i), damage);
        }
    }

    public void handleEnemyRemoval() {
        Iterator<Enemy> enemyIterator = gameScreen.enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.hasDied() || enemy.hasReachedEndPoint()) {
                enemyIterator.remove();
                if (enemy.hasDied()) {
                    gameScreen.statsManager.gainGold(enemy.goldValue);
                    gameScreen.statsManager.gainScore(enemy.scoreValue);
                }
                if (enemy.hasReachedEndPoint()) {
                    gameScreen.statsManager.loseLife();
                    if (gameScreen.statsManager.isGameOver()) {
                        gameScreen.loseGame();
                    }
                }

                boolean allSpawnersCompleted = gameScreen.enemySpawner.hasCompletedSpawningEnemies() &&
                        gameScreen.enemySpawner2.hasCompletedSpawningEnemies() &&
                        gameScreen.enemySpawner3.hasCompletedSpawningEnemies();
                boolean allEnemiesDead = gameScreen.enemies.stream().allMatch(Enemy::hasDied);
                if (allSpawnersCompleted && allEnemiesDead  && !enemyIterator.hasNext()) {
                    gameScreen.statsManager.gainGold(2);
                    if(gameScreen.statsManager.waveNumber%2==0){
                        gameScreen.statsManager.forestTokens++;
                    }else {
                        gameScreen.statsManager.grassTokens++;
                    }
                    gameScreen.statsManager.toggleWaveInProgress();
                }
            }
        }
    }
}

