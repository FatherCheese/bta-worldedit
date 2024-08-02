package cookie.worldedit.core.commands;

import cookie.worldedit.extra.WandClipboard;
import cookie.worldedit.extra.WandPlayerData;
import net.minecraft.core.block.Block;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.net.command.commands.SetBlockCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandLine extends Command {

    public CommandLine() {
        super("/line", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        {
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

                if (minY > maxY) {
                    temp = minY;
                    minY = maxY;
                    maxY = temp;
                } else if (minY < maxY) {
                    temp = maxY;
                    maxY = minY;
                    minY = temp;
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

                WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

                if (wandClipboard.page == -1) {
                    wandClipboard.createNewPage();
                }

                List<List<Integer>> coordinates = getLine(minX, maxY, minZ, maxX, minY, maxZ);

                for (List<Integer> coordinate : coordinates) {
                    int id = commandSender.getPlayer().world.getBlockId(coordinate.get(0), coordinate.get(1), coordinate.get(2));
                    int meta = commandSender.getPlayer().world.getBlockMetadata(coordinate.get(0), coordinate.get(1), coordinate.get(2));
                    wandClipboard.putBlock(coordinate.get(0), coordinate.get(1), coordinate.get(2), id, meta);
                }

                wandClipboard.createNewPage();

                for (List<Integer> coordinate : coordinates) {
                    wandClipboard.putBlock(coordinate.get(0), coordinate.get(1), coordinate.get(2), id1, meta1);
                    commandSender.getPlayer().world.setBlockAndMetadataWithNotify(coordinate.get(0), coordinate.get(1), coordinate.get(2), id1, meta1);
                }
                return true;
            }


            return false;
        }
    }

    @Override
    public boolean opRequired(String[] strings) {
        return true;
    }

    @Override
    public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
        commandSender.sendMessage("//line <block>");
        commandSender.sendMessage("*  <block> - block to place");
    }

    public static List<List<Integer>> getLine(int x1, int y1, int z1, int x2, int y2, int z2) {
        List<List<Integer>> listOfPoints = new ArrayList<>();
        List<Integer> lp1 = Arrays.asList(x1, y1, z1);
        listOfPoints.add(Collections.unmodifiableList(lp1));
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);
        int xs = Integer.compare(x2, x1);
        int ys = Integer.compare(y2, y1);
        int zs = Integer.compare(z2, z1);
        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;
            while (x1 != x2) {
                x1 += xs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
                List<Integer> lp2 = Arrays.asList(x1, y1, z1);
                listOfPoints.add(Collections.unmodifiableList(lp2));
            }
        } else if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;
            while (y1 != y2) {
                y1 += ys;
                if (p1 >= 0) {
                    x1 += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
                List<Integer> lp3 = Arrays.asList(x1, y1, z1);
                listOfPoints.add(Collections.unmodifiableList(lp3));
            }
        } else {
            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;
            while (z1 != z2) {
                z1 += zs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x1 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
                List<Integer> lp4 = Arrays.asList(x1, y1, z1);
                listOfPoints.add(Collections.unmodifiableList(lp4));
            }
        }
        return listOfPoints;
    }
}
