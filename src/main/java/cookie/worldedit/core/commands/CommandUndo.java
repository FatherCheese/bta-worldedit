package cookie.worldedit.core.commands;

import cookie.worldedit.extra.WandClipboard;
import cookie.worldedit.extra.WandPlayerData;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.world.chunk.ChunkPosition;

import java.util.Map;

public class CommandUndo extends Command {

    public CommandUndo() {
        super("/undo", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        WandClipboard wandClipboard = WandPlayerData.wandClipboards.get(commandSender.getPlayer().username);

        if (wandClipboard == null) {
            commandSender.sendMessage("Clipboard is empty!");
            return true;
        }

        if (wandClipboard.previousPage()) {
            commandSender.sendMessage("Can't go further back!");
            return true;
        }

        for (Map.Entry<ChunkPosition, int[]> entry : wandClipboard.getCurrentPage().entrySet()) {
            ChunkPosition chunkPosition = entry.getKey();
            int[] block = entry.getValue();
            commandSender.getPlayer().world.setBlockAndMetadataWithNotify(chunkPosition.x, chunkPosition.y, chunkPosition.z, block[0], block[1]);
        }

        if (strings.length == 1) {
            int times = Integer.parseInt(String.valueOf(strings.length));

            if (times >= wandClipboard.page) {
                for (int undoCount = 0; undoCount < times; undoCount++) {
                    for (Map.Entry<ChunkPosition, int[]> entry : wandClipboard.getCurrentPage().entrySet()) {
                        ChunkPosition chunkPosition = entry.getKey();
                        int[] block = entry.getValue();
                        commandSender.getPlayer().world.setBlockAndMetadataWithNotify(chunkPosition.x, chunkPosition.y, chunkPosition.z, block[0], block[1]);
                    }
                }
                return true;
            }
        }

        return true;
    }

    @Override
    public boolean opRequired(String[] strings) {
        return true;
    }

    @Override
    public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
        commandSender.sendMessage("//undo");
    }
}
