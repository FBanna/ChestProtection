package fbanna.chestprotection.mixin;

import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(BaseContainerBlockEntity.class)
public abstract class MixinContainerCanTakeItem
        extends BlockEntity
        implements Container {

    private MixinContainerCanTakeItem(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }


    @Override
    public boolean canTakeItem(Container into, int slot, ItemStack itemStack) {

        CheckProtected cp = CheckProtected.createContainerOpen(this.getBlockPos(), getLevel());
        return cp.isClear();

    }

}
