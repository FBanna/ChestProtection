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

        //if (state.getBlock() == Blocks.CHEST)
        CheckProtected cp = CheckProtected.createContainerOpen(pos, this.level);
//
//        if (cp.isClear()) {
//            return;
//        }
//
//        state.hasBlockEntity()
//
//        CheckProtected cp = CheckProtected.createContainerOpen()

        if (!cp.isClear()){
            cir.cancel();
        }
    }
}
