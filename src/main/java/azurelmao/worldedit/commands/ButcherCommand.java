package azurelmao.worldedit.commands;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import net.minecraft.src.command.Command;
import net.minecraft.src.command.CommandHandler;
import net.minecraft.src.command.CommandSender;
import org.pf4j.Extension;

@Extension
public class ButcherCommand implements com.bta.util.CommandHandler {
    @Override
    public Command command() {
        return new Command("/butcher") {
            @Override
            public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] args) {
                if (args.length == 1) {
                    switch (args[0]) {
                        case "all": {
                            for (Entity entity : commandSender.getPlayer().worldObj.loadedEntityList) {
                                if (entity != commandSender.getPlayer()) {
                                    entity.setEntityDead();
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
                            for (Entity entity : commandSender.getPlayer().worldObj.loadedEntityList) {
                                if (entity instanceof EntityMob) {
                                    entity.setEntityDead();
                                }
                            }
                            break;
                        }
                        case "passive": {
                            for (Entity entity : commandSender.getPlayer().worldObj.loadedEntityList) {
                                if (entity instanceof EntityAnimal) {
                                    entity.setEntityDead();
                                }
                            }
                            break;
                        }
                        case "item": {
                            for (Entity entity : commandSender.getPlayer().worldObj.loadedEntityList) {
                                if (entity instanceof EntityItem) {
                                    entity.setEntityDead();
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
        };
    }
}