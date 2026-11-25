package com.虫巢计划;

import com.虫巢计划.model.BrainSnapshot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Мать-червь — первый сущностный агент. Наследуется от PathAwareEntity (базовый класс для entity с pathfinding),
 * но运动指令 применяется из внешнего мозга (MotherBrainThread).
 * На тик считывает Snapshot и записывает velocity/attack.
 */
public class MotherBugEntity extends PathAwareEntity {

    public static final Logger LOGGER = LoggerFactory.getLogger(MotherBugEntity.class);
    private static final float BRAIN_SPEED = 0.18f;

    private final MotherBrainThread brain;
    private long lastAppliedTick = -1;

    public MotherBugEntity(net.minecraft.entity.EntityType<? extends PathAwareEntity> type, net.minecraft.world.World world) {
        super(type, world);
        this.brain = ChongNaoJiHuaMod.getBrain();
        // отключаем стандартные goal-ы, чтобы управление шло из мозга
        this.goalSelector.clear();
        this.targetSelector.clear();
    }

    public static net.minecraft.entity.attribute.DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED, BRAIN_SPEED)
                .add(net.minecraft.entity.attribute.EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.getWorld().isClient) {
            applyBrainTick();
        }
    }

    private void applyBrainTick() {
        BrainSnapshot snap = brain.readSnapshot();
        if (!snap.valid) return;
        if (snap.producingTick <= lastAppliedTick) return;

        float forward = snap.moveForward;
        float strafe = snap.strafe;
        if (forward != 0.0f || strafe != 0.0f) {
            float yaw = (float) Math.toRadians(this.getYaw());
            float dx = (float) (forward * -Math.sin(yaw) + strafe * Math.cos(yaw));
            float dz = (float) (forward * Math.cos(yaw) + strafe * -Math.sin(yaw));
            this.setVelocity(dx * BRAIN_SPEED, this.getVelocity().y, dz * BRAIN_SPEED);
        }
        if (snap.jump) {
            this.jump();
        }
        if (snap.attack && this.isAlive()) {
            // атакующий хук — переопределить под конкретных врагов
        }
        lastAppliedTick = snap.producingTick;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }
}