package cookie.worldedit.core.commands;

import net.minecraft.core.block.Block;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CommandUp extends Command {

    public CommandUp() {
        super("up", "");
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
            boolean negX = commandSender.getPlayer().x < 0;
            boolean negZ = commandSender.getPlayer().z < 0;

            // This is error prevention. Without this it will offset by 1 in the negative value.
            double setXValue = 0;
            double setZValue = 0;

            if (negX)
                setXValue -= 1;
            if (negZ)
                setZValue -= 1;
            if (negX && negZ) {
                setXValue -= 2;
                setZValue -= 2;
            }

            commandSender.getPlayer().world.setBlockWithNotify((int) (commandSender.getPlayer().x + setXValue),
                    (int) commandSender.getPlayer().y + Integer.parseInt(strings[0]),
                    (int) (commandSender.getPlayer().z + setZValue),
                    Block.glass.id);

            commandSender.getPlayer().setPos(commandSender.getPlayer().x,
                    (int) commandSender.getPlayer().y + Integer.parseInt(strings[0]) + 3,
                    commandSender.getPlayer().z);
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
