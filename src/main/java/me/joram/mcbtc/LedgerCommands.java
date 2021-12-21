package me.joram.mcbtc;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class LedgerCommands implements CommandExecutor {

    private Main main;

    private NumberFormat nf;
    private Commands command;

    public LedgerCommands(Main main) {
        this.main = main;
        command = new Commands(main);
        nf = new DecimalFormat(
                "################################################.###########################################");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("You are not a player. Only players are allowed to use this command!");
            return false;
        }
        Player player = (Player) sender;
        Commands command = new Commands(main);
        if (cmd.getName().equalsIgnoreCase("ledger")) {
            if (args.length == 0) {
                // Create inventory and ledger icon
                main.getConfig().set("ledger." + player.getUniqueId(), getCurrentLedgerAmount(player));

                main.saveConfig();

                Inventory inv = Bukkit.createInventory(player, 27, player.getName() + " 's ledger");
                ItemStack item = new ItemStack(Material.LEVER);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Ledger");
                // set lore
                String walletBalanceString = nf.format(command.getCurrentAmount(player));
                String ledgerBalanceString = nf.format(getCurrentLedgerAmount(player));

                List<String> loreText = new ArrayList<String>();
                loreText.add(ChatColor.BLUE + "The current amount in your account is: " + ChatColor.AQUA
                        + walletBalanceString);

                loreText.add(ChatColor.BLUE + "The current amount on your ledger is " + ChatColor.AQUA
                        + ledgerBalanceString + ChatColor.BLUE + " BTC");
                itemMeta.setLore(loreText);

                for (int i = 9; i < 18; i++) {
                    ItemStack[] redItemsArray = new ItemStack[9];
                    redItemsArray[i - 9] = new ItemStack(Material.RED_CONCRETE);

                    ItemMeta[] redItemMetaArray = new ItemMeta[9];
                    redItemMetaArray[i - 9] = redItemsArray[i - 9].getItemMeta();

                    Double calculatedAmount = ((Math.pow(10, (i - 9))) / 10000000);
                    String bcalculatedAmountString = nf.format(calculatedAmount);

                    redItemMetaArray[i - 9]
                            .setDisplayName(ChatColor.RED + "Withdraw " + bcalculatedAmountString + " BTC");
                    redItemsArray[i - 9].setItemMeta(redItemMetaArray[i - 9]);

                    inv.setItem(i, redItemsArray[i - 9]);

                }

                for (int i = 18; i < 27; i++) {
                    ItemStack[] greenItemsArray = new ItemStack[9];
                    greenItemsArray[i - 18] = new ItemStack(Material.GREEN_CONCRETE);

                    ItemMeta[] greenItemMetaArray = new ItemMeta[9];
                    greenItemMetaArray[i - 18] = greenItemsArray[i - 18].getItemMeta();

                    Double calculatedAmount = ((Math.pow(10, (i - 18))) / 10000000);
                    String calculatedAmountString = nf.format(calculatedAmount);

                    greenItemMetaArray[i - 18]
                            .setDisplayName(ChatColor.GREEN + "Deposit " + calculatedAmountString + " BTC");
                    greenItemsArray[i - 18].setItemMeta(greenItemMetaArray[i - 18]);

                    inv.setItem(i, greenItemsArray[i - 18]);

                }

                item.setItemMeta(itemMeta);
                inv.setItem(4, item);

                // Exit Icon
                ItemStack exitIcon = new ItemStack(Material.BARRIER);
                ItemMeta exitIconMeta = exitIcon.getItemMeta();
                exitIconMeta.setDisplayName(ChatColor.DARK_RED + "Quit ledger");
                exitIcon.setItemMeta(exitIconMeta);
                inv.setItem(0, exitIcon);

                player.openInventory(inv);

            } else if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "Use the command /ledger to open your ledger!");
            } else if (args.length == 2) {
                try {
                    Double bedrag = Double.parseDouble(args[1]);

                    if (args[0].equalsIgnoreCase("get")) {

                        getFromLedger(bedrag, player);
                    } else if (args[0].equalsIgnoreCase("set")) {
                        setOnLedger(bedrag, player);
                    } else {
                        player.sendMessage(
                                ChatColor.RED + "Something went wrong! Use the command /ledger to open your ledger. ");
                    }

                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "Fill in a correct amount!");
                }

            }

        }

        return false;

    }

    public double getCurrentLedgerAmount(Player player) {
        double currentAmount;
        currentAmount = main.getConfig().getDouble("ledger." + player.getUniqueId());

        return currentAmount;
    }

    public void setLedgerBalance(Double amount, Player player) {

        main.getConfig().set("ledger." + player.getUniqueId(), amount);
        main.saveConfig();
    }

    public boolean setOnLedger(Double setAmount, Player player) {
        Double amountToSet = setAmount;
        String amountToSetString = nf.format(amountToSet);
        Double balanceUser = command.getCurrentAmount(player);
        Double balanceLedger = getCurrentLedgerAmount(player);
        if (setAmount < 0) {
            player.sendMessage(
                    ChatColor.DARK_RED + "You are not allowed to fill in a negative amount. Use a valid amount!");
            return false;
        }
        if (balanceUser < setAmount) {
            player.sendMessage(ChatColor.RED + "You don't have enough BTC!");
            return false;
        }

        balanceUser = command.getCurrentAmount(player);
        main.getConfig().set("balances." + player.getUniqueId(), (balanceUser - setAmount));
        main.saveConfig();
        main.reloadConfig();

        main.getConfig().set("ledger." + player.getUniqueId(), (balanceLedger + setAmount));
        main.saveConfig();
        main.reloadConfig();

        player.sendMessage(ChatColor.AQUA + "You have succesfully sent " + ChatColor.BLUE + amountToSetString + ChatColor.AQUA
                + " from your wallet to your ledger.");

        return true;
    }

    public boolean getFromLedger(Double getAmount, Player player) {
        if (getAmount < 0) {
            player.sendMessage(
                    ChatColor.DARK_RED + "You are not allowed to fill in a negative amount. Use a valid amount!");
            return false;
        }
        Double amountToGet = getAmount;
        String amountToGetString = nf.format(amountToGet);
        Double balanceUser = command.getCurrentAmount(player);
        Double balanceLedger = getCurrentLedgerAmount(player);

        if (balanceLedger < getAmount) {
            player.sendMessage(ChatColor.RED + "You don't have enought BTC on your ledger!");
            return false;
        }

        main.getConfig().set("ledger." + player.getUniqueId(), (balanceLedger - getAmount));
        main.saveConfig();
        main.reloadConfig();

        balanceUser = command.getCurrentAmount(player);
        main.getConfig().set("balances." + player.getUniqueId(), (balanceUser + getAmount));
        main.saveConfig();
        main.reloadConfig();

        player.sendMessage(ChatColor.AQUA + "You have succesfully withdrawn: " + ChatColor.BLUE + amountToGetString
                + ChatColor.AQUA + " from your ledger to your wallet");

        return true;
    }
}
