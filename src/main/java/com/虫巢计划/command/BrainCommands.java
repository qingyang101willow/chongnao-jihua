package com.虫巢计划.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * 调试命令: /brain status — 显示脑线程状态
 */
public class BrainCommands implements CommandRegistrationCallback {

    public static final BrainCommands INSTANCE = new BrainCommands();

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("brain")
                .then(CommandManager.literal("status")
                        .executes(BrainCommands::status))
                .then(CommandManager.literal("reset")
                        .executes(BrainCommands::reset))
        );
    }

    private static int status(CommandContext<ServerCommandSource> ctx) {
        var brain = com.虫巢计划.ChongNaoJiHuaMod.getBrain();
        if (brain == null) {
            ctx.getSource().sendFeedback(() -> Text.literal("脑线程未初始化"), false);
            return 0;
        }
        
        var snap = brain.readSnapshot();
        var buf = brain.frameBuffer;
        
        String msg = String.format(
                "脑线程状态:\n" +
                "  运行: %s\n" +
                "  Tick: %d\n" +
                "  Action: %s\n" +
                "  Confidence: %.2f\n" +
                "  缓冲大小: %d/%d\n" +
                "  丢帧率: %.1f%%",
                brain.isRunning(),
                snap.producingTick,
                snap.action,
                snap.confidence,
                buf.size(),
                64,
                buf.getDropRate() * 100
        );
        
        ctx.getSource().sendFeedback(() -> Text.literal(msg), false);
        return 1;
    }

    private static int reset(CommandContext<ServerCommandSource> ctx) {
        var brain = com.虫巢计划.ChongNaoJiHuaMod.getBrain();
        if (brain == null) {
            ctx.getSource().sendFeedback(() -> Text.literal("脑线程未初始化"), false);
            return 0;
        }
        brain.frameBuffer.resetStats();
        ctx.getSource().sendFeedback(() -> Text.literal("统计已重置"), false);
        return 1;
    }
}
