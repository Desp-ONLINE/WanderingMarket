package org.desp.wanderingMarket;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.desp.wanderingMarket.command.ItemRegisterCommand;
import org.desp.wanderingMarket.command.WanderingMarketCommand;
import org.desp.wanderingMarket.command.WanderingMarketETCCommand;
import org.desp.wanderingMarket.database.ItemDataRepository;
import org.desp.wanderingMarket.database.PlayerDataRepository;
import org.desp.wanderingMarket.listener.ItemConfirmListener;
import org.desp.wanderingMarket.listener.ItemSelectListener;
import org.desp.wanderingMarket.listener.PlayerJoinAndQuitListener;

public final class WanderingMarket extends JavaPlugin {
    @Getter
    private static WanderingMarket instance;

    @Override
    public void onEnable() {
        instance = this;
        ItemDataRepository.getInstance().loadItemData();

        PlayerDataRepository.getInstance().loadAllPlayerData();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinAndQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemSelectListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemConfirmListener(), this);

        getCommand("사파이어").setExecutor(new WanderingMarketETCCommand());
        getCommand("사파이어상점").setExecutor(new WanderingMarketCommand());
        getCommand("사파이어아이템등록").setExecutor(new ItemRegisterCommand());
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PlayerDataRepository.getInstance().saveAllPlayerData();
    }
}
