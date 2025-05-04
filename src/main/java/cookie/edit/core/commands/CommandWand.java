package cookie.edit.core.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilderLiteral;
import com.mojang.brigadier.builder.ArgumentBuilderRequired;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.command.CommandManager;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeEntity;
import net.minecraft.core.net.command.helpers.EntitySelector;
import net.minecraft.core.net.command.util.CommandHelper;

import java.util.List;

public class CommandWand implements CommandManager.CommandRegistry {
    private static final SimpleCommandExceptionType FAILURE = new SimpleCommandExceptionType(() -> I18n.getInstance().translateKey("command.commands.wand.exception_failure"));

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        // Command Registry (name and OP)
        ArgumentBuilderLiteral<CommandSource> command = ArgumentBuilderLiteral.literal("/wand");
        command.requires(CommandSource::hasAdmin);

        // Arguments for the command
        ArgumentBuilderRequired<CommandSource, EntitySelector> targetArgument = ArgumentBuilderRequired.argument("target", ArgumentTypeEntity.usernames());

        // Command execution
        targetArgument.executes(context -> {
            CommandSource source = context.getSource();
            EntitySelector entitySelector = context.getArgument("target", EntitySelector.class);
            List<? extends Entity> entities = entitySelector.get(source);

            ItemStack wandAxe = new ItemStack(Items.TOOL_AXE_WOOD);
            CompoundTag tags = new CompoundTag();
            tags.putByte("Wand", (byte)1);
            wandAxe.setData(tags);

            for (Entity player : entities) {
                ((Player)player).inventory.insertItem(wandAxe, true);
            }

            if (entities.isEmpty()) {
                throw FAILURE.create();
            } else if (entities.size() == 1) {
                source.sendTranslatableMessage("command.commands.give.success_single_entity", CommandHelper.getEntityName(entities.get(0)), 1, wandAxe.getDisplayName());
            } else {
                source.sendTranslatableMessage("command.commands.give.success_single_entity", entities.size(), 1, wandAxe.getDisplayName());
            }

            return 1;
        });

        // The command structure
        command.then(targetArgument);

        // Finally, register
        dispatcher.register(command);
    }
}
