package com.anagorny.chaters.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: sosnov
 * Date: 04.01.14
 * Time: 18:19
 * To change this template use File | Settings | File Templates.
 */
public class Config {
    private static final String CFG_FILE = "./config.cfg";
    private static int PORT;

    static {
        Properties prop = new Properties();
        FileInputStream propFile = null;
        try {
            propFile = new FileInputStream(CFG_FILE);
            prop.load(propFile);
            PORT = Integer.parseInt(prop.getProperty("PORT"));
        } catch (FileNotFoundException fnf) {
            System.err.println("Config file not founded in \"" + CFG_FILE + "'\"");
        } catch (IOException ioe) {
            System.err.println("Not corrected Config file in \"" + CFG_FILE + "'\"");
            ioe.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            try {
                propFile.close();
            } catch (IOException ioe) {
                System.err.println("Error while closed  config file.");
                ioe.printStackTrace();
            }
        }

    }

    public static int PORT() {
        return PORT;
    }

}
