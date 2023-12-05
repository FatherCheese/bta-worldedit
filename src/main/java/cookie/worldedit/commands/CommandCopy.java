package cookie.worldedit.commands;

import cookie.worldedit.WandClipboard;
import cookie.worldedit.WandPlayerData;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CommandCopy extends Command {

    public CommandCopy() {
        super("/copy", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {
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
                        int id = commandSender.getPlayer().world.getBlockId(x, y, z);
                        int meta = commandSender.getPlayer().world.getBlockMetadata(x, y, z);

                        copyClipboard.putBlock(x - primaryPosition[0], y - primaryPosition[1], z - primaryPosition[2], id, meta);
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
        commandSender.sendMessage("//copy");
    }
}
