package fbanna.chestprotection.mixin;


import fbanna.chestprotection.ChestProtection;
import fbanna.chestprotection.protect.CheckProtected;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public class MixinLevelSetBlockState {

    @Shadow
    @Final
    private Level level;

    @Inject(method = "setBlockState", at = @At(value = "HEAD"), cancellable = true)
    private void setBlockState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir) {

//        if (!state.hasBlockEntity()) {
//            return;
//        }

        CheckProtected cp = CheckProtected.createContainerOpen(pos, this.level);

        //ChestProtection.LOGGER.info("yup were doing this");

        if (!cp.isClear()){
            cir.cancel();
        }
    }
}
