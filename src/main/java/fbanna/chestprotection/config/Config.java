package fbanna.chestprotection.config;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class Config {

    public static final boolean OP_CAN_BREAK;
    public static final boolean SPECTATOR_CAN_OPEN;

    static {
        final Properties properties = new Properties();
        final Properties newProperties = new Properties();
        final Path path = FabricLoader.getInstance().getConfigDir().resolve("chestprotection.properties");

        if (Files.isRegularFile(path)) {
            try (InputStream in = Files.newInputStream(path, StandardOpenOption.CREATE)) {
                properties.load(in);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        OP_CAN_BREAK = getBoolean(properties, newProperties, "op_can_break", false);
        SPECTATOR_CAN_OPEN = getBoolean(properties, newProperties, "spectator_can_open", true);

        try (OutputStream out = Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            newProperties.store(out, "Configuration file for ChestProtection");
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean getBoolean(Properties properties, Properties newProperties, String key, boolean defaultValue) {
        try {
            final boolean value = Boolean.parseBoolean(properties.getProperty(key));
            newProperties.setProperty(key, Boolean.toString(value));
            return value;
        } catch(NumberFormatException e) {
            newProperties.setProperty(key, Boolean.toString(defaultValue));
            return defaultValue;
        }
    }
}
