package com.chongnaojihua;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChongNaoJiHuaMod implements ModInitializer {
    public static final String MOD_ID = "chongnaojihua";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("虫巢计划模组已启动！");
    }
}
