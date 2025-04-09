package fr.ubx.poo.ubgarden.game.launcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import fr.ubx.poo.ubgarden.game.*;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class GameLauncher {

    private GameLauncher() {
    }

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        // Load parameters
        int waspMoveFrequency = integerProperty(properties, "waspMoveFrequency", 2);
        int hornetMoveFrequency = integerProperty(properties, "hornetMoveFrequency", 1);

        int gardenerEnergy = integerProperty(properties, "gardenerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        long energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 1_000);
        long diseaseDuration = integerProperty(properties, "diseaseDuration", 5_000);

        return new Configuration(gardenerEnergy, energyBoost, energyRecoverDuration, diseaseDuration, waspMoveFrequency, hornetMoveFrequency);
    }

    public Game load(File file) throws IOException {


            System.out.println("Loading file: " + file.getAbsolutePath());

            List<String> lines = java.nio.file.Files.readAllLines(file.toPath())
                    .stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();



            int height = lines.size();
            int width = lines.get(0).length();


            for (String line : lines) {
                if (line.length() != width) {
                    throw new RuntimeException("Inconsistent line length in map file");
                }
            }

        MapLevel mapLevel = new MapLevel(width, height);

        for (int j = 0; j < height; j++) {
            String line = lines.get(j);
            for (int i = 0; i < width; i++) {
                char c = line.charAt(i);
                MapEntity entity = MapEntity.fromCode(c);
                mapLevel.set(i, j, entity);
            }
        }

            Properties emptyConfig = new Properties();
            Position gardenerPosition = mapLevel.getGardenerPosition();
            Position waspPosition = mapLevel.getwaspPosition();
            Position hornetPosition = mapLevel.gethornetPosition();
            if (gardenerPosition == null)
                throw new RuntimeException("Gardener not found");
            if (waspPosition == null)
                throw new RuntimeException("wasp not found");
            if (hornetPosition == null)
                throw new RuntimeException("hornet not found");
            Configuration configuration = getConfiguration(emptyConfig);
            World world = new World(1);
            Game game = new Game(world, configuration, gardenerPosition,waspPosition,hornetPosition);
            Map level = new Level(game, 1, mapLevel);
            world.put(1, level);
            return game;
    }



    public Game load() throws RuntimeException {
        Properties emptyConfig = new Properties();
        MapLevel mapLevel = new MapLevelDefaultStart();
        Position gardenerPosition = mapLevel.getGardenerPosition();
        Position waspPosition = mapLevel.getwaspPosition();
        Position hornetPosition = mapLevel.gethornetPosition();
        if (gardenerPosition == null)
            throw new RuntimeException("Gardener not found");
        if (waspPosition == null)
            throw new RuntimeException("wasp not found");
        if (hornetPosition == null)
            throw new RuntimeException("hornet not found");
        Configuration configuration = getConfiguration(emptyConfig);
        World world = new World(1);
        Game game = new Game(world, configuration, gardenerPosition,waspPosition,hornetPosition);
        Map level = new Level(game, 1, mapLevel);
        world.put(1, level);
        return game;
    }

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }

}
