package fbanna.chestprotection.mixin;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.core.BlockPos;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


// Redirect CompoundContainer through BaseContainerBlockEntity by calling it on container1
@Mixin(CompoundContainer.class)
public abstract class MixinCompoundContainerCanTakeItem implements Container {


    @Shadow
    @Final
    private Container container1;

    @Override
    public boolean canTakeItem(Container into, int slot, ItemStack itemStack) {

        return ((BaseContainerBlockEntity) this.container1).canTakeItem(into, slot, itemStack);

    }
}