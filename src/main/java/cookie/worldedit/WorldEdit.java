package cookie.worldedit;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldEdit implements ModInitializer {
    public static final String MOD_ID = "worldedit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("World Edit has been initialized.");
    }
}
