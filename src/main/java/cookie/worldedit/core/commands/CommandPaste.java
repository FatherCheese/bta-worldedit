package cookie.worldedit.core.commands;

import cookie.worldedit.extra.WandClipboard;
import cookie.worldedit.extra.WandPlayerData;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.core.world.chunk.ChunkPosition;

import java.util.Map;

public class CommandPaste extends Command {

    public CommandPaste() {
        super("/paste", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        WandClipboard copyClipboard = WandPlayerData.copyClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());
        WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

        if (copyClipboard.page == -1) {
            commandSender.sendMessage("Clipboard is empty!");
            return true;
        }

        int x = (int) commandSender.getPlayer().x;
        int y = (int) commandSender.getPlayer().y;
        int z = (int) commandSender.getPlayer().z;

        if (copyClipboard.page == -1) {
            copyClipboard.createNewPage();
        }

        for (Map.Entry<ChunkPosition, int[]> entry : copyClipboard.getCurrentPage().entrySet()) {
            int[] block = entry.getValue();
            ChunkPosition position = entry.getKey();

            commandSender.getPlayer().world.setBlockAndMetadataWithNotify(position.x + x + 5, position.y + y - 1, position.z + z, block[0], block[1]);
        }

        wandClipboard.createNewPage();

        return true;
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
