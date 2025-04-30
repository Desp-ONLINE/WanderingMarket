package org.desp.wanderingMarket.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.desp.sapphireMarket.database.PlayerDataRepository;
import org.desp.sapphireMarket.dto.PlayerDataDto;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WanderingMarketETCCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) {
            return false;
        }

        PlayerDataRepository playerDataRepository = PlayerDataRepository.getInstance();
        if (strings.length == 0) {
            // 현재 플레이어의 데이터 가져오기
            PlayerDataDto playerData = playerDataRepository.getPlayerData(player);
            int sapphireAmount = playerData.getSapphireAmount();
            player.sendMessage("§a◇ §e" + player.getName() + "§f님의 사파이어: §a" + sapphireAmount + "§f원");
            return true;
        }

        if (!player.isOp()) {
            return false;
        }

        String playerName = strings[1];

        String targetPlayerUUID = playerDataRepository.getPlayerUUID(playerName);
        PlayerDataDto targetPlayerDto = playerDataRepository.getPlayerListCache().get(targetPlayerUUID);

        return switch (strings[0]) {
            case "보기" -> {
                int sapphireAmount = targetPlayerDto.getSapphireAmount();
                player.sendMessage("§a◇ §e" + playerName + "§f님의 사파이어: §a" + sapphireAmount + "§f원");
                yield true;
            }
            case "지급" -> {
                int amount = Integer.parseInt(strings[2]);
                playerDataRepository.addSapphireAmount(targetPlayerUUID, amount);
                player.sendMessage("§a◇ §e" + playerName + "§f님에게 §a" + amount + "§f원을 지급했습니다.");
                yield true;
            }
            case "차감" -> {
                int minusAmount = Integer.parseInt(strings[2]);
                playerDataRepository.reduceSapphireAmount(targetPlayerUUID, minusAmount);
                player.sendMessage("§a◇ §e" + playerName + "§f님의 사파이어에서 §c" + minusAmount + "§f원을 차감했습니다.");
                yield true;
            }
            default -> {
                commandSender.sendMessage("§c 잘못된 명령어입니다.");
                yield false;
            }
        };
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command,
                                                @NotNull String s, @NotNull String[] strings) {

        List<String> completions = new ArrayList<>();
        if (strings.length == 1) {
            completions.addAll(Arrays.asList("보기", "지급", "차감"));
        }

        return completions;
    }
}
