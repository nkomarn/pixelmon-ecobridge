package xyz.nkomarn.ecobridge;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.nkomarn.ecobridge.economy.EcoImpl;

public class EcoBridge extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!hook()) {
            getLogger().severe("Failed to hook into Vault; the economy will not function.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private boolean hook() {
        if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
            return false;
        }

        getServer().getServicesManager().register(Economy.class, new EcoImpl(this), this, ServicePriority.Highest);
        return true;
    }
}
