package azurelmao.worldedit.events;

import azurelmao.worldedit.WandPlayerData;
import com.bta.events.PlaceEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet15Place;
import org.pf4j.Extension;

@Extension
public class WandUseEvent implements PlaceEvent {
    @Override
    public void onEvent(EntityPlayerMP entityPlayerMP, Packet15Place packet15Place, MinecraftServer minecraftServer) {
        ItemStack heldItem = entityPlayerMP.getCurrentEquippedItem();

        if (packet15Place.xPosition == -1 && packet15Place.yPosition == 255 && packet15Place.zPosition == -1 && packet15Place.direction == 255 && packet15Place.heightPlaced == 0) {
            return;
        }

        if (heldItem == null) {
            return;
        }

        if (!heldItem.tag.hasKey("Wand")) {
            return;
        }

        int x = packet15Place.xPosition;
        int y = packet15Place.yPosition;
        int z = packet15Place.zPosition;

        minecraftServer.configManager.sendChatMessageToPlayer(entityPlayerMP.username, "Set secondary position at [" + x + " " + y + " " + z + "]");
        WandPlayerData.secondaryPositions.put(entityPlayerMP.username, new int[]{x, y, z});
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
