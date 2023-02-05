package azurelmao.worldedit;

import net.minecraft.src.ChunkPosition;

import java.util.ArrayList;
import java.util.HashMap;

public class WandClipboard {
    public int page = -1;

    public ArrayList<HashMap<ChunkPosition, int[]>> history = new ArrayList<>();

    public void createNewPage() {
        if (page < history.size()-1) {
            if (page + 1 < history.size()) {
                history.subList(page + 1, history.size()).clear();
            }
        }

        history.add(new HashMap<>());
        page += 1;
    }

    public HashMap<ChunkPosition, int[]> getCurrentPage() {
        return history.get(page);
    }

    public void putBlock(int x, int y, int z, int id, int meta) {
        this.getCurrentPage().put(new ChunkPosition(x, y, z), new int[]{id, meta});
    }

    public void lastPage() {
        page = history.size()-1;
    }

    public void firstPage() {
        page = 0;
    }

    public boolean nextPage() {
        page += 1;
        if (page >= history.size()) {
            this.lastPage();
            return true;
        }
        return false;
    }

    public boolean previousPage() {
        page -= 1;
        if (page < 0) {
            this.firstPage();
            return true;
        }
        return false;
    }

}