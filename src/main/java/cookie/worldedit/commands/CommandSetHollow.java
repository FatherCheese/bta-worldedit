package cookie.worldedit.commands;

import cookie.worldedit.WandClipboard;
import cookie.worldedit.WandPlayerData;
import net.minecraft.core.block.Block;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.commands.SetBlockCommand;

public class CommandSetHollow extends Command {
    public CommandSetHollow() {
        super("/hollowset", "/hset");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        if (strings.length == 2) {
            int[] primaryPosition = WandPlayerData.primaryPositions.get(commandSender.getPlayer().username);
            int[] secondPosition = WandPlayerData.secondaryPositions.get(commandSender.getPlayer().username);

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

            String[] blockName = strings[0].split(":");
            int meta1 = 0;
            if (blockName.length >= 2) {
                meta1 = Integer.parseInt(blockName[1]);
            }

            int id1;
            if (blockName[0].equals("0") || blockName[0].equals("air") || blockName[0].equals("tile.air")) {
                id1 = 0;
            } else {
                Block block = Block.getBlock(SetBlockCommand.getBlock(blockName[0], meta1));

                if (block == null) {
                    commandSender.sendMessage("Block does not exist!");
                    return true;
                }

                id1 = block.id;
            }

            boolean overwrite = Boolean.parseBoolean(strings[1]);

            WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

            if (wandClipboard.page == -1) {
                wandClipboard.createNewPage();
            }

            for(int x = minX; x <= maxX; ++x) {
                for(int y = minY; y <= maxY; ++y) {
                    for(int z = minZ; z <= maxZ; ++z) {
                        if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                            int id = commandSender.getPlayer().world.getBlockId(x, y, z);
                            int meta = commandSender.getPlayer().world.getBlockMetadata(x, y, z);
                            wandClipboard.putBlock(x, y, z, id, meta);
                        } else if (overwrite) {
                            int id = commandSender.getPlayer().world.getBlockId(x, y, z);
                            int meta = commandSender.getPlayer().world.getBlockMetadata(x, y, z);
                            wandClipboard.putBlock(x, y, z, id, meta);
                        }
                    }
                }
            }

            wandClipboard.createNewPage();

            for(int x = minX; x <= maxX; ++x) {
                for(int y = minY; y <= maxY; ++y) {
                    for(int z = minZ; z <= maxZ; ++z) {
                        if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                            wandClipboard.putBlock(x, y, z, id1, meta1);
                            commandSender.getPlayer().world.setBlockAndMetadataWithNotify(x, y, z, id1, meta1);
                        } else if (overwrite) {
                            wandClipboard.putBlock(x, y, z, 0, 0);
                            commandSender.getPlayer().world.setBlockAndMetadataWithNotify(x, y, z, 0, 0);
                        }
                    }
                }
            }

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
        commandSender.sendMessage("//hset <block> <overwrite?>");
        commandSender.sendMessage("*  <block> - block to place");
        commandSender.sendMessage("*  <overwrite?> - whether to clear the blocks inside the selection");
    }
}
