package azurelmao.worldedit.commands;

import azurelmao.worldedit.WandClipboard;
import azurelmao.worldedit.WandPlayerData;
import net.minecraft.src.command.Command;
import net.minecraft.src.command.CommandHandler;
import net.minecraft.src.command.CommandSender;
import org.pf4j.Extension;

@Extension
public class CutCommand implements com.bta.util.CommandHandler {
    @Override
    public Command command() {
        return new Command("/cut") {
            @Override
            public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
                if (args.length == 0) {
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

                    WandClipboard copyClipboard = WandPlayerData.copyClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

                    if (copyClipboard.page == -1) {
                        copyClipboard.createNewPage();
                    }

                    copyClipboard.getCurrentPage().clear();

                    for(int x = minX; x <= maxX; ++x) {
                        for(int y = minY; y <= maxY; ++y) {
                            for(int z = minZ; z <= maxZ; ++z) {
                                int id = commandSender.getPlayer().worldObj.getBlockId(x, y, z);
                                int meta = commandSender.getPlayer().worldObj.getBlockMetadata(x, y, z);

                                copyClipboard.putBlock(x - primaryPosition[0], y - primaryPosition[1], z - primaryPosition[2], id, meta);
                                commandSender.getPlayer().worldObj.setBlock(x, y, z, 0);
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
                commandSender.sendMessage("//cut");
            }
        };
    }
}