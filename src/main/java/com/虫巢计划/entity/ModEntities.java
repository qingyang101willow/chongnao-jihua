package com.虫巢计划.entity;

import com.虫巢计划.ChongNaoJiHuaMod;
import com.虫巢计划.MotherBugEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Регистрирует EntityType и атрибуты MotherBugEntity.
 */
public class ModEntities {

    public static final Logger LOGGER = LoggerFactory.getLogger(ModEntities.class);

    // ID母虫
    public static final EntityType<MotherBugEntity> MOTHER_BUG = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(ChongNaoJiHuaMod.MOD_ID, "mother_bug"),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, MotherBugEntity::new)
                    .dimensions(EntityDimensions.fixed(1.5f, 1.0f)) // 宽1.5，高1.0
                    .build()
    );

    public static void registerEntities() {
        LOGGER.info("注册实体: mother_bug");
        FabricDefaultAttributeRegistry.register(MOTHER_BUG, MotherBugEntity.createAttributes());
    }
}