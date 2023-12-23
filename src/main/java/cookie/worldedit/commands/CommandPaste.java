package cookie.worldedit.commands;

import cookie.worldedit.WandClipboard;
import cookie.worldedit.WandPlayerData;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.world.chunk.ChunkPosition;

import java.util.HashMap;
import java.util.Map;

public class CommandPaste extends Command {

    public CommandPaste() {
        super("/paste", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        if (strings.length == 0) {

            WandClipboard copyClipboard = WandPlayerData.copyClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());
            WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

            if (copyClipboard.page == -1) {
                commandSender.sendMessage("Clipboard is empty!");
                return true;
            }

            int x = (int) commandSender.getPlayer().x;
            int y = (int) commandSender.getPlayer().y;
            int z = (int) commandSender.getPlayer().z;

//            commandSender.getPlayer().world.setBlockAndMetadataWithNotify(x + 3, y, z - 1, Block.stone.id, 0);

            for (Map.Entry<ChunkPosition, int[]> entry : copyClipboard.getCurrentPage().entrySet()) {
                int[] block = entry.getValue();
                ChunkPosition position = entry.getKey();

                commandSender.getPlayer().world.setBlockAndMetadataWithNotify(position.x + x + 3, position.y + y, position.z + z - 1, block[0], block[1]);
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
}
