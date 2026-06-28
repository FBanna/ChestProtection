package fbanna.chestprotection.mixin;

import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(BaseContainerBlockEntity.class)
public abstract class MixinContainerCanTakeItem
        extends BlockEntity
        implements Container,
        MenuProvider,
        Nameable {


    @Overwrite
    public boolean canTakeItem(Container into, int slot, ItemStack itemStack) {
//
//        CheckProtected.createContainerOpen()

        CheckProtected.ProtectedStatus status = CheckProtected.getStatus(this.getItem(0));

        if (status != CheckProtected.ProtectedStatus.CLEAR) {
            return false;
        }

        return true;

    }

}
