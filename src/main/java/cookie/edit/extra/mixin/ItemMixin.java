package cookie.edit.extra.mixin;

import cookie.edit.extra.WandPlayerData;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Item.class, remap = false)
public abstract class ItemMixin {

    @Inject(method = "onUseItem", at = @At("TAIL"))
    private void blockEdit_rcWand(ItemStack stack, World world, Player player, CallbackInfoReturnable<ItemStack> cir) {
        if (world.isClientSide) {
            return;
        }

        if (stack == null) {
            return;
        }

        if (player.getGamemode() != Gamemode.creative) {
            return;
        }

        if (stack.getData().containsKey("Wand")) {
            HitResult rayTraceResult = player.rayTrace(63, 1.0F, false, false);
            if (rayTraceResult != null && rayTraceResult.hitType == HitResult.HitType.TILE) {
                int x = rayTraceResult.x;
                int y = rayTraceResult.y;
                int z = rayTraceResult.z;

                int[] hitPos = new int[]{x, y, z};
                WandPlayerData.secondaryPositions.put(player.username, hitPos);
                player.sendMessage(String.format("§lSet secondary positions to§0: §<ff8080>%d §<80ff80>%d §<8080ff>%d", x, y, z));
            }
        }
    }
}
