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

            String[] blockName1 = strings[0].split(":");
            int meta1 = 0;
            if (blockName1.length >= 2) {
                meta1 = Integer.parseInt(blockName1[1]);
            }

            int id1;
            if (blockName1[0].equals("0") || blockName1[0].equals("air") || blockName1[0].equals("tile.air")) {
                id1 = 0;
            } else {
                Block block = Block.getBlock(SetBlockCommand.getBlock(blockName1[0], meta1));

                if (block == null) {
                    commandSender.sendMessage("Block does not exist!");
                    return true;
                }

                id1 = block.id;
            }

            String[] blockName2 = strings[0].split(":");
            int meta2 = 0;
            if (blockName2.length >= 2) {
                meta2 = Integer.parseInt(blockName2[1]);
            }

            int id2;
            if (blockName2[0].equals("0") || blockName2[0].equals("air") || blockName2[0].equals("tile.air")) {
                id2 = 0;
            } else {
                Block block = Block.getBlock(SetBlockCommand.getBlock(blockName2[0], meta1));

                if (block == null) {
                    commandSender.sendMessage("Block does not exist!");
                    return true;
                }

                id2 = block.id;
            }

            WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

            if (wandClipboard.page == -1) {
                wandClipboard.createNewPage();
            }

            for(int x = minX; x <= maxX; ++x) {
                for(int y = minY; y <= maxY; ++y) {
                    for(int z = minZ; z <= maxZ; ++z) {
                        int id = commandSender.getPlayer().world.getBlockId(x, y, z);
                        int meta = commandSender.getPlayer().world.getBlockMetadata(x, y, z);
                        wandClipboard.putBlock(x, y, z, id, meta);
                    }
                }
            }

            wandClipboard.createNewPage();

            for(int x = minX; x <= maxX; ++x) {
                for(int y = minY; y <= maxY; ++y) {
                    for(int z = minZ; z <= maxZ; ++z) {
                        int id = commandSender.getPlayer().world.getBlockId(x, y, z);
                        int meta = commandSender.getPlayer().world.getBlockMetadata(x, y, z);

                        // This is a temporary band-aid until we figure out
                        // what's going on with the original code
                        if (id != 0) {
                            wandClipboard.putBlock(x, y, z, id2, meta2);
                            commandSender.getPlayer().world.setBlockAndMetadataWithNotify(x, y, z, id2, meta2);
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
        commandSender.sendMessage("//replace <block1> <block2>");
        commandSender.sendMessage("*  <block1> - block to place");
        commandSender.sendMessage("*  <block2> - block to replace");
    }
}
