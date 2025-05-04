package cookie.edit.extra.mixin;

import cookie.edit.extra.WandPlayerData;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerMixin extends Mob {

    public PlayerMixin(@Nullable World world) {
        super(world);
    }

    @Shadow public abstract ItemStack getHeldItem();

    @Shadow public String username;

    @Shadow public abstract void sendMessage(String string);

    @Shadow public abstract Gamemode getGamemode();

    @Inject(method = "swingItem", at = @At("TAIL"))
    private void blockEdit_lcWand(CallbackInfo ci) {
        ItemStack stack = getHeldItem();
        if (stack == null) {
            return;
        }

        if (getGamemode() != Gamemode.creative) {
            return;
        }

        if (stack.getData().containsKey("Wand")) {
            HitResult rayTraceResult = rayTrace(63, 1.0F, false, false);
            if (rayTraceResult != null && rayTraceResult.hitType == HitResult.HitType.TILE) {
                int x = rayTraceResult.x;
                int y = rayTraceResult.y;
                int z = rayTraceResult.z;

                int[] hitPos = new int[]{x, y, z};
                WandPlayerData.primaryPositions.put(username, hitPos);
                sendMessage(String.format("§lSet primary positions to§0: §<ff8080>%d §<80ff80>%d §<8080ff>%d", x, y, z));
            }
        }
    }
}
