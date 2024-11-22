package fbanna.chestprotection.mixin;

import java.util.List;

import fbanna.chestprotection.check.CheckChest;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(Explosion.class)
public abstract class MixinExplosion {

    @Shadow
    @Final
    private World world;

    @Shadow
    public abstract List<BlockPos> getAffectedBlocks();

    @Inject(method = "collectBlocksAndDamageEntities", at = @At("TAIL"))
    private void prevent_explosion(CallbackInfo info) {
        getAffectedBlocks().removeIf(pos -> {
            BlockEntity block = world.getBlockEntity(pos);

            if (block instanceof ChestBlockEntity) {

                CheckChest book = new CheckChest(pos, world);
                if (book.chestStatus != CheckChest.status.CLEAR){

                    return true;

                }

            }

            return false;


        });
    }
}