package xyz.nkomarn.ecobridge.economy;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccount;
import com.pixelmonmod.pixelmon.api.economy.IPixelmonBankAccountManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.ecobridge.EcoBridge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EcoImpl implements Economy {

    private static final DecimalFormat FORMAT = new DecimalFormat("#,###,##0.00");
    private static Method GET_METHOD;
    private final EcoBridge bridge;

    public EcoImpl(@NotNull EcoBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public boolean isEnabled() {
        return bridge.isEnabled();
    }

    @Override
    public String getName() {
        return "PokéDollar";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return "₱ " + FORMAT.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return "PokéDollars";
    }

    @Override
    public String currencyNameSingular() {
        return "PokéDollar";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return hasAccount(Bukkit.getOfflinePlayer(playerName));
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return getPixelmonAccount(player.getUniqueId()).isPresent();
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        return getBalance(Bukkit.getOfflinePlayer(playerName));
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getPixelmonAccount(player.getUniqueId())
                .map(IPixelmonBankAccount::getMoney)
                .orElse(0);
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return has(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return getBalance(player) >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        getPixelmonAccount(player.getUniqueId())
                .ifPresent(account -> account.changeMoney(-((int) amount)));

        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        return depositPlayer(Bukkit.getOfflinePlayer(playerName), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        getPixelmonAccount(player.getUniqueId())
                .ifPresent(account -> account.changeMoney((int) amount));

        return null;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return false;
    }

    @NotNull
    private UUID getIdByUsername(@NotNull String username) {
        return Bukkit.getOfflinePlayer(username).getUniqueId();
    }

    @NotNull
    private Optional<IPixelmonBankAccount> getPixelmonAccount(@NotNull UUID uuid) {
        try {
            // This is very ugly as trying to access it normally would force NMS importing, which we don't want to do
            return (Optional<IPixelmonBankAccount>) GET_METHOD.invoke(Pixelmon.moneyManager, uuid);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    static {
        try {
            GET_METHOD = IPixelmonBankAccountManager.class.getMethod("getBankAccount", UUID.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
