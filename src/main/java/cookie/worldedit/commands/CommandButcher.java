package cookie.worldedit.commands;

import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.animal.EntityAnimal;
import net.minecraft.core.entity.monster.EntityMonster;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;

public class CommandButcher extends Command {

    public CommandButcher() {
        super("/butcher", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {
        if (strings.length == 1) {
            switch (strings[0]) {
                case "all": {
                    for (Entity entity : commandSender.getPlayer().world.loadedEntityList) {
                        if (entity != commandSender.getPlayer()) {
                            entity.remove();
                        }
                    }
                    break;
                }
                case "player": {
                    for (EntityPlayerMP player : MinecraftServer.getInstance().configManager.playerEntities) {
                        if (player != commandSender.getPlayer()) {
                            player.killPlayer();
                        }
                    }
                    break;
                }
                case "hostile": {
                    for (Entity entity : commandSender.getPlayer().world.loadedEntityList) {
                        if (entity instanceof EntityMonster) {
                            entity.remove();
                        }
                    }
                    break;
                }
                case "passive": {
                    for (Entity entity : commandSender.getPlayer().world.loadedEntityList) {
                        if (entity instanceof EntityAnimal) {
                            entity.remove();
                        }
                    }
                    break;
                }
                case "item": {
                    for (Entity entity : commandSender.getPlayer().world.loadedEntityList) {
                        if (entity instanceof EntityItem) {
                            entity.remove();
                        }
                    }
                    break;
                }
                default: break;
            }

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
        commandSender.sendMessage("//butcher <type>");
        commandSender.sendMessage("*  <type> - type of entities to kill");
        commandSender.sendMessage("*    + all");
        commandSender.sendMessage("*    + player");
        commandSender.sendMessage("*    + hostile");
        commandSender.sendMessage("*    + passive");
        commandSender.sendMessage("*    + item");
    }
}
