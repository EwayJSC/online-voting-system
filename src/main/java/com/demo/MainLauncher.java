package com.demo;

import com.demo.verticle.standard.MainVerticle;
import io.vertx.core.Launcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Tu Pham Phuong - phamptu@gmail.com on 2/27/20.
 */
public class MainLauncher extends Launcher {
    private static Logger logger = LoggerFactory.getLogger(MainLauncher.class);

    public static void main(String[] args) {
        for (String arg : args) {
            logger.info("arg " + arg);
        }

        new Launcher().dispatch(args);
    }
}
