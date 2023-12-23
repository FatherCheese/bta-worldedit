package cookie.worldedit.commands;

import net.minecraft.core.block.Block;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CommandUp extends Command {

    public CommandUp() {
        super("/up", "");
    }

    public boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        if (strings.length == 1 && isNumber(strings[0])) {
            commandSender.getPlayer().world.setBlockWithNotify((int) commandSender.getPlayer().x - 1,
                    (int) commandSender.getPlayer().y + Integer.parseInt(strings[0]),
                    (int) commandSender.getPlayer().z - 1,
                    Block.glass.id);

            commandSender.getPlayer().setPos(commandSender.getPlayer().x, (int) commandSender.getPlayer().y + Integer.parseInt(strings[0]) + 3, commandSender.getPlayer().z);
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

    }
}
