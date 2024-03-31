package cookie.worldedit.commands;

import cookie.worldedit.Utils;
import cookie.worldedit.WandPlayerData;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CommandSetPos2 extends Command {

    public CommandSetPos2() {
        super("/setPos2", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        int[] hitPosition;
        switch (strings.length){
            case 0:
                hitPosition = new int[]{(int) Math.floor(commandSender.getPlayer().x), ((int) commandSender.getPlayer().y - 1), (int) Math.floor(commandSender.getPlayer().z)};
                break;
            case 3:
                hitPosition = new int[]{Utils.parseInt(strings[0]), Utils.parseInt(strings[1]), Utils.parseInt(strings[2])};
                break;
            default:
                sendCommandSyntax(commandHandler, commandSender);
                return false;
        }
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
        commandSender.sendMessage("//setpos2 <x> <y> <z>");
    }
}
