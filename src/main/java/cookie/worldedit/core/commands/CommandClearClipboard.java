package cookie.worldedit.core.commands;

import cookie.worldedit.extra.WandPlayerData;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CommandClearClipboard extends Command {

    public CommandClearClipboard() {
        super("/clearclipboard", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
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
}
