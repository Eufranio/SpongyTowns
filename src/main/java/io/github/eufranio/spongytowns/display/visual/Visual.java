package io.github.eufranio.spongytowns.display.visual;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import io.github.eufranio.spongytowns.SpongyTowns;
import io.github.eufranio.spongytowns.interfaces.Claim;
import io.github.eufranio.spongytowns.util.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Frani on 08/03/2018.
 */
public abstract class Visual {

    public List<UUID> viewers = Lists.newArrayList();
    public List<BlockLocation> blocks = Lists.newArrayList();
    private Task task;
    private Player p;

    @Getter
    public static List<Visual> visuals = Lists.newArrayList();

    public Visual(Player... viewers) {
        for (Player p : viewers) {
            this.viewers.add(p.getUniqueId());
        }
        this.p = viewers[0];
    }

    public abstract boolean isVisualValid();

    public abstract void populateBlocks(Player firstViewer);

    public boolean useConsumer() {
        return false;
    }

    public BiConsumer<Player, BlockLocation> getConsumer() {
        return null;
    }

    public void add(Visual other) {
        this.blocks.addAll(other.blocks);
        this.viewers.addAll(other.viewers);
    }

    public void start() {
        this.populateBlocks(p);
        this.task = Task.builder()
                .intervalTicks(10)
                .execute(this::tick)
                .submit(SpongyTowns.getInstance());

        Task.builder()
                .delay(15, TimeUnit.SECONDS) //TODO: change this
                .execute(this::stop)
                .submit(SpongyTowns.getInstance());
    }

    @Getter
    @RequiredArgsConstructor
    public static class BlockLocation {
        private final BlockType block;
        private final Vector3i location;
        private final World world;
    }

    public void tick() {
        this.viewers.stream()
                .map(Sponge.getServer()::getPlayer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(p -> {
                            if (!this.isVisualValid()) {
                                this.blocks.clear();
                                this.populateBlocks(p);
                            }
                            this.blocks.forEach(block -> {
                                if (this.useConsumer()) {
                                    this.getConsumer().accept(p, block);
                                } else {
                                    if (p.getWorld().getUniqueId().equals(block.getWorld().getUniqueId())) {
                                        p.sendBlockChange(block.getLocation(), block.getBlock().getDefaultState());
                                    }
                                }
                            });
                        }
                );
    }

    public void stop() {
        this.task.cancel();
        this.task = null;
        this.viewers.stream()
                .map(Sponge.getServer()::getPlayer)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(p ->
                    this.blocks.forEach(block -> p.resetBlockChange(block.getLocation()))
                );
        visuals.remove(this);
    }

}
