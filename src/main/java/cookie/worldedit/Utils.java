package cookie.worldedit;

import net.minecraft.core.net.command.CommandError;

public class Utils {
    public static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception var3) {
            throw new CommandError("Not an integer: \"" + str + "\"");
        }
    }
}
