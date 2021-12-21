package me.joram.mcbtc;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.md_5.bungee.api.ChatColor;

public class MainListener implements Listener {

    private Main main;
    private NumberFormat nf;
    private Commands command;

    public MainListener(Main main) {
        this.main = main;
        command = new Commands(main);
        nf = new DecimalFormat(
                "################################################.###########################################");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String stringAmount = nf.format(command.getCurrentAmount(event.getPlayer()));
        event.getPlayer().sendMessage(
                ChatColor.BLUE + "Welcome back! Your current balance of your wallet is: " + stringAmount + " BTC!");
    }

    @EventHandler
    public void breakStoneGetBitcoin(BlockBreakEvent event) {

        if ((event.getBlock().getType() == Material.DIAMOND_ORE)) {
            command.mineRealBTC(event.getPlayer(), 1.0);

        } else if ((event.getBlock().getType() == Material.GOLD_ORE)) {
            command.mineRealBTC(event.getPlayer(), 0.7);

        } else if ((event.getBlock().getType() == Material.IRON_ORE)) {
            command.mineRealBTC(event.getPlayer(), 0.1);

        } else if ((event.getBlock().getType() == Material.COPPER_ORE)) {
            command.mineRealBTC(event.getPlayer(), 0.1);

        } else if ((event.getBlock().getType() == Material.REDSTONE_ORE)) {
            command.mineRealBTC(event.getPlayer(), 0.05);

        }
    }

    @EventHandler
    public void whenPlayerDies(PlayerDeathEvent event) {
        Commands command = new Commands(main);
        if (event.getEntity() instanceof Player) {
            Player player = event.getEntity();
            Double newAmount = (command.getCurrentAmount(player) * 0.25); // -75%
            Double removedAmount = (newAmount * 3); // the 75%

            command.setBalancePlayer((newAmount), player);
            String stringAmount = nf.format(command.getCurrentAmount(player));

            player.sendMessage(ChatColor.DARK_RED
                    + "Unfortunately, you have died. This cost you 75% of your total BTC assets (wallet only). You still have "
                    + ChatColor.RED + stringAmount + ChatColor.DARK_RED + " BTC.");

            // give 75% of the BTC balance to the killer of the player
            if (player.getKiller() != null) {
                Player killer = player.getKiller().getPlayer();
                Double balanceKiller = (command.getCurrentAmount(killer));
                command.setBalancePlayer((balanceKiller + removedAmount), killer);

                killer.sendMessage(
                        ChatColor.AQUA + "You have killed " + player.getName() + ". So you receive 75% of his wallet: "
                                + ChatColor.BLUE + removedAmount + ChatColor.AQUA + " BTC.");
            }

        }
    }

}
