package com.虫巢计划;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChongNaoJiHuaMod implements ModInitializer {
    public static final String MOD_ID = "虫巢计划";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // 模组初始化入口
        // 虫巢计划 - 实现蜂群智能的Minecraft模组
        LOGGER.info("虫巢计划模组已启动！");
        
        // 第一阶段目标：搭 Fabric 环境，造空壳母虫，跑通"感官→脑线程→运动"最小闭环
        // TODO: 实现母虫神经层 (MotherWormNeuralLayer)
        // TODO: 实现感官编码 (SensoryFrame)
        // TODO: 实现意图提取 (IntentExtraction)
        // TODO: 实现四子代理层 (FourSubAgentLayer)
        // TODO: 实现普通虫执行层 (CommonWormExecutionLayer)
    }
}
