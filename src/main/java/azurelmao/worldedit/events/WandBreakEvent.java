package azurelmao.worldedit.events;

import azurelmao.worldedit.WandPlayerData;
import com.bta.events.BlockDigEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet14BlockDig;
import org.pf4j.Extension;

@Extension
public class WandBreakEvent implements BlockDigEvent {
    public boolean cancel = false;
    @Override
    public void onEvent(EntityPlayerMP entityPlayerMP, Packet14BlockDig packet14BlockDig, MinecraftServer minecraftServer) {
        ItemStack heldItem = entityPlayerMP.getCurrentEquippedItem();

        if (heldItem == null) {
            return;
        }

        if (!heldItem.tag.hasKey("Wand")) {
            return;
        }

        cancel = true;

        int x = packet14BlockDig.xPosition;
        int y = packet14BlockDig.yPosition;
        int z = packet14BlockDig.zPosition;

        int id = entityPlayerMP.worldObj.getBlockId(x, y, z);
        entityPlayerMP.worldObj.markBlockNeedsUpdate(x, y, z);
        entityPlayerMP.worldObj.notifyBlocksOfNeighborChange(x, y, z, id);

        minecraftServer.configManager.sendChatMessageToPlayer(entityPlayerMP.username, "Set primary position at [" + x + " " + y + " " + z + "]");
        WandPlayerData.primaryPositions.put(entityPlayerMP.username, new int[]{x, y, z});
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }
}
