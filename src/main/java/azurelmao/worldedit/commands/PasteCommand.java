package azurelmao.worldedit.commands;

import azurelmao.worldedit.WandClipboard;
import azurelmao.worldedit.WandPlayerData;
import net.minecraft.src.Block;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.command.Command;
import net.minecraft.src.command.CommandHandler;
import net.minecraft.src.command.CommandSender;
import org.pf4j.Extension;

import java.util.HashMap;
import java.util.Map;

@Extension
public class PasteCommand implements com.bta.util.CommandHandler {
    @Override
    public Command command() {
        return new Command("/paste") {
            @Override
            public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
                if (args.length == 0) {
                    int[] primaryPosition = WandPlayerData.primaryPositions.get(commandSender.getPlayer().username);

                    if (primaryPosition == null) {
                        commandSender.sendMessage("Primary position isn't set!");
                        return true;
                    }

                    int originX = primaryPosition[0];
                    int originY = primaryPosition[1];
                    int originZ = primaryPosition[2];

                    WandClipboard copyClipboard = WandPlayerData.copyClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

                    if (copyClipboard.page == -1) {
                        commandSender.sendMessage("Copy clipboard is empty!");
                        return true;
                    }

                    HashMap<ChunkPosition, int[]> normalBlocks = new HashMap<>();
                    HashMap<ChunkPosition, int[]> notNormalBlocks = new HashMap<>();

                    WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

                    if (wandClipboard.page == -1) {
                        wandClipboard.createNewPage();
                    }

                    for (Map.Entry<ChunkPosition, int[]> entry : copyClipboard.getCurrentPage().entrySet()) {
                        ChunkPosition position = entry.getKey();

                        int id = commandSender.getPlayer().worldObj.getBlockId(position.x + originX, position.y + originY, position.z + originZ);
                        int meta = commandSender.getPlayer().worldObj.getBlockMetadata(position.x + originX, position.y + originY, position.z + originZ);
                        wandClipboard.putBlock(position.x + originX, position.y + originY, position.z + originZ, id, meta);

                        int[] block = entry.getValue();
                        if (block[0] != 0 && Block.getBlock(block[0]).renderAsNormalBlock()) {
                            normalBlocks.put(new ChunkPosition(position.x + originX, position.y + originY, position.z + originZ), block);
                        } else {
                            notNormalBlocks.put(new ChunkPosition(position.x + originX, position.y + originY, position.z + originZ), block);
                        }
                    }

                    wandClipboard.createNewPage();

                    for (Map.Entry<ChunkPosition, int[]> entry : normalBlocks.entrySet()) {
                        ChunkPosition position = entry.getKey();
                        int[] block = entry.getValue();

                        wandClipboard.putBlock(position.x, position.y, position.z, block[0], block[1]);
                        commandSender.getPlayer().worldObj.setBlockAndMetadataWithNotify(position.x, position.y, position.z, block[0], block[1]);
                    }

                    for (Map.Entry<ChunkPosition, int[]> entry : notNormalBlocks.entrySet()) {
                        ChunkPosition position = entry.getKey();
                        int[] block = entry.getValue();

                        wandClipboard.putBlock(position.x, position.y, position.z, block[0], block[1]);
                        commandSender.getPlayer().worldObj.setBlockAndMetadataWithNotify(position.x, position.y, position.z, block[0], block[1]);
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
                commandSender.sendMessage("//paste");
            }
        };
    }
}