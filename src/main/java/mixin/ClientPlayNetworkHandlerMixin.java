package mixin;

import com.client.event.events.GameEvent;
import com.client.event.events.LostOfTotemEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.TeamS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin implements ClientPlayPacketListener {
    @Shadow private ClientWorld world;
    @Shadow private MinecraftClient client;
    @Unique private boolean worldNotNull;

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void onGameJoinHead(GameJoinS2CPacket packet, CallbackInfo info) {
        worldNotNull = world != null;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void onEntityTrackerUpdate(EntityTrackerUpdateS2CPacket packet) {
        try {
            NetworkThreadUtils.forceMainThread(packet, this, this.client);
            Entity entity = this.world.getEntityById(packet.id());
            if (entity != null && packet.getTrackedValues() != null) {
                entity.getDataTracker().writeUpdatedEntries(packet.getTrackedValues());
            }
        } catch (Exception ignore) {
        }
    }

    @Inject(method = "onGameJoin", at = @At("TAIL"), cancellable = true)
    private void onGameJoinTail(GameJoinS2CPacket packet, CallbackInfo info) {
        if (worldNotNull) {
            GameEvent.Left left = new GameEvent.Left(packet);
            left.post();
            if (left.isCancelled()) {
                info.cancel();
            }
        }

        GameEvent.Join join = new GameEvent.Join(packet);
        join.post();
        if (join.isCancelled()) {
            info.cancel();
        }
    }

    @Inject(method = "onEntityStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleManager;addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;I)V"), cancellable = true)
    private void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        LostOfTotemEvent event = new LostOfTotemEvent(packet.getEntity(world));
        event.post();
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    /**
     * @author .
     * @reason .
     */
    @Overwrite
    public void onTeam(TeamS2CPacket packet) {

        try {
            NetworkThreadUtils.forceMainThread(packet, this, this.client);
            Scoreboard scoreboard = this.world.getScoreboard();
            Team team;
            if (packet.getMode() == 0) {
                team = scoreboard.addTeam(packet.getTeamName());
            } else {
                team = scoreboard.getTeam(packet.getTeamName());
            }

            if (packet.getMode() == 0 || packet.getMode() == 2) {
                team.setDisplayName(packet.getDisplayName());
                team.setColor(packet.getPlayerPrefix());
                team.setFriendlyFlagsBitwise(packet.getFlags());
                AbstractTeam.VisibilityRule visibilityRule = AbstractTeam.VisibilityRule.getRule(packet.getNameTagVisibilityRule());
                if (visibilityRule != null) {
                    team.setNameTagVisibilityRule(visibilityRule);
                }

                AbstractTeam.CollisionRule collisionRule = AbstractTeam.CollisionRule.getRule(packet.getCollisionRule());
                if (collisionRule != null) {
                    team.setCollisionRule(collisionRule);
                }

                team.setPrefix(packet.getPrefix());
                team.setSuffix(packet.getSuffix());
            }

            Iterator var6;
            String string;
            if (packet.getMode() == 0 || packet.getMode() == 3) {
                var6 = packet.getPlayerList().iterator();

                while (var6.hasNext()) {
                    string = (String) var6.next();
                    scoreboard.addPlayerToTeam(string, team);
                }
            }

            if (packet.getMode() == 4) {
                var6 = packet.getPlayerList().iterator();

                while (var6.hasNext()) {
                    string = (String) var6.next();
                    scoreboard.removePlayerFromTeam(string, team);
                }
            }

            if (packet.getMode() == 1) {
                scoreboard.removeTeam(team);
            }
        } catch (Exception ignore) {
        }
    }
}