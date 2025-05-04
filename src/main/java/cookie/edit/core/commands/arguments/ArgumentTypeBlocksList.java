package cookie.edit.core.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public class ArgumentTypeBlocksList implements ArgumentType<ArgumentTypeBlocksList.BlockListEntry[]> {
    private static final ArgumentTypeBlocksList INSTANCE = new ArgumentTypeBlocksList();

    public static ArgumentTypeBlocksList blockList() {
        return INSTANCE;
    }

    @Override
    public BlockListEntry[] parse(StringReader reader) throws CommandSyntaxException {
        // The key is to properly consume the input from the StringReader
        // Make sure to skip any whitespace before starting
        reader.skipWhitespace();

        // Read until the end of input or until we hit another command argument
        StringBuilder input = new StringBuilder();
        while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
            input.append(reader.read());
        }

        String blockListStr = input.toString();

        // Split by commas
        String[] parts = blockListStr.split(",");
        BlockListEntry[] result = new BlockListEntry[parts.length];

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            float chance = 100.0f; // Default 100% chance

            // Check if it has a chance percentage
            if (part.contains("%")) {
                String[] chanceAndBlock = part.split("%", 2);
                try {
                    chance = Float.parseFloat(chanceAndBlock[0]);
                    part = chanceAndBlock[1];
                } catch (NumberFormatException e) {
                    throw new CommandSyntaxException(
                            new SimpleCommandExceptionType(new LiteralMessage("Invalid chance percentage")),
                            new LiteralMessage("Invalid chance: " + chanceAndBlock[0])
                    );
                }
            }

            // Parse the ID:meta format
            String[] idAndMeta = part.split(":", 2);
            int blockId;
            int meta = 0; // Default meta

            try {
                blockId = Integer.parseInt(idAndMeta[0]);

                // Parse meta if provided
                if (idAndMeta.length > 1) {
                    meta = Integer.parseInt(idAndMeta[1]);
                }
            } catch (NumberFormatException e) {
                throw new CommandSyntaxException(
                        new SimpleCommandExceptionType(new LiteralMessage("Invalid block ID or meta")),
                        new LiteralMessage("Invalid format: " + part)
                );
            }

            result[i] = new BlockListEntry(blockId, meta, chance);
        }

        return result;
    }

    public static class BlockListEntry {
        private final int blockId;
        private final int meta;
        private final float chance;

        public BlockListEntry(int blockId, int meta, float chance) {
            this.blockId = blockId;
            this.meta = meta;
            this.chance = chance;
        }

        public int getBlockId() { return blockId; }
        public int getMeta() { return meta; }
        public float getChance() { return chance; }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        builder.suggest("1:0");     // Stone
        builder.suggest("200:0");   // Grass
        builder.suggest("220:0");   // Dirt

        return builder.buildFuture();
    }
}
