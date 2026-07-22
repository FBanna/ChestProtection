package fbanna.chestprotection.mixin;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BaseContainerBlockEntity.class)
public abstract class MixinBaseContainerCanTakeItem
        extends BlockEntity
        implements Container {

    private MixinBaseContainerCanTakeItem(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }


    @Override
    public boolean canTakeItem(Container into, int slot, ItemStack itemStack) {

        CheckProtected cp = CheckProtected.createContainerOpen(this.getBlockPos(), getLevel());
        return cp.isClear();

    }

}