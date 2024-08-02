package cookie.worldedit.core.commands;

import cookie.worldedit.extra.WandPlayerData;
import net.minecraft.core.block.Block;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.commands.SetBlockCommand;

public class CommandCount extends Command {

    public CommandCount() {
        super("/count", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        int[] primaryPosition = WandPlayerData.primaryPositions.get(commandSender.getPlayer().username);
        int[] secondPosition = WandPlayerData.secondaryPositions.get(commandSender.getPlayer().username);

        // Position null check and warning.
        if (primaryPosition == null || secondPosition == null) {
            commandSender.sendMessage("Positions aren't set!");
            return true;
        }

        int minX = primaryPosition[0];
        int minY = primaryPosition[1];
        int minZ = primaryPosition[2];
        int maxX = secondPosition[0];
        int maxY = secondPosition[1];
        int maxZ = secondPosition[2];

        // Swap min and max values if min is higher than max.
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

        int countedBlocks = 0;

        // Counts all valid blocks.
        if (strings.length == 0) {
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        if (commandSender.getPlayer().world.getBlockId(x, y, z) != 0)
                            countedBlocks++;
                    }
                }
            }
            commandSender.sendMessage(countedBlocks + " blocks");
            return true;
        }

        // Counts all blocks that match the set ID.
        if (strings.length == 1) {
            String[] blockName = strings[0].split(":");
            int origMeta = 0;
            if (blockName.length >= 2) {
                origMeta = Integer.parseInt(blockName[1]);
            }

            int blockID;
            if (blockName[0].equals("0") || blockName[0].equals("air") || blockName[0].equals("tile.air")) {
                blockID = 0;
            } else {
                Block block = Block.getBlock(SetBlockCommand.getBlock(blockName[0], origMeta));

                // Block ID check.
                if (block == null) {
                    commandSender.sendMessage("Block does not exist!");
                    return true;
                }

                blockID = block.id;
            }

            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        if (commandSender.getPlayer().world.getBlockId(x, y, z) == blockID)
                            countedBlocks++;
                    }
                }
            }
            commandSender.sendMessage(countedBlocks + " blocks");
            return true;
        }
        return true;
    }

    @Override
    public boolean opRequired(String[] strings) {
        return true;
    }

    @Override
    public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
        commandSender.sendMessage("//count");
    }
}
