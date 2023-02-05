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
public class HollowSetCommand implements com.bta.util.CommandHandler {
    @Override
    public Command command() {
        return new Command("/hset") {
            @Override
            public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
                if (args.length == 2) {
                    int[] primaryPosition = WandPlayerData.primaryPositions.get(commandSender.getPlayer().username);
                    int[] secondPosition = WandPlayerData.secondaryPositions.get(commandSender.getPlayer().username);

                    if (primaryPosition == null || secondPosition == null) {
                        commandSender.sendMessage("Positions aren't set!");
                        return false;
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

                    String[] id1 = args[0].split(":");
                    int meta1 = 0;
                    if (id1.length >= 2) {
                        meta1 = Integer.parseInt(id1[1]);
                    }
                    Block block = SetBlockCommand.getBlock(id1[0], meta1);

                    boolean overwrite = Boolean.parseBoolean(args[1]);

                    WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

                    if (wandClipboard.page == -1) {
                        wandClipboard.createNewPage();
                    }

                    for(int x = minX; x <= maxX; ++x) {
                        for(int y = minY; y <= maxY; ++y) {
                            for(int z = minZ; z <= maxZ; ++z) {
                                if (x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ) {
                                    int id = commandSender.getPlayer().worldObj.getBlockId(x, y, z);
                                    int meta = commandSender.getPlayer().worldObj.getBlockMetadata(x, y, z);
                                    wandClipboard.putBlock(x, y, z, id, meta);
                                } else if (overwrite) {
                                    int id = commandSender.getPlayer().worldObj.getBlockId(x, y, z);
                                    int meta = commandSender.getPlayer().worldObj.getBlockMetadata(x, y, z);
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
                                    wandClipboard.putBlock(x, y, z, block.blockID, meta1);
                                    commandSender.getPlayer().worldObj.setBlockAndMetadataWithNotify(x, y, z, block.blockID, meta1);
                                } else if (overwrite) {
                                    wandClipboard.putBlock(x, y, z, 0, 0);
                                    commandSender.getPlayer().worldObj.setBlockAndMetadataWithNotify(x, y, z, 0, 0);
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
        };
    }
}