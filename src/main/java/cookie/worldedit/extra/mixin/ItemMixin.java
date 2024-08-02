package cookie.worldedit.extra.mixin;

import cookie.worldedit.extra.WandPlayerData;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Item.class, remap = false)
public abstract class ItemMixin {

    @Inject(method = "onUseItemOnBlock", at = @At("TAIL"))
    private void worldedit_useItem(ItemStack itemstack, EntityPlayer entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
        if (!world.isClientSide) {
            if (itemstack != null && itemstack.getData().containsKey("Wand")) {
                int[] hitPosition = {blockX, blockY, blockZ};
                if (!entityplayer.isSneaking()) {
                    WandPlayerData.primaryPositions.put(entityplayer.username, hitPosition);
                    entityplayer.sendMessage("Set primary position at " + blockX + ", " + blockY + ", " + blockZ);
                } else if (entityplayer.isSneaking()) {
                    WandPlayerData.secondaryPositions.put(entityplayer.username, hitPosition);
                    entityplayer.sendMessage("Set secondary position at " + blockX + ", " + blockY + ", " + blockZ);
                }
            }
        }
    }
}
