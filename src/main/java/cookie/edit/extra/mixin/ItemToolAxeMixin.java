package cookie.edit.extra.mixin;

import net.minecraft.core.block.Block;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tool.ItemTool;
import net.minecraft.core.item.tool.ItemToolAxe;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemToolAxe.class, remap = false)
public abstract class ItemToolAxeMixin extends ItemTool {
    protected ItemToolAxeMixin(String name, String namespaceId, int id, int damageDealt, ToolMaterial toolMaterial, Tag<Block<?>> tagEffectiveAgainst) {
        super(name, namespaceId, id, damageDealt, toolMaterial, tagEffectiveAgainst);
    }

    @Inject(method = "beforeDestroyBlock", at = @At("HEAD"), cancellable = true)
    private void blockEdit_cancelBreak(World world, ItemStack stack, int blockId, int x, int y, int z, Side side, Player player, CallbackInfoReturnable<Boolean> cir) {
        if (stack == null) {
            return;
        }

        if (stack.getData().containsKey("Wand")) {
            cir.setReturnValue(false);
        }

        cir.cancel();
    }
}
