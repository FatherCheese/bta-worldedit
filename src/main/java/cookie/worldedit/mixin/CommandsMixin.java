package cookie.worldedit.mixin;

import cookie.worldedit.commands.*;
import net.minecraft.core.net.command.Command;
import net.minecraft.core.net.command.Commands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = Commands.class, remap = false)
public abstract class CommandsMixin {

    @Shadow public static List<Command> commands;

    @Inject(method = "initCommands", at = @At("TAIL"))
    private static void worldEdit_initCommands(CallbackInfo ci) {
        commands.add(new CommandButcher());
        commands.add(new CommandClearClipboard());
        commands.add(new CommandCopy());
        commands.add(new CommandCut());
        commands.add(new CommandCylinder());
        commands.add(new CommandCylinderHollow());
        commands.add(new CommandLine());
        commands.add(new CommandPaste());
        commands.add(new CommandRedo());
        commands.add(new CommandReplace());
        commands.add(new CommandSet());
        commands.add(new CommandSetHollow());
        commands.add(new CommandSetPos1());
        commands.add(new CommandSetPos2());
        commands.add(new CommandSphere());
        commands.add(new CommandSphereHollow());
        commands.add(new CommandStack());
        commands.add(new CommandUndo());
        commands.add(new CommandUp());
        commands.add(new CommandWand());
    }
}
