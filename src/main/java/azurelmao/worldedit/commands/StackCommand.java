package azurelmao.worldedit.commands;

import azurelmao.worldedit.WandClipboard;
import azurelmao.worldedit.WandPlayerData;
import net.minecraft.src.Block;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.Vec3D;
import net.minecraft.src.command.Command;
import net.minecraft.src.command.CommandHandler;
import net.minecraft.src.command.CommandSender;
import net.minecraft.src.command.commands.SetBlockCommand;
import net.minecraft.src.helper.Direction;
import org.pf4j.Extension;

import java.util.HashMap;
import java.util.Map;

@Extension
public class StackCommand implements com.bta.util.CommandHandler {
    @Override
    public Command command() {
        return new Command("/stack") {
            @Override
            public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
                if (args.length == 1) {
                    double[] primaryPosition = WandPlayerData.primaryPositions.get(commandSender.getPlayer().username);
                    double[] secondPosition = WandPlayerData.secondaryPositions.get(commandSender.getPlayer().username);

                    if (primaryPosition == null || secondPosition == null) {
                        commandSender.sendMessage("Positions aren't set!");
                        return false;
                    }

                    int minX = (int) primaryPosition[0];
                    int minY = (int) primaryPosition[1];
                    int minZ = (int) primaryPosition[2];
                    int maxX = (int) secondPosition[0];
                    int maxY = (int) secondPosition[1];
                    int maxZ = (int) secondPosition[2];

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

                    int times;
                    try {
                        times = Integer.parseInt(args[0]);
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage("Invalid number! Expected an integer number.");
                        return false;
                    }

                    int offsetX = 0;
                    int offsetZ = 0;
                    Direction direction = Direction.getHorizontalDirection(commandSender.getPlayer()).getOpposite();
                    switch (direction) {
                        case EAST -> offsetX = maxX-minX+1;
                        case WEST -> offsetX = -(maxX-minX+1);
                        case SOUTH -> offsetZ = maxZ-minZ+1;
                        case NORTH -> offsetZ = -(maxZ-minZ+1);
                    }

                    HashMap<ChunkPosition, int[]> blocksToStack = new HashMap<>();
                    for(int x = minX; x <= maxX; ++x) {
                        for(int y = minY; y <= maxY; ++y) {
                            for(int z = minZ; z <= maxZ; ++z) {
                                int id = commandSender.getPlayer().worldObj.getBlockId(x, y, z);
                                int meta = commandSender.getPlayer().worldObj.getBlockMetadata(x, y, z);
                                blocksToStack.put(new ChunkPosition(x, y, z), new int[]{id, meta});
                            }
                        }
                    }

                    WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

                    if (wandClipboard.page == -1) {
                        wandClipboard.createNewPage();
                    }

                    for (int i = 1; i <= times; ++i) {
                        for (Map.Entry<ChunkPosition, int[]> entry : blocksToStack.entrySet()) {
                            ChunkPosition position = entry.getKey();
                            int id = commandSender.getPlayer().worldObj.getBlockId(position.x + offsetX * i, position.y, position.z + offsetZ * i);
                            int meta = commandSender.getPlayer().worldObj.getBlockMetadata(position.x + offsetX * i, position.y, position.z + offsetZ * i);
                            wandClipboard.putBlock(position.x + offsetX * i, position.y, position.z + offsetZ * i, id, meta);
                        }
                    }

                    wandClipboard.createNewPage();

                    for (int i = 1; i <= times; ++i) {
                        for (Map.Entry<ChunkPosition, int[]> entry : blocksToStack.entrySet()) {
                            ChunkPosition position = entry.getKey();
                            int[] block = entry.getValue();

                            wandClipboard.putBlock(position.x + offsetX * i, position.y, position.z + offsetZ * i, block[0], block[1]);
                            commandSender.getPlayer().worldObj.setBlockAndMetadataWithNotify(position.x + offsetX * i, position.y, position.z + offsetZ * i, block[0], block[1]);
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
                commandSender.sendMessage("//stack <number>");
                commandSender.sendMessage("*  Arguments:");
                commandSender.sendMessage("*  <number> - number of times to stack");
            }
        };
    }
}