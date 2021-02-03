package com.solexgames.arvendium.util.command;

import com.solexgames.arvendium.ArvendiumPlugin;
import com.solexgames.arvendium.util.file.ConfigFile;

public abstract class BaseCommand {

    public ArvendiumPlugin main;
    public ConfigFile configFile;

    public BaseCommand() {
        this.main = ArvendiumPlugin.getInstance();
        this.configFile = this.main.getConfigFile();
    }

    public abstract void onCommand(CommandArgs p0);
}
