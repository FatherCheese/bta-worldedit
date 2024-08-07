package cookie.worldedit.core.commands;

import cookie.worldedit.extra.WandPlayerData;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CommandSetPos2 extends Command {

    public CommandSetPos2() {
        super("/setPos2", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        int[] hitPosition = {(int) Math.floor(commandSender.getPlayer().x), ((int) commandSender.getPlayer().y - 1), (int) Math.floor(commandSender.getPlayer().z)};
        WandPlayerData.secondaryPositions.put(commandSender.getPlayer().username, hitPosition);
        commandSender.sendMessage("Set secondary position at " + hitPosition[0] + ", " + hitPosition[1] + ", " + hitPosition[2]);
        return true;
    }

    @Override
    public boolean opRequired(String[] strings) {
        return true;
    }

    @Override
    public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
        commandSender.sendMessage("//setpos2");
    }
}
