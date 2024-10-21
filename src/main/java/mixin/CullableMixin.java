package mixin;

import com.client.impl.function.client.Optimization;
import com.client.system.function.FunctionManager;
import com.client.utils.optimization.EntityCullingBase;
import com.client.utils.optimization.interfaces.Cullable;
import net.minecraft.entity.Entity;
import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Entity.class, BlockEntity.class})
public class CullableMixin implements Cullable {
    private long lasttime = 0L;
    private boolean culled = false;
    private boolean outOfCamera = false;

    public void setTimeout() {
        this.lasttime = System.currentTimeMillis() + 1000L;
    }

    public boolean isForcedVisible() {
        return this.lasttime > System.currentTimeMillis();
    }

    public void setCulled(boolean value) {
        this.culled = value;
        if (!value) {
            this.setTimeout();
        }
    }

    public boolean isCulled() {
        return (!FunctionManager.get(Optimization.class).isEnabled() || !FunctionManager.get(Optimization.class).rayTrace.get()) ? false : this.culled;
    }

    public void setOutOfCamera(boolean value) {
        this.outOfCamera = value;
    }

    public boolean isOutOfCamera() {
        return (!FunctionManager.get(Optimization.class).isEnabled() || !FunctionManager.get(Optimization.class).rayTrace.get()) ? false : this.outOfCamera;
    }
}