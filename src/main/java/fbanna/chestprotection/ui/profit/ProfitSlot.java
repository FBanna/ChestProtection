package fbanna.chestprotection.ui.profit;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

public class ProfitSlot extends Slot {
    public ProfitSlot(Container container, int slot) {
        super(container, slot, 0, 0);
    }

    // This needs to exist for some reason :)
    @Override
    public boolean mayPlace(@NonNull ItemStack itemStack) {
        return false;
    }


}
