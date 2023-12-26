package cookie.worldedit.commands;

import cookie.worldedit.WandClipboard;
import cookie.worldedit.WandPlayerData;
import net.minecraft.core.block.Block;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.commands.SetBlockCommand;

public class CommandReplace extends Command {

    public CommandReplace() {
        super("/replace", "");
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

        // Swaps the places of min and max values, if they're negative.
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

        WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

        if (wandClipboard.page == -1) {
            wandClipboard.createNewPage();
        }

        int countedBlocks = 0;

        // Replace all blocks.
        if (strings.length == 1) {
            String[] blockName = strings[0].split(":");
            int origMeta = 0;
            if (blockName.length >= 2) {
                origMeta = Integer.parseInt(blockName[1]);
            }

            int origID;
            if (blockName[0].equals("0") || blockName[0].equals("air") || blockName[0].equals("tile.air")) {
                origID = 0;
            } else {
                Block block = Block.getBlock(SetBlockCommand.getBlock(blockName[0], origMeta));

                if (block == null) {
                    commandSender.sendMessage("Block does not exist!");
                    return true;
                }

                origID = block.id;
            }

            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        int id = commandSender.getPlayer().world.getBlockId(x, y, z);
                        int meta = commandSender.getPlayer().world.getBlockMetadata(x, y, z);
                        wandClipboard.putBlock(x, y, z, id, meta);

                        if (id != 0) {
                            countedBlocks++;
                            commandSender.getPlayer().world.setBlockAndMetadataWithNotify(x, y, z, origID, origMeta);
                        }
                    }
                }
            }

            wandClipboard.createNewPage();
            commandSender.sendMessage("Replaced " + countedBlocks + " blocks");
            return true;
        }

        // Replace specified blocks only.
        if (strings.length == 2) {
            String[] priBlockID = strings[0].split(":");
            int priMeta = 0;
            if (priBlockID.length >= 2) {
                priMeta = Integer.parseInt(priBlockID[1]);
            }
            String[] secBlockID = strings[1].split(":");
            int secMeta = 0;
            if (priBlockID.length >= 2) {
                secMeta = Integer.parseInt(priBlockID[1]);
            }

            int origID;
            if (priBlockID[0].equals("0") || priBlockID[0].equals("air") || priBlockID[0].equals("tile.air")) {
                origID = 0;
            } else {
                Block block = Block.getBlock(SetBlockCommand.getBlock(priBlockID[0], priMeta));

                if (block == null) {
                    commandSender.sendMessage("Block does not exist!");
                    return true;
                }

                origID = block.id;
            }

            int newID;
            if (secBlockID[0].equals("0") || secBlockID[0].equals("air") || secBlockID[0].equals("tile.air")) {
                newID = 0;
            } else {
                Block block = Block.getBlock(SetBlockCommand.getBlock(secBlockID[0], secMeta));

                if (block == null) {
                    commandSender.sendMessage("Block does not exist!");
                    return true;
                }

                newID = block.id;
            }

            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        int id = commandSender.getPlayer().world.getBlockId(x, y, z);
                        int meta = commandSender.getPlayer().world.getBlockMetadata(x, y, z);
                        wandClipboard.putBlock(x, y, z, id, meta);

                        if (id == newID && meta == secMeta) {
                            countedBlocks++;
                            commandSender.getPlayer().world.setBlockAndMetadataWithNotify(x, y, z, origID, priMeta);
                        }
                    }
                }
            }

            commandSender.sendMessage("Replaced " + countedBlocks + " blocks");
            wandClipboard.createNewPage();
            return true;
        }
        return false;
    }

    @Override
    public boolean opRequired(String[] strings) {
        return true;
    }

    @Override
    public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
        commandSender.sendMessage("//replace <block1>");
        commandSender.sendMessage("*  <block1> - block to place");
        commandSender.sendMessage("*  + <block2> - block to replace");
    }
}
