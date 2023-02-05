package azurelmao.worldedit.commands;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.command.Command;
import net.minecraft.src.command.CommandHandler;
import net.minecraft.src.command.CommandSender;
import org.pf4j.Extension;

@Extension
public class WandCommand implements com.bta.util.CommandHandler {
    @Override
    public Command command() {
        return new Command("/wand") {
            @Override
            public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
                ItemStack theWand = new ItemStack(Item.toolAxeWood);
                theWand.tag.setBoolean("Wand", true);

                commandSender.getPlayer().inventory.addItemStackToInventory(theWand);
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
        };
    }
}