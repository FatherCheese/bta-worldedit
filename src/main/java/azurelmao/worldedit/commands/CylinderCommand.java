package azurelmao.worldedit.commands;

import azurelmao.worldedit.WandClipboard;
import azurelmao.worldedit.WandPlayerData;
import net.minecraft.src.Block;
import net.minecraft.src.command.Command;
import net.minecraft.src.command.CommandHandler;
import net.minecraft.src.command.CommandSender;
import net.minecraft.src.command.commands.SetBlockCommand;
import org.pf4j.Extension;

@Extension
public class CylinderCommand implements com.bta.util.CommandHandler {
    @Override
    public Command command() {
        return new Command("/cyl") {
            @Override
            public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
                if (args.length == 4) {
                    double[] primaryPosition = WandPlayerData.primaryPositions.get(commandSender.getPlayer().username);

                    if (primaryPosition == null) {
                        commandSender.sendMessage("Primary position isn't set!");
                        return true;
                    }

                    int originX = (int) primaryPosition[0];
                    int originY = (int) primaryPosition[1];
                    int originZ = (int) primaryPosition[2];

                    String[] id1 = args[0].split(":");
                    int meta1 = 0;
                    if (id1.length >= 2) {
                        meta1 = Integer.parseInt(id1[1]);
                    }
                    Block block = SetBlockCommand.getBlock(id1[0], meta1);

                    double radius;
                    try {
                        radius = Double.parseDouble(args[1]);
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage("Invalid radius! Expected a decimal number.");
                        return false;
                    }
                    int blockRadius = (int) Math.round(radius);

                    int blockHeight;
                    try {
                        blockHeight = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage("Invalid height! Expected an integer number.");
                        return false;
                    }

                    WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

                    if (wandClipboard.page == -1) {
                        wandClipboard.createNewPage();
                    }

                    for(int x = -blockRadius; x <= blockRadius; ++x) {
                        for(int y = 0; y < blockHeight; ++y) {
                            for(int z = -blockRadius; z <= blockRadius; ++z) {
                                if (isPointInsideCylinder(x, z, radius)) {
                                    int id = commandSender.getPlayer().worldObj.getBlockId(x+originX, y+originY, z+originZ);
                                    int meta = commandSender.getPlayer().worldObj.getBlockMetadata(x+originX, y+originY, z+originZ);
                                    wandClipboard.putBlock(x+originX, y+originY, z+originZ, id, meta);
                                }
                            }
                        }
                    }

                    wandClipboard.createNewPage();

                    for(int x = -blockRadius; x <= blockRadius; ++x) {
                        for(int y = 0; y < blockHeight; ++y) {
                            for(int z = -blockRadius; z <= blockRadius; ++z) {
                                if (isPointInsideCylinder(x, z, radius)) {
                                    wandClipboard.putBlock(x+originX, y+originY, z+originZ, block.blockID, meta1);
                                    commandSender.getPlayer().worldObj.setBlockAndMetadataWithNotify(x+originX, y+originY, z+originZ, block.blockID, meta1);
                                }
                            }
                        }
                    }

                    return true;
                }

                return false;
            }

            public boolean isPointInsideCylinder(int x, int z, double radius) {
                return x*x + z*z < radius*radius;
            }

            @Override
            public boolean opRequired(String[] strings) {
                return true;
            }

            @Override
            public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
                commandSender.sendMessage("//cyl <block> <radius> <height> <raised?>");
                commandSender.sendMessage("*  Arguments:");
                commandSender.sendMessage("*  <block> - block to place");
                commandSender.sendMessage("*  <radius> - cylinder radius");
                commandSender.sendMessage("*  <height> - cylinder height");
                commandSender.sendMessage("*  <raised?> - whether to shift the cylinder up");
            }
        };
    }
}