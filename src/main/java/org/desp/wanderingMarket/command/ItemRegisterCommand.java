package org.desp.wanderingMarket.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.desp.sapphireMarket.database.ItemDataRepository;
import org.desp.sapphireMarket.dto.ItemDataDto;
import org.jetbrains.annotations.NotNull;

public class ItemRegisterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s,
                             @NotNull String[] strings) {

        if (!(commandSender instanceof Player player)) {
            return false;
        }
        if (!player.isOp()){
            return false;
        }
        // /아이템등록 기타_판도라의열쇠 <갯수> <가격> <유저최대구매가능갯수> <유저하루최대구매가능갯수> <서버전체유저구매가능갯수> <슬롯> *-1로 설정시 제한없음
        String MMOItem_ID = strings[0];
        int amount = Integer.parseInt(strings[1]);
        int price = Integer.parseInt(strings[2]);
        int userMaxPurchaseAmount = Integer.parseInt(strings[3]);
        int userDailyPurchaseAmount = Integer.parseInt(strings[4]);
        int serverMaxPurchaseAmount = Integer.parseInt(strings[5]);
        int slot = Integer.parseInt(strings[6]);

        ItemDataDto newItem = ItemDataDto.builder()
                .MMOItem_ID(MMOItem_ID)
                .amount(amount)
                .price(price)
                .userMaxPurchaseAmount(userMaxPurchaseAmount)
                .userDailyPurchaseAmount(userDailyPurchaseAmount)
                .serverMaxPurchaseAmount(serverMaxPurchaseAmount)
                .slot(slot)
                .build();

        ItemDataRepository.getInstance().insertItemData(newItem);
        return false;
    }
}
