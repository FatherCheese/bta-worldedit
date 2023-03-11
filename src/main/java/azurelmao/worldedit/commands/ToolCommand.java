package azurelmao.worldedit.commands;

import net.minecraft.src.ItemStack;
import net.minecraft.src.command.Command;
import net.minecraft.src.command.CommandHandler;
import net.minecraft.src.command.CommandSender;
import org.pf4j.Extension;

@Extension
public class ToolCommand implements com.bta.util.CommandHandler {
    @Override
    public Command command() {
        return new Command("/tool") {
            @Override
            public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
                if (args.length > 0) {
                    ItemStack theWand = commandSender.getPlayer().getCurrentEquippedItem();
                    theWand.tag.setString("Tool", String.join(" ", args));

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
                commandSender.sendMessage("//tool <command> <args>");
                commandSender.sendMessage("*  <command> - command to bind to item");
                commandSender.sendMessage("*  <args> - the command's arguments");
            }
        };
    }
}