package cookie.worldedit.core.commands;

import cookie.worldedit.extra.WandClipboard;
import cookie.worldedit.extra.WandPlayerData;
import net.minecraft.core.block.Block;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.commands.SetBlockCommand;

public class CommandCylinderHollow extends Command {

    public CommandCylinderHollow() {
        super("/hollowcylinder", "/hcyl");
    }

    public boolean isPointInsideCylinder(int x, int z, double radius) {
        return x*x + z*z < radius*radius;
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        if (strings.length == 4) {
            int[] primaryPosition = WandPlayerData.primaryPositions.get(commandSender.getPlayer().username);

            if (primaryPosition == null) {
                commandSender.sendMessage("Primary position isn't set!");
                return true;
            }

            int originX = primaryPosition[0];
            int originY = primaryPosition[1];
            int originZ = primaryPosition[2];

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

            double radius;
            try {
                radius = Double.parseDouble(strings[1]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage("Invalid radius! Expected a decimal number.");
                return false;
            }
            int blockRadius = (int) Math.round(radius);

            int blockHeight;
            try {
                blockHeight = Integer.parseInt(strings[2]);
            } catch (NumberFormatException e) {
                commandSender.sendMessage("Invalid height! Expected an integer number.");
                return false;
            }

            boolean overwrite = Boolean.parseBoolean(strings[3]);

            WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

            if (wandClipboard.page == -1) {
                wandClipboard.createNewPage();
            }

            for(int x = -blockRadius; x <= blockRadius; ++x) {
                for(int y = 0; y < blockHeight; ++y) {
                    for(int z = -blockRadius; z <= blockRadius; ++z) {
                        if (isPointInsideCylinder(x, z, radius)) {
                            boolean isExposed = !isPointInsideCylinder(x + 1, z, radius);
                            if (!isExposed && !isPointInsideCylinder(x-1, z, radius)) {
                                isExposed = true;
                            }
                            if (!isExposed && !isPointInsideCylinder(x, z+1, radius)) {
                                isExposed = true;
                            }
                            if (!isExposed && !isPointInsideCylinder(x, z-1, radius)) {
                                isExposed = true;
                            }

                            if (isExposed) {
                                int id = commandSender.getPlayer().world.getBlockId(x+originX, y+originY, z+originZ);
                                int meta = commandSender.getPlayer().world.getBlockMetadata(x+originX, y+originY, z+originZ);
                                wandClipboard.putBlock(x+originX, y+originY, z+originZ, id, meta);
                            } else if (overwrite) {
                                int id = commandSender.getPlayer().world.getBlockId(x+originX, y+originY, z+originZ);
                                int meta = commandSender.getPlayer().world.getBlockMetadata(x+originX, y+originY, z+originZ);
                                wandClipboard.putBlock(x+originX, y+originY, z+originZ, id, meta);
                            }
                        }
                    }
                }
            }

            wandClipboard.createNewPage();

            for(int x = -blockRadius; x <= blockRadius; ++x) {
                for(int y = 0; y < blockHeight; ++y) {
                    for(int z = -blockRadius; z <= blockRadius; ++z) {
                        if (isPointInsideCylinder(x, z, radius)) {
                            boolean isExposed = !isPointInsideCylinder(x + 1, z, radius);
                            if (!isExposed && !isPointInsideCylinder(x-1, z, radius)) {
                                isExposed = true;
                            }
                            if (!isExposed && !isPointInsideCylinder(x, z+1, radius)) {
                                isExposed = true;
                            }
                            if (!isExposed && !isPointInsideCylinder(x, z-1, radius)) {
                                isExposed = true;
                            }

                            if (isExposed) {
                                wandClipboard.putBlock(x+originX, y+originY, z+originZ, id1, meta1);
                                commandSender.getPlayer().world.setBlockAndMetadataWithNotify(x+originX, y+originY, z+originZ, id1, meta1);
                            } else if (overwrite) {
                                wandClipboard.putBlock(x+originX, y+originY, z+originZ, 0, 0);
                                commandSender.getPlayer().world.setBlockAndMetadataWithNotify(x+originX, y+originY, z+originZ, 0, 0);
                            }
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
        commandSender.sendMessage("//hcyl <block> <radius> <height> <overwrite?>");
        commandSender.sendMessage("*  <block> - block to place");
        commandSender.sendMessage("*  <radius> - cylinder radius");
        commandSender.sendMessage("*  <height> - cylinder height");
        commandSender.sendMessage("*  <overwrite?> - whether to clear the blocks inside the cylinder");
    }
}
