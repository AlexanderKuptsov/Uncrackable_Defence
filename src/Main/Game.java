package Main;

import Data.EnemyTank;
import Data.Player;
import Data.WaveManager;
import Graphics.TileGrid;
import Helpers.StateManager;
import Towers.TowerCannon;
import Towers.TowerFlameThrower;
import Towers.TowerIce;
import Towers.TowerType;
import UI.Button;
import UI.UI;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.opengl.Texture;

import static Helpers.Artist.*;
import static Helpers.LevelManager.loadMap;

/**
 * Created by shurik on 02.05.2017.
 */
public class Game {

    private TileGrid grid;
    private Player player;
    private WaveManager waveManager;
    private UI gameUI;
    private Texture menuBackground;

    public static int Cash, Lives;

    private final int NUMBER_OF_WAVES = 3;
    private final int TOWER_PICKER_MENU_X = 1280;
    private final int TOWER_PICKER_MENU_Y = 0;
    private final int TOWER_PICKER_MENU_WIDTH = 192;
    private final int TOWER_PICKER_MENU_HEIGHT = 960;
    private final int MAX_TOWERS_IN_ROW = 2;

    public Game(String mapName) {
        //grid = new TileGrid(map);
        grid = loadMap(mapName);

       /* waveManager = new WaveManager(new Enemy(quickLoad("tankNavy"),
                grid.getTile(0, 5), grid, TILE_SIZE, TILE_SIZE, 55, 80), 3, 6);*/
        waveManager = new WaveManager(new EnemyTank(0, 5, grid), 3, 6);

        player = new Player(grid, waveManager);
        this.menuBackground = quickLoad("menuBackground2");
        setup();
        setupUI();
    }

    private void setup() {
        Cash = 75;
        Lives = 5;
    }

    private void setupUI() {
        gameUI = new UI();

        gameUI.createMenu("TowerPicker", TOWER_PICKER_MENU_X, TOWER_PICKER_MENU_Y,
                TOWER_PICKER_MENU_WIDTH, TOWER_PICKER_MENU_HEIGHT, MAX_TOWERS_IN_ROW, 0);

        gameUI.getMenu("TowerPicker").addButton(
                new Button("TowerIce", quickLoad("Towers/towerIceFull"), 0, 0, TILE_SIZE, TILE_SIZE));
        gameUI.getMenu("TowerPicker").addButton(
                new Button("FlameThrower", quickLoad("Towers/flameThrowerFull"), 0, 0, TILE_SIZE, TILE_SIZE));
        gameUI.getMenu("TowerPicker").addButton(
                new Button("TowerCannonPurple", quickLoad("Towers/towerPurpleFull"), 0, 0, TILE_SIZE, TILE_SIZE));

        gameUI.addButton("Quit", "quit", 1334, 825, 80, 80);
    }

    private void updateUI() {
        gameUI.draw();
        final int TEXT_X = 1310;
        final int TEXT_Y = 625;
        final int TEXT_GAP = 50;
        final int COST_X_DELTA = TILE_SIZE / 8;

        gameUI.drawString(TEXT_X, TEXT_Y, "Lives: " + Lives);
        gameUI.drawString(TEXT_X, TEXT_Y + TEXT_GAP, "Cash: " + Cash + " $");
        gameUI.drawString(TEXT_X, TEXT_Y + TEXT_GAP * 2, "Wave: " + waveManager.getWaveNumber());
        gameUI.drawString(TEXT_X, TEXT_Y + TEXT_GAP * 3, StateManager.framesInLastSecond + " fps");

        gameUI.drawString(gameUI.getMenu("TowerPicker").getButton("TowerIce").getX() + COST_X_DELTA,
                gameUI.getMenu("TowerPicker").getButton("TowerIce").getY() + TILE_SIZE,
                TowerType.CannonIce.getCost() + " $");
        gameUI.drawString(gameUI.getMenu("TowerPicker").getButton("FlameThrower").getX() + COST_X_DELTA,
                gameUI.getMenu("TowerPicker").getButton("FlameThrower").getY() + TILE_SIZE,
                TowerType.FlameThrower.getCost() + " $");
        gameUI.drawString(gameUI.getMenu("TowerPicker").getButton("TowerCannonPurple").getX() + COST_X_DELTA,
                gameUI.getMenu("TowerPicker").getButton("TowerCannonPurple").getY() + TILE_SIZE,
                TowerType.CannonPurple.getCost() + " $");

        if (Mouse.next()) {
            boolean mouseClicked = Mouse.isButtonDown(0);
            if (mouseClicked) {
                if (gameUI.getMenu("TowerPicker").isButtonClicked("TowerIce"))
                    player.pickTower(new TowerIce(TowerType.CannonIce, grid.getTile(0, 0),
                            waveManager.getCurrentWave().getEnemies()));
                if (gameUI.getMenu("TowerPicker").isButtonClicked("FlameThrower"))
                    player.pickTower(new TowerFlameThrower(TowerType.FlameThrower, grid.getTile(0,
                            0), waveManager.getCurrentWave().getEnemies()));
                if (gameUI.getMenu("TowerPicker").isButtonClicked("TowerCannonPurple"))
                    player.pickTower(new TowerCannon(TowerType.CannonPurple, grid.getTile(0, 0),
                            waveManager.getCurrentWave().getEnemies()));

                if (gameUI.isButtonClicked("Quit"))
                    System.exit(0);
            }
        }
        drawQuadTexture(grid.getTile(0, 0).getTexture(), 0, 0, TILE_SIZE, TILE_SIZE);
    }

    private void Restart() {
        StateManager.setState(StateManager.GameState.MAINMENU);
        setup();
        waveManager.restartEnemies();
        player.cleanProjectiles();
        player.getTowerList().clear();
    }


    public void update() {
        drawQuadTexture(menuBackground, TOWER_PICKER_MENU_X, TOWER_PICKER_MENU_Y,
                TOWER_PICKER_MENU_WIDTH, TOWER_PICKER_MENU_HEIGHT);
        grid.draw();
        player.update();
        waveManager.update();
        updateUI();

        if (waveManager.getWaveNumber() > NUMBER_OF_WAVES) {
            System.out.println("Congratulations! You win!");
            Restart();
        }

       /* if (Lives <= 0) {
            System.out.println("Noob! You loose!");
            Restart();
        }*/
    }
}