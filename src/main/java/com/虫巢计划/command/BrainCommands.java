package com.虫巢计划.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调试命令: /brain status — 显示脑线程状态
 */
public class BrainCommands implements CommandRegistrationCallback {

    public static final BrainCommands INSTANCE = new BrainCommands();
    public static final Logger LOGGER = LoggerFactory.getLogger(BrainCommands.class);

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("brain")
                .then(CommandManager.literal("status")
                        .executes(BrainCommands::status))
                .then(CommandManager.literal("feed")
                        .executes(BrainCommands::feed))
        );
        LOGGER.info("注册调试命令: /brain status, /brain feed");
    }

    private static int status(CommandContext<ServerCommandSource> ctx) {
        var brain = com.虫巢计划.ChongNaoJiHuaMod.getBrain();
        if (brain == null) {
            ctx.getSource().sendFeedback(() -> Text.literal("脑线程未初始化"), false);
            return 0;
        }
        var snap = brain.readSnapshot();
        ctx.getSource().sendFeedback(() -> Text.literal(
                String.format("脑线程: running=%s, tick=%d, action=%s, conf=%.2f",
                        brain.isRunning(),
                        snap.producingTick,
                        snap.action,
                        snap.confidence)
        ), false);
        return 1;
    }

    private static int feed(CommandContext<ServerCommandSource> ctx) {
        var world = ctx.getSource().getWorld();
        if (world == null || world.isClient) {
            ctx.getSource().sendFeedback(() -> Text.literal("只在服务器端执行"), false);
            return 0;
        }
        var brain = com.虫巢计划.ChongNaoJiHuaMod.getBrain();
        if (brain == null) {
            ctx.getSource().sendFeedback(() -> Text.literal("脑线程未初始化"), false);
            return 0;
        }
        com.虫巢计划.WorldSensor.collect(world, ctx.getSource().getPlayer(), null);
        brain.feed(com.虫巢计划.WorldSensor.peek());
        ctx.getSource().sendFeedback(() -> Text.literal("手动feed触发"), false);
        return 1;
    }
}