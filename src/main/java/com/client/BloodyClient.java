package com.client;

import api.interfaces.EventHandler;
import com.client.event.events.TickEvent;
import com.client.impl.function.client.ClickGui;
import com.client.impl.function.visual.MotionBlur;
import com.client.interfaces.IClientConnection;
import com.client.system.companion.CompanionRegistry;
import com.client.system.function.FunctionManager;
import com.client.system.gps.GpsManager;
import com.client.utils.auth.Encryptor;
import com.client.utils.auth.Loader;
import com.client.utils.changelog.ChangeLog;
import com.client.utils.math.vector.BloodyExecutor;
import com.client.utils.optimization.EntityCullingBase;
import com.client.utils.render.Fonts;
import com.client.utils.render.Matrices;
import com.client.utils.render.gl.PostProcessRenderer;
import com.client.utils.render.wisetree.font.main.IFont;
import com.client.utils.render.wisetree.render.render2d.utils.shader.Shader;
import com.client.utils.render.wisetree.render.render2d.utils.shader.shaders.OutlineShader;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;

public class BloodyClient implements ModInitializer, ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("bloody-client");
	public static final MinecraftClient mc = MinecraftClient.getInstance();
	public static final File FOLDER = new File(FabricLoader.getInstance().getGameDir().toString(), "bloody-client");
	public static final File GPS_FOLDER = new File(FabricLoader.getInstance().getGameDir().toString(), "assets");
	public static final File UNHOOK_FOLDER = new File(FabricLoader.getInstance().getGameDir().toString(), "sessions");
	public static final String REPORT_WEBHOOK = Encryptor.decrypt("nmyLCLOG21nGewzmS/21vlcsgpDpwLi7DVdnnR/UmxxpWjNHyZGWGUhnqmIYzIz+UAHjqKZ7p3QrjytEHSFuvxANGP59JQ3JlFIhcFmFa2W6j75GV5Nri1NtGn36YHEIcMoV55ep3IWPbpYqsoo1ZzCgyWNbK3Ppg2iAcYDK1VE=");
	public static long initTime;
	public static Shader shader;
	public static OutlineShader shaderManager = new OutlineShader();
	private float currentBlur;
	private final ManagedShaderEffect motionblur = ShaderEffectManager.getInstance().manage(new Identifier("bloody-client", "shaders/post/motion_blur.json"), (shader) -> {
		shader.setUniformValue("BlendFactor", this.getBlur());
	});;

	@Override
	public void onInitializeClient() {
		CompanionRegistry.registerEntityRenderers();
	}

	@Override
	public void onInitialize() {
		GeckoLib.initialize();
		CompanionRegistry.ENTITIES.register();
		CompanionRegistry.onAttributeCreation();
		Loader.unHook = UNHOOK_FOLDER.exists();
		Loader.load();
		new EntityCullingBase().onInitialize();
		ShaderEffectRenderCallback.EVENT.register((deltaTick) -> {
			if (this.getBlur() != 0.0F) {
				if (this.currentBlur != this.getBlur()) {
					this.motionblur.setUniformValue("BlendFactor", this.getBlur());
					this.currentBlur = this.getBlur();
				}

				this.motionblur.render(deltaTick);
			}
		});
	}

	public float getBlur() {
		MotionBlur motionBlur = FunctionManager.get(MotionBlur.class);
		if (motionBlur == null || !motionBlur.isEnabled()) return 0;

		return (float)Math.min(motionBlur.smoothness.get(), 99) / 100.0F;
	}

	@EventHandler
	private void onTick(TickEvent.Pre event) {
		FunctionManager.get(ClickGui.class).updateColor();
	}

	public static void onPostWindowInitialize() {
		IFont.init();
		Matrices.begin(new MatrixStack());
		PostProcessRenderer.init();
		Fonts.init();
		GpsManager.init();
		ChangeLog.init();
		initTime = System.currentTimeMillis();
		shader = new Shader(Shader.mainMenuShader);
	}

	public static boolean canUpdate() {
		return mc != null && mc.world != null && mc.player != null;
	}

	public static String getTime() {
		return LocalTime.now().toString().split("\\.")[0];
	}

	public static String getDate() {
		return LocalDate.now().toString();
	}

	public static void run(Runnable task, long ms) {
		BloodyExecutor.execute(() -> {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

			task.run();
        });
	}

	public static void sendPacket(Packet<?> packet) {
		if (mc.player.networkHandler == null) return;
		((IClientConnection) mc.player.networkHandler).sendPacket(packet);
	}
}