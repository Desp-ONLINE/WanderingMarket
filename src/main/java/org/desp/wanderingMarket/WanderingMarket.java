package org.desp.wanderingMarket;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.desp.wanderingMarket.command.WanderingMarketCommand;
import org.desp.wanderingMarket.database.ItemDataRepository;
import org.desp.wanderingMarket.database.NPCLocationDataRepository;
import org.desp.wanderingMarket.database.PlayerDataRepository;
import org.desp.wanderingMarket.listener.ItemConfirmListener;
import org.desp.wanderingMarket.listener.ItemSelectListener;
import org.desp.wanderingMarket.listener.PlayerJoinAndQuitListener;
import org.desp.wanderingMarket.utils.TimeManager;

public final class WanderingMarket extends JavaPlugin {

    @Getter
    private static WanderingMarket instance;

    @Override
    public void onEnable() {
        instance = this;
        NPCLocationDataRepository.getInstance().loadAllPlayerData();
        ItemDataRepository.getInstance().loadItemData();

        TimeManager.getInstance().startMarketRotationTask();

        PlayerDataRepository.getInstance().loadAllPlayerData();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinAndQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemSelectListener(), this);
        Bukkit.getPluginManager().registerEvents(new ItemConfirmListener(), this);

        getCommand("방랑상인상점").setExecutor(new WanderingMarketCommand());
    }

    @Override
    public void onDisable() {
        PlayerDataRepository.getInstance().saveAllPlayerData();
    }
}
