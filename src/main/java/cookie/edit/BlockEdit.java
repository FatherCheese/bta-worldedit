package cookie.edit;

import cookie.edit.core.commands.CommandRedo;
import cookie.edit.core.commands.CommandSet;
import cookie.edit.core.commands.CommandUndo;
import cookie.edit.core.commands.CommandWand;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.net.command.CommandManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;

public class BlockEdit implements ModInitializer, GameStartEntrypoint {
    public static final String MOD_ID = "blockedit";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CommandManager.registerCommand(new CommandWand());
        CommandManager.registerCommand(new CommandSet());
        CommandManager.registerCommand(new CommandUndo());
        CommandManager.registerCommand(new CommandRedo());
        LOGGER.info("BlockEdit has been initialized.");
    }

    @Override
    public void beforeGameStart() {

    }

    @Override
    public void afterGameStart() {

    }
}
