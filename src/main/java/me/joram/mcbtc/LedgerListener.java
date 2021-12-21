package me.joram.mcbtc;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.md_5.bungee.api.ChatColor;

public class LedgerListener implements Listener {

    private Main main;
    private LedgerCommands lcommands;

    public LedgerListener(Main main) {
        this.main = main;
        lcommands = new LedgerCommands(main);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        if (!event.getView().getTitle().equals(event.getWhoClicked().getName() + " 's ledger")) {
            return;
        }
        if (event.getCurrentItem() == null)
            return;
        if (event.getCurrentItem().getItemMeta() == null)
            return;
        if (event.getCurrentItem().getItemMeta().getDisplayName() == null)
            return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == 0) {
            player.closeInventory();
            player.sendMessage(ChatColor.YELLOW + "You have quit the ledger");
        }

        // Neem geld op

        switch (event.getSlot()) {
            case 9:
                lcommands.getFromLedger(0.0000001, player);
                break;
            case 10:
                lcommands.getFromLedger(0.000001, player);
                break;
            case 11:
                lcommands.getFromLedger(0.00001, player);
                break;
            case 12:
                lcommands.getFromLedger(0.0001, player);
                break;
            case 13:
                lcommands.getFromLedger(0.001, player);
                break;
            case 14:
                lcommands.getFromLedger(0.01, player);
                break;
            case 15:
                lcommands.getFromLedger(0.1, player);
                break;
            case 16:
                lcommands.getFromLedger(1.0, player);
                break;
            case 17:
                lcommands.getFromLedger(10.0, player);
                break;

            // Geld storten
            case 18:
                lcommands.setOnLedger(0.0000001, player);
                break;
            case 19:
                lcommands.setOnLedger(0.000001, player);
                break;
            case 20:
                lcommands.setOnLedger(0.00001, player);
                break;
            case 21:
                lcommands.setOnLedger(0.0001, player);
                break;
            case 22:
                lcommands.setOnLedger(0.001, player);
                break;
            case 23:
                lcommands.setOnLedger(0.01, player);
                break;
            case 24:
                lcommands.setOnLedger(0.1, player);
                break;
            case 25:
                lcommands.setOnLedger(1.0, player);
                break;
            case 26:
                lcommands.setOnLedger(10.0, player);
                break;
            default:
                break;
        }

    }
}
