package me.joram.mcbtc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {

        FileConfiguration config = getConfig();

        // registration of Listeners
        getServer().getPluginManager().registerEvents(new MainListener(this), this);
        getServer().getPluginManager().registerEvents(new LedgerListener(this), this);

        // registration of Commands
        this.getCommand("wallet").setExecutor(new Commands(this));
        this.getCommand("btcpay").setExecutor(new Commands(this));
        this.getCommand("gamble").setExecutor(new Commands(this));

        // Alfa
        this.getCommand("ledger").setExecutor(new LedgerCommands(this));

    }

}
