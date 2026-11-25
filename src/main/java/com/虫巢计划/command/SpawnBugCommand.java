package com.虫巢计划.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PigEntity;
import com.虫巢计划.MotherBugEntity;
import com.虫巢计划.ChongNaoJiHuaMod;

/**
 * 命令: /spawnbug — 在玩家位置生成母虫
 */
public class SpawnBugCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("spawnbug")
                .executes(SpawnBugCommand::spawn)
        );
    }

    private static int spawn(CommandContext<ServerCommandSource> ctx) {
        var source = ctx.getSource();
        var player = source.getPlayer();
        if (player == null) {
            source.sendFeedback(() -> Text.literal("玩家必须在世界中"), false);
            return 0;
        }
        var world = player.getWorld();
        if (world.isClient) {
            source.sendFeedback(() -> Text.literal("只在服务器端执行"), false);
            return 0;
        }

        var bug = new MotherBugEntity(
                com.虫巢计划.entity.ModEntities.MOTHER_BUG,
                world
        );
        bug.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), 0, 0);
        world.spawnEntity(bug);

        ChongNaoJiHuaMod.registerMotherBug(bug);
        source.sendFeedback(() -> Text.literal("母虫已生成! UUID: " + bug.getUuid()), false);
        return 1;
    }
}