package cookie.worldedit.commands;

import cookie.worldedit.WandClipboard;
import cookie.worldedit.WandPlayerData;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.CommandHandler;
import net.minecraft.core.net.command.CommandSender;

public class CommandDrain extends Command {
    public CommandDrain() {
        super("/drain", "");
    }

    @Override
    public boolean execute(CommandHandler commandHandler, CommandSender commandSender, String[] strings) {

        WandClipboard wandClipboard = WandPlayerData.wandClipboards.computeIfAbsent(commandSender.getPlayer().username, k -> new WandClipboard());

        if (wandClipboard.page == -1) {
            wandClipboard.createNewPage();
        }

        if (strings.length == 1) {
            int values = Integer.parseInt(strings[0]);
            int blocks = 0;

            EntityPlayer player = commandSender.getPlayer();
            int x = (int) player.x;
            int y = (int) player.y;
            int z = (int) player.z;

            // TODO fix this so it centers around the player!
            for (int drX = -values; drX < values; drX++) {
                for (int drY = -values; drY < values; drY++) {
                    for (int drZ = -values; drZ < values; drZ++) {
                        if (player.world.getBlockMaterial(x + drX, y + drY, z + drZ) == Material.water ||
                                player.world.getBlockMaterial(x + drX, y + drY, z + drZ) == Material.lava) {
                            int id = commandSender.getPlayer().world.getBlockId(x + drX, y + drY, z + drZ);
                            int meta = commandSender.getPlayer().world.getBlockMetadata(x + drX, y + drY, z + drZ);
                            wandClipboard.putBlock(x + drX, y + drY, z + drZ, id, meta);

                            player.world.setBlockWithNotify(x + drX, y + drY, z + drZ, 0);
                            blocks++;
                        }
                    }
                }
            }
            commandSender.sendMessage("Drained " + blocks + " blocks");
            wandClipboard.createNewPage();
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
        commandSender.sendMessage("//drain <range>");
    }
}
