package com.solexgames.arvendium.utils.command;

import com.solexgames.arvendium.Arvendium;
import com.solexgames.arvendium.ArvendiumConfig;

public abstract class BaseCommand {

    public Arvendium main;
    public ArvendiumConfig configFile;

    public BaseCommand() {
        this.main = Arvendium.getInstance();
        this.configFile = this.main.getConfigFile();
    }

    public abstract void onCommand(CommandArgs p0);
}
