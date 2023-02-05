package azurelmao.worldedit.commands;

import azurelmao.worldedit.WandPlayerData;
import net.minecraft.src.command.Command;
import net.minecraft.src.command.CommandHandler;
import net.minecraft.src.command.CommandSender;
import org.pf4j.Extension;

@Extension
public class ClearClipboardCommand implements com.bta.util.CommandHandler {
    @Override
    public Command command() {
        return new Command("/clearclipboard") {
            @Override
            public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
                WandPlayerData.wandClipboards.remove(commandSender.getPlayer().username);

                return true;
            }

            @Override
            public boolean opRequired(String[] strings) {
                return true;
            }

            @Override
            public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
                commandSender.sendMessage("//clearclipboard");
            }
        };
    }
}