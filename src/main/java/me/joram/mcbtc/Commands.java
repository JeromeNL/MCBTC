package me.joram.mcbtc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor {

    private Main main;
    private NumberFormat nf;

    public Commands(Main main) {
        this.main = main;
        nf = new DecimalFormat(
                "################################################.###########################################");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("You are not a player. Only players are allowed to use this command!");
            return false;
        } else {
            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("wallet")) {
                sendPlayerCurrentBalance(player);

            } else if (cmd.getName().equalsIgnoreCase("gamble")) {
                try {
                    if (args.length == 1) {
                        Double amount = 0.0;

                        amount = Double.parseDouble(args[0]);
                        if (amount > 0) {
                            gamble(amount, player);

                        } else {
                            player.sendMessage(ChatColor.RED + "You are not allowed to gamble with a negative amount!");
                        }

                    } else if (args.length == 0) {
                        player.sendMessage(ChatColor.RED + "You have to fill in a amount: /gamble <amount>");
                    } else if (args.length > 2) {
                        player.sendMessage(ChatColor.RED + "Use /gamble <amount>");
                    } else {
                        player.sendMessage(ChatColor.RED + "What is going on!?");
                    }

                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "You have to fill in a correct amount!");
                }
            }

            else if (cmd.getName().equalsIgnoreCase("btcpay")) {
                if (args.length == 2) {
                    try {
                        Double amount = Double.parseDouble(args[1]);
                        Player ontvanger = Bukkit.getPlayer(args[0]);

                        if (ontvanger == null) {
                            player.sendMessage(ChatColor.RED + "Player not found!");
                        } else {
                            if (amount > 0) {
                                payOtherPlayer(amount, player, ontvanger);
                            } else {
                                player.sendMessage(ChatColor.RED
                                        + "That is not a correct amount! Transferring a negative amount is theft.");
                            }
                        }

                    } catch (Exception e) {
                        player.sendMessage(ChatColor.RED + "This amount is not correct!");
                    }
                } else if (args.length == 1) {
                    player.sendMessage(ChatColor.RED + "Don't forget to fill in an amount: /btcpay <player> <amount>");
                } else if (args.length == 0) {
                    player.sendMessage(
                            ChatColor.RED + "You have to fill in a player and an amount: /btcpay <player> <amount>");
                } else if (args.length > 2) {
                    player.sendMessage(
                            ChatColor.RED + "You have to fill in a player and an amount: /btcpay <player> <amount>");
                } else {
                    player.sendMessage(ChatColor.RED + "What is going on!?");
                }

            } else {
                return true;
            }

        }
        return true;

    }

    // returns a random BTC value.
    public double mineBTC() {
        double randomAmount;

        Random random = new Random();
        randomAmount = 0.00001 + (0.0005 - 0.00001) * random.nextDouble();

        return randomAmount;
    }

    // get the balance of a players wallet
    public double getCurrentAmount(Player player) {
        double currentAmount;
        currentAmount = main.getConfig().getDouble("balances." + player.getUniqueId());
        return currentAmount;
    }

    // Player == player who is mining // hashrate == number between 0.0000...1 and
    // 1.0
    public double mineRealBTC(Player player, double hashRate) {

        double amount = (mineBTC() * hashRate);

        String amountString = nf.format(amount);
        main.getConfig().set("balances." + player.getUniqueId(), amount);
        main.saveConfig();
        player.sendMessage(ChatColor.AQUA + "You just mined: " + amountString + " BTC!");
        return amount;
    }

    // send BTC from player A to player B
    public boolean payOtherPlayer(Double amount, Player sender, Player receiver) {
        Double amountToSent = amount;
        String amountToSentString = nf.format(amountToSent);
        Double balanceSender = getCurrentAmount(sender);
        Double balanceReceiver = getCurrentAmount(receiver);

        if (getCurrentAmount(sender) < amount) {
            sender.sendMessage(ChatColor.RED
                    + "You are trying to transfer an amount that is too high. You don't have that much BTC, unfortunately.");
            return false;
        }

        main.getConfig().set("balances." + sender.getUniqueId(), (balanceSender - amountToSent));
        main.saveConfig();
        main.reloadConfig();
        balanceReceiver = getCurrentAmount(receiver);
        main.getConfig().set("balances." + receiver.getUniqueId(), (balanceReceiver + amountToSent));

        main.saveConfig();
        sender.sendMessage(ChatColor.AQUA + "You have succesfully transfered: " + ChatColor.BLUE + amountToSentString
                + ChatColor.AQUA + " BTC to " + ChatColor.BLUE + receiver.getName() + ChatColor.AQUA + ".");
        receiver.sendMessage(ChatColor.AQUA + "You have received an amount of: " + ChatColor.BLUE + amountToSentString
                + ChatColor.AQUA + " BTC from " + ChatColor.BLUE + sender.getName() + ChatColor.AQUA + ".");

        return true;

    }

    // player gambles with his deposit: win = +100% return, lose = 0% return (win
    // chance = 45%)
    public double gamble(Double gambleAmount, Player gambler) {

        if (getCurrentAmount(gambler) < gambleAmount) {
            gambler.sendMessage(ChatColor.RED
                    + "You are not doing well financially. You are now trying to gamble with money you don't have.");
            return 0.0;
        }

        double chance = Math.random();
        Double gamblerBalance = getCurrentAmount(gambler);

        main.getConfig().set("balances." + gambler.getUniqueId(), (gamblerBalance - gambleAmount));
        main.saveConfig();

        if (chance < 0.45) {
            Double wonAmount = gambleAmount * 2;
            gamblerBalance = getCurrentAmount(gambler);
            main.saveConfig();

            main.getConfig().set("balances." + gambler.getUniqueId(), (gamblerBalance + wonAmount));
            main.saveConfig();

            gambler.sendMessage(ChatColor.GREEN + "Congratulations! You have won " + (wonAmount) + " BTC!.");

            return (wonAmount);

        }
        gambler.sendMessage(ChatColor.DARK_RED + "Unfortunately, you have lost your stake of  " + ChatColor.RED
                + (gambleAmount) + ChatColor.DARK_RED + " BTC");
        return 0;
    }

    // set wallet balance of a player
    public void setBalancePlayer(Double amount, Player player) {

        main.getConfig().set("balances." + player.getUniqueId(), amount);
        main.saveConfig();

    }

    // send the current balance to himself
    public void sendPlayerCurrentBalance(Player player) {
        String formattedAmount = nf.format(getCurrentAmount(player));
        player.sendMessage(ChatColor.AQUA + "You currently have " + formattedAmount + " BTC in your wallet");
    }
}