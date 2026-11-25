package com.虫巢计划;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.虫巢计划.entity.ModEntities;
import com.虫巢计划.command.BrainCommands;
import com.虫巢计划.command.SpawnBugCommand;

/**
 * Главная точка входа. Регистрирует singleton мозга, обрабатывает старт/стоп сервера,
 * тиковый пайплайн «сенсор → мозг → snapshot → application» для MotherBugEntity,
 * а также регистрирует сущность и команды.
 */
public class ChongNaoJiHuaMod implements ModInitializer {

    public static final String MOD_ID = "虫巢计划";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static volatile MotherBrainThread brain;
    private static volatile MinecraftServer server;
    private static volatile MotherBugEntity motherBugInstance;

    @Override
    public void onInitialize() {
        LOGGER.info("虫巢计划模组 загружается...");

        // 注册实体
        ModEntities.registerEntities();

        // 脑线程 — стартовать сразу, ждать реального мира не нужно
        brain = new MotherBrainThread();
        brain.setDaemon(true);
        brain.startBrain();

        // Старт/стоп сервера
        ServerLifecycleEvents.SERVER_STARTING.register(s -> {
            server = s;
            LOGGER.info("Сервер запущен, brain активен");
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(s -> {
            if (brain != null) brain.stopBrain();
            server = null;
            LOGGER.info("Сервер остановлен, brain выключен");
        });

        // Тик-хук: собирать сенсор и фидить мозг
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!(world instanceof ServerWorld sw)) return;
            if (brain == null || !brain.isRunning()) return;
            if (motherBugInstance == null || !motherBugInstance.isAlive()) return;

            WorldSensor.collect(sw, null, motherBugInstance);
            brain.feed(WorldSensor.peek());
        });

        // Команды
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            BrainCommands.INSTANCE.register(dispatcher);
            SpawnBugCommand.register(dispatcher);
        });

        LOGGER.info("虫巢计划模组 загружен успешно");
    }

    public static MotherBrainThread getBrain() {
        return brain;
    }

    public static void registerMotherBug(MotherBugEntity bug) {
        motherBugInstance = bug;
        LOGGER.info("母虫 регистирован: {}", bug.getUuid());
    }
}