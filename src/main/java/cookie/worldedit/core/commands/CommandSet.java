package cookie.worldedit.core.commands;

import cookie.worldedit.extra.WandClipboard;
import cookie.worldedit.extra.WandPlayerData;
import net.minecraft.core.block.Block;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.commands.SetBlockCommand;

public class CommandSet extends Command {

    public CommandSet() {
        super("/set", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        if (strings.length == 1) {
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

            WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

            if (wandClipboard.page == -1) {
                wandClipboard.createNewPage();
            }

            int countedBlocks = 0;

            for(int x = minX; x <= maxX; ++x) {
                for(int y = minY; y <= maxY; ++y) {
                    for(int z = minZ; z <= maxZ; ++z) {
                        int id = commandSender.getPlayer().world.getBlockId(x, y, z);
                        int meta = commandSender.getPlayer().world.getBlockMetadata(x, y, z);
                        wandClipboard.putBlock(x, y, z, id, meta);
                        ++countedBlocks;
                    }
                }
            }

            wandClipboard.createNewPage();

            for(int x = minX; x <= maxX; ++x) {
                for(int y = minY; y <= maxY; ++y) {
                    for(int z = minZ; z <= maxZ; ++z) {
                        int id = commandSender.getPlayer().world.getBlockId(x, y, z);
                        int meta = commandSender.getPlayer().world.getBlockMetadata(x, y, z);

                        wandClipboard.putBlock(x, y, z, origID, origMeta);
                        commandSender.getPlayer().world.setBlockAndMetadataWithNotify(x, y, z, origID, origMeta);
                    }
                }
            }

            commandSender.sendMessage("Set " + countedBlocks + " blocks");
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
        commandSender.sendMessage("//set <block>");
        commandSender.sendMessage("*  <block> - block to place");
    }
}
