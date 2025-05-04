package cookie.edit.core.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import cookie.edit.core.commands.arguments.ArgumentTypeBlocksList;
import cookie.edit.extra.WandClipboard;
import cookie.edit.extra.WandPlayerData;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.world.World;

import java.util.List;

public class CommandSet implements CommandManager.CommandRegistry {
    private static final SimpleCommandExceptionType FAILURE_POS = new SimpleCommandExceptionType(() -> I18n.getInstance().translateKey("command.commands.set.exception_failure_pos"));
    private static final SimpleCommandExceptionType FAILURE_BLOCK = new SimpleCommandExceptionType(() -> I18n.getInstance().translateKey("command.commands.set.exception_failure_block"));

    private static int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ArgumentTypeBlocksList.BlockListEntry[] blocks = context.getArgument("blocks", ArgumentTypeBlocksList.BlockListEntry[].class);

        CommandSource source = context.getSource();
        EntitySelector entitySelector = context.getArgument("target", EntitySelector.class);
        List<? extends Entity> entities = entitySelector.get(source);

        // Target positions
        int[] primaryPos = new int[]{0, 0, 0};
        int[] secondaryPos = new int[]{0, 0, 0};
        WandClipboard wandClipboard = null;

        // Player and clipboard (is this, and the target function, needed?)
        for (Entity player : entities) {
            primaryPos = WandPlayerData.primaryPositions.get(((Player) player).username);
            secondaryPos = WandPlayerData.secondaryPositions.get(((Player) player).username);

            if (primaryPos == null || secondaryPos == null) {
                throw FAILURE_POS.create();
            }

            wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(((Player) player).username, clipboard -> new WandClipboard());
        }

        // Positions
        int minX = primaryPos[0];
        int minY = primaryPos[1];
        int minZ = primaryPos[2];
        int maxX = secondaryPos[0];
        int maxY = secondaryPos[1];
        int maxZ = secondaryPos[2];

        int temp;
        if (minX > maxX) {
            temp = minX;
            minX = maxX;
            maxX = temp;
        }

        if (minY > maxY) {
            temp = minY;
            minY = maxY;
            maxY = temp;
        }

        if (minZ > maxZ) {
            temp = minZ;
            minZ = maxZ;
            maxZ = temp;
        }

        // Saves the current blocks present, for the undo command
        if (wandClipboard != null && wandClipboard.page == -1) {
            wandClipboard.createNewPage();
        }

        int countedBlocks = 0;
        World world = source.getWorld();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    int id = world.getBlockId(x, y, z);
                    int meta = world.getBlockMetadata(x, y, z);

                    wandClipboard.putBlock(x, y, z, id, meta);
                    ++countedBlocks;
                }
            }
        }

        // Place the blocks from the command
        wandClipboard.createNewPage();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    float totalChance = 0f;
                    for (ArgumentTypeBlocksList.BlockListEntry entry : blocks) {
                        totalChance += entry.getChance();
                    }

                    float randomValue = world.rand.nextFloat() * totalChance;
                    float currentWeight = 0f;

                    ArgumentTypeBlocksList.BlockListEntry selectedEntry = blocks[0];

                    for (ArgumentTypeBlocksList.BlockListEntry entry : blocks) {
                        currentWeight += entry.getChance();
                        if (randomValue <= currentWeight) {
                            selectedEntry = entry;
                            break;
                        }
                    }

                    if (Blocks.blocksList[selectedEntry.getBlockId()] == null) {
                        throw FAILURE_BLOCK.create();
                    }

                    wandClipboard.putBlock(x, y, z, selectedEntry.getBlockId(), selectedEntry.getMeta());

                    world.setBlockAndMetadataWithNotify(x, y, z, selectedEntry.getBlockId(), selectedEntry.getMeta());
                    countedBlocks++;
                }
            }
        }

        source.sendTranslatableMessage("command.commands.set.success", countedBlocks);
        return 1;
    }

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        // Command Registry (name and OP)
        ArgumentBuilderLiteral<CommandSource> command = ArgumentBuilderLiteral.literal("/set");
        command.requires(CommandSource::hasAdmin);

        // Arguments for the command
        ArgumentBuilderRequired<CommandSource, EntitySelector> targetArgument = ArgumentBuilderRequired.argument("target", ArgumentTypeEntity.usernames());
        ArgumentBuilderRequired<CommandSource, ArgumentTypeBlocksList.BlockListEntry[]> blocksArgument = ArgumentBuilderRequired.argument("blocks", ArgumentTypeBlocksList.blockList());

        // Command execution
        blocksArgument.executes(CommandSet::run);

        // The command structure
        targetArgument.then(blocksArgument);
        command.then(targetArgument);

        dispatcher.register(command);
    }
}
