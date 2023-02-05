package azurelmao.worldedit.events;

import azurelmao.worldedit.WandPlayerData;
import com.bta.events.PlaceEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.*;
import org.pf4j.Extension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Extension
public class ToolUseEvent implements PlaceEvent {
    @Override
    public void onEvent(EntityPlayerMP entityPlayerMP, Packet15Place packet15Place, MinecraftServer minecraftServer) {
        ItemStack heldItem = entityPlayerMP.getCurrentEquippedItem();

        if (heldItem == null) {
            return;
        }

        if (!(packet15Place.xPosition == -1 && packet15Place.yPosition == 255 && packet15Place.zPosition == -1 && packet15Place.direction == 255 && packet15Place.heightPlaced == 0)) {
            return;
        }

        if (!heldItem.tag.hasKey("Tool")) {
            return;
        }

        double viewDistance = minecraftServer.propertyManagerObj.getIntProperty("view-distance", 10) * 16;
        Vec3D position = entityPlayerMP.getPosition(1);
        position.yCoord += 1.6;
        Vec3D direction = entityPlayerMP.getLook(1);
        Vec3D target = position.addVector(direction.xCoord * viewDistance, direction.yCoord * viewDistance, direction.zCoord * viewDistance);

        MovingObjectPosition hitResult = entityPlayerMP.worldObj.rayTraceBlocks(position, target);
        if (hitResult == null) {
            return;
        }

        int[] hitPosition = {hitResult.blockX, hitResult.blockY, hitResult.blockZ};
        int[] tempPrimary = WandPlayerData.primaryPositions.get(entityPlayerMP.username);
        WandPlayerData.primaryPositions.put(entityPlayerMP.username, hitPosition);

        try {
            Method method = NetServerHandler.class.getDeclaredMethod("handleSlashCommand", String.class);
            method.setAccessible(true);
            method.invoke(entityPlayerMP.playerNetServerHandler, heldItem.tag.getString("Tool"));

        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        WandPlayerData.primaryPositions.put(entityPlayerMP.username, tempPrimary);
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
}
