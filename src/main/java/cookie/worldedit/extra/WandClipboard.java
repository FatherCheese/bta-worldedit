package cookie.worldedit.extra;

import net.minecraft.core.world.chunk.ChunkPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WandClipboard {
    public List<HashMap<ChunkPosition, int[]>> history = new ArrayList<>();
    public int page = -1;

    public void createNewPage() {
        if (page < history.size()-1) {
            if (page + 1 < history.size()) {
                history.subList(page + 1, history.size()).clear();
            }
        }

        history.add(new HashMap<>());
        page += 1;
    }

    public Map<ChunkPosition, int[]> getCurrentPage() {
        return history.get(page);
    }

    public void putBlock(int x, int y, int z, int id, int meta) {
        this.getCurrentPage().put(new ChunkPosition(x, y, z), new int[]{id, meta});
    }

    public boolean nextPage() {
        page += 1;
        if (page >= history.size()) {
            page = history.size()-1;
            return true;
        }
        return false;
    }

    public boolean previousPage() {
        page -= 1;
        if (page < 0) {
            page = 0;
            return true;
        }
        return false;
    }

}
