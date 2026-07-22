package fbanna.chestprotection.mixin;

import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mixin(BlockBehaviour.class)
public class MixinDrops {

    @Inject(method = "getDrops", at = @At(value = "HEAD"), cancellable = true)
    private void inject(BlockState state, LootParams.Builder params, CallbackInfoReturnable<List<ItemStack>> cir) {

        BlockEntity result = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY); // find block entity because why not

        if (result != null) {

            CheckProtected cp = CheckProtected.createContainerOpen(result.getBlockPos(), params.getLevel());

            if (!cp.isClear()) {
                cir.setReturnValue(Collections.emptyList());
            }

        }


    }

}
