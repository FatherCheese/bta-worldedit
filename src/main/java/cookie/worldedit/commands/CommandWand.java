package cookie.worldedit.commands;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CommandWand extends Command {
    public CommandWand() {
        super("/wand", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        ItemStack theWand = new ItemStack(Item.toolAxeWood);
//        theWand.tag.setBoolean("Wand", true);
        theWand.getData().putBoolean("Wand", true);

        commandSender.getPlayer().inventory.insertItem(theWand, false);
        return true;
    }

    @Override
    public boolean opRequired(String[] strings) {
        return true;
    }

    @Override
    public void sendCommandSyntax(CommandHandler commandHandler, CommandSender commandSender) {
        commandSender.sendMessage("//wand");
    }
}
