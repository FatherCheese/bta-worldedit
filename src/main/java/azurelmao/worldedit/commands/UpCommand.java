package azurelmao.worldedit.commands;

import net.minecraft.src.Block;
import net.minecraft.src.command.Command;
import net.minecraft.src.command.CommandHandler;
import net.minecraft.src.command.CommandSender;
import org.pf4j.Extension;

import java.text.NumberFormat;
import java.text.ParsePosition;

@Extension
public class UpCommand implements com.bta.util.CommandHandler {
    @Override
    public Command command() {
        return new Command("/up") {
            @Override
            public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
                if (args.length == 1 && isNumber(args[0])) {
                    commandSender.getPlayer().worldObj.setBlockWithNotify((int) commandSender.getPlayer().posX, (int) commandSender.getPlayer().posY + Integer.parseInt(args[0]), (int) commandSender.getPlayer().posZ, Block.glass.blockID);
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
                commandSender.sendMessage("//up <#>");
                commandSender.sendMessage("*  <#> - how many blocks up to set to glass");
            }
        };
    }
    public boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}