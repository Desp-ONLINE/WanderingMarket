package org.desp.wanderingMarket.listener;

import java.util.Map;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.desp.sapphireMarket.database.ItemDataRepository;
import org.desp.sapphireMarket.database.ItemPurchaseLogRepository;
import org.desp.sapphireMarket.database.PlayerDataRepository;
import org.desp.sapphireMarket.dto.ItemDataDto;
import org.desp.sapphireMarket.dto.ItemPurchaseLogDto;
import org.desp.sapphireMarket.dto.PlayerDataDto;
import org.desp.sapphireMarket.gui.ItemPurchaseConfirmGUI;
import org.desp.sapphireMarket.gui.SapphireMarketGUI;
import org.desp.sapphireMarket.utils.DateUtil;
import org.desp.sapphireMarket.utils.ItemParser;

public class ItemConfirmListener implements Listener {

    public ItemDataRepository itemDataRepository;
    public PlayerDataRepository playerDataRepository;

    public ItemConfirmListener() {
        itemDataRepository = ItemDataRepository.getInstance();
        playerDataRepository = PlayerDataRepository.getInstance();
    }

    @EventHandler
    public void onItemConfirm(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof ItemPurchaseConfirmGUI)) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        Map<String, ItemDataDto> itemDataList = itemDataRepository.getItemDataList();
        PlayerDataDto playerData = playerDataRepository.getPlayerData(player);

        ItemStack item = event.getInventory().getItem(13);
        String purchaseItemID = MMOItems.getID(item);
        ItemDataDto purchaseItemDataDto = itemDataList.get(purchaseItemID);
        int purchaseItemPrice = purchaseItemDataDto.getPrice();
        int purchaseAmount = purchaseItemDataDto.getAmount();

        ItemStack purchaseItemStack = ItemParser.getValidTypeItem(purchaseItemID);
        purchaseItemStack.setAmount(purchaseAmount);

        int slot = event.getRawSlot();
        if ((0 <= slot && slot <= 2) || (9 <= slot && slot <= 11) || (18 <= slot && slot <= 20)) { // 구매취소
            SapphireMarketGUI sapphireMarketGUI = new SapphireMarketGUI(player);
            player.openInventory(sapphireMarketGUI.getInventory());
            return;
        }

        if ((6 <= slot && slot <= 8) || (15 <= slot && slot <= 17) || (24 <= slot && slot <= 26)) { // 구매
            // 구매 수량 기록 조회
            int userTotalAmount = ItemPurchaseLogRepository.getInstance().countPurchaseLog(player, purchaseItemID);
            int userTodayAmount = ItemPurchaseLogRepository.getInstance().countTodayPurchaseLog(player, purchaseItemID);

            // 서버 전체 제한 체크
            if (purchaseItemDataDto.getServerMaxPurchaseAmount() != -1) {
                if (0>= purchaseItemDataDto.getServerMaxPurchaseAmount()) {
                    player.sendMessage("[사파이어상점]: ◇ §c 서버 전체 구매 가능 수량 초과입니다.");
                    player.openInventory(new SapphireMarketGUI(player).getInventory());
                    return;
                }
            }

            // 개인 총 구매 제한 체크
            if (purchaseItemDataDto.getUserMaxPurchaseAmount() != -1) {
                if (userTotalAmount + purchaseAmount > purchaseItemDataDto.getUserMaxPurchaseAmount()) {
                    player.sendMessage("[사파이어상점]: ◇ §c 총 구매 수량 초과입니다. 구매 가능: "
                            + (purchaseItemDataDto.getUserMaxPurchaseAmount() - userTotalAmount) + "개");
                    player.openInventory(new SapphireMarketGUI(player).getInventory());
                    return;
                }
            }

            // 개인 일일 구매 제한 체크
            if (purchaseItemDataDto.getUserDailyPurchaseAmount() != -1) {
                if (userTodayAmount + purchaseAmount > purchaseItemDataDto.getUserDailyPurchaseAmount()) {
                    player.sendMessage("[사파이어상점]: ◇ §c 오늘 구매 수량 초과입니다. 오늘 남은 구매 가능: "
                            + (purchaseItemDataDto.getUserDailyPurchaseAmount() - userTodayAmount) + "개");
                    player.openInventory(new SapphireMarketGUI(player).getInventory());
                    return;
                }
            }

            // 사파이어 잔액 확인
            if (playerData.getSapphireAmount() >= purchaseItemPrice) {
                // 구매 성공
                player.sendMessage("§f[사파이어상점]: ◇ 아이템 구매에 성공하였습니다.");
                player.getInventory().addItem(purchaseItemStack);

                // 사파이어 차감
                playerDataRepository.reduceSapphireAmount(player.getUniqueId().toString(), purchaseItemPrice);

                // 서버 구매 제한 차감
                if (purchaseItemDataDto.getServerMaxPurchaseAmount() != -1) {
                    itemDataRepository.reduceServerMaxPurchaseAMount(purchaseItemDataDto, purchaseAmount);
                }

                // 구매 로그 기록
                ItemPurchaseLogDto newLog = ItemPurchaseLogDto.builder()
                        .user_id(player.getName())
                        .uuid(player.getUniqueId().toString())
                        .purchaseItemID(purchaseItemID)
                        .amount(purchaseAmount)
                        .purchasePrice(purchaseItemPrice)
                        .purchaseTime(DateUtil.getCurrentTime())
                        .build();
                ItemPurchaseLogRepository.getInstance().insertPurchaseLog(newLog);
            } else {
                player.sendMessage("[사파이어상점]: ◇ §c 잔액이 부족합니다.");
            }
            player.openInventory(new SapphireMarketGUI(player).getInventory());
        }
    }
}
