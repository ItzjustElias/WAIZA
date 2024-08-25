package elias.zoom.mixin;

import elias.zoom.client.ZoomModClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Mouse.class)
public class MousewheelAcc {

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    public void onScrollEvent(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (ZoomModClient.isZooming()) {
            if (vertical != 0) {
                if (vertical < 0) {
                    ZoomModClient.zoomIn();
                }
                if (vertical > 0) {
                    ZoomModClient.zoomOut();
                }
                // Cancel the event to prevent default scrolling behavior when zooming
                ci.cancel();
            }
        }
    }
}
