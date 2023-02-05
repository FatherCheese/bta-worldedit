package azurelmao.worldedit.commands;

import azurelmao.worldedit.WandClipboard;
import azurelmao.worldedit.WandPlayerData;
import net.minecraft.src.ChunkPosition;
import net.minecraft.src.command.Command;
import net.minecraft.src.command.CommandHandler;
import net.minecraft.src.command.CommandSender;
import org.pf4j.Extension;

import java.util.Map;

@Extension
public class UndoCommand implements com.bta.util.CommandHandler {
    @Override
    public Command command() {
        return new Command("/undo") {
            @Override
            public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
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
                    commandSender.getPlayer().worldObj.setBlockAndMetadataWithNotify(chunkPosition.x, chunkPosition.y, chunkPosition.z, block[0], block[1]);
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
        };
    }
}