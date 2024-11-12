package mixin;

import java.util.Map;

import com.client.utils.render.ScoreboardEvent;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Scoreboard.class})
public class ScoreboardMixin {
    @Shadow
    private Map<String, Team> teamsByPlayer;

    @Inject(
            at = {@At("TAIL")},
            method = {"addPlayerToTeam"}
    )
    public void addPlayerToTeam(String playerName, Team team, CallbackInfoReturnable<Boolean> ci) {
        ScoreboardEvent.trigger();
    }

    @Inject(
            at = {@At("TAIL")},
            method = {"removePlayerFromTeam"}
    )
    public void removePlayerFromTeam(String playerName, Team team, CallbackInfo ci) {
        ScoreboardEvent.trigger();
    }

    @Shadow
    public Team getPlayerTeam(String playerName) {
        return null;
    }


}