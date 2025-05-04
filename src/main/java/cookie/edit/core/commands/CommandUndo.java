package cookie.edit.core.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentTypeInteger;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import cookie.edit.extra.WandClipboard;
import cookie.edit.extra.WandPlayerData;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkPosition;

import java.util.List;
import java.util.Map;

public class CommandUndo implements CommandManager.CommandRegistry {
    private static final SimpleCommandExceptionType FAILURE_PREVIOUS_PAGE = new SimpleCommandExceptionType(() -> I18n.getInstance().translateKey("command.commands.undo.exception_failure_previous_page"));

    private static int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        EntitySelector entitySelector = context.getArgument("target", EntitySelector.class);
        List<? extends Entity> entities = entitySelector.get(source);

        Player player = null;
        WandClipboard wandClipboard = null;
        for (Entity entityPlayer : entities) {
            player = (Player) entityPlayer;
            wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(((Player) entityPlayer).username, clipboard -> new WandClipboard());
        }

        if (player == null) {
            return 0;
        }

        if (wandClipboard.previousPage()) {
            throw FAILURE_PREVIOUS_PAGE.create();
        }

        World world = source.getWorld();
        for (Map.Entry<ChunkPosition, int[]> entry : wandClipboard.getCurrentPage().entrySet()) {
            ChunkPosition chunkPosition = entry.getKey();
            int[] block = entry.getValue();
            world.setBlockAndMetadataWithNotify(chunkPosition.x, chunkPosition.y, chunkPosition.z, block[0], block[1]);
        }

        int timesToUndo = context.getArgument("amount", Integer.class);
        if (timesToUndo >= wandClipboard.page) {
            for (int i = 0; i < timesToUndo; i++) {
                for (Map.Entry<ChunkPosition, int[]> entry : wandClipboard.getCurrentPage().entrySet()) {
                    ChunkPosition chunkPosition = entry.getKey();
                    int[] block = entry.getValue();
                    world.setBlockAndMetadataWithNotify(chunkPosition.x, chunkPosition.y, chunkPosition.z, block[0], block[1]);
                }
            }
        }

        source.sendTranslatableMessage("command.commands.undo.success", timesToUndo);

        return 1;
    }

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        // Command Registry (name and OP)
        ArgumentBuilderLiteral<CommandSource> command = ArgumentBuilderLiteral.literal("/undo");
        command.requires(CommandSource::hasAdmin);

        // Arguments for the command
        ArgumentBuilderRequired<CommandSource, EntitySelector> targetArgument = ArgumentBuilderRequired.argument("target", ArgumentTypeEntity.usernames());
        ArgumentBuilderRequired<CommandSource, Integer> boolArgument = ArgumentBuilderRequired.argument("amount", ArgumentTypeInteger.integer(1));

        // Command execution
        boolArgument.executes(CommandUndo::run);

        // The command structure
        targetArgument.then(boolArgument);
        command.then(targetArgument);

        dispatcher.register(command);
    }
}
