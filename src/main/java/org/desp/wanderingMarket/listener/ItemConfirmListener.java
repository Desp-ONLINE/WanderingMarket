package org.desp.wanderingMarket.listener;

import com.binggre.binggreEconomy.BinggreEconomy;
import java.util.Map;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.desp.wanderingMarket.database.ItemDataRepository;
import org.desp.wanderingMarket.database.ItemPurchaseLogRepository;
import org.desp.wanderingMarket.database.ItemPurchaseMemoryLogRepository;
import org.desp.wanderingMarket.database.PlayerDataRepository;
import org.desp.wanderingMarket.dto.ItemDataDto;
import org.desp.wanderingMarket.dto.ItemPurchaseLogDto;
import org.desp.wanderingMarket.gui.ItemPurchaseConfirmGUI;
import org.desp.wanderingMarket.gui.WanderingMarketGUI;
import org.desp.wanderingMarket.utils.DateUtil;
import org.desp.wanderingMarket.utils.ItemParser;

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

        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory.getType().equals(InventoryType.PLAYER) ) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Map<Integer, ItemDataDto> itemDataList = itemDataRepository.getItemDataList();

        ItemStack item = event.getInventory().getItem(13);
        String purchaseItemID = MMOItems.getID(item);

        ItemDataDto purchaseItemDataDto = null;
        for (ItemDataDto value : itemDataList.values()) {
            if (value.getMMOItem_ID().equals(purchaseItemID) && value.getAmount() == item.getAmount()) {
                purchaseItemDataDto = value;
                break;
            }
        }

        int purchaseItemPrice = purchaseItemDataDto.getPrice();
        int purchaseAmount = purchaseItemDataDto.getAmount();

        ItemStack purchaseItemStack = ItemParser.getValidTypeItem(purchaseItemID);
        purchaseItemStack.setAmount(purchaseAmount);

        int slot = event.getRawSlot();
        if ((0 <= slot && slot <= 2) || (9 <= slot && slot <= 11) || (18 <= slot && slot <= 20)) { // 구매취소
            WanderingMarketGUI wanderingMarketGUI = new WanderingMarketGUI(player);
            player.openInventory(wanderingMarketGUI.getInventory());
            return;
        }

        if ((6 <= slot && slot <= 8) || (15 <= slot && slot <= 17) || (24 <= slot && slot <= 26)) { // 구매
            // 구매 수량 기록 조회
            int userTotalAmount = ItemPurchaseMemoryLogRepository.getInstance().countPurchaseLog(player, purchaseItemDataDto.getItemID());

            // 개인 총 구매 제한 체크
            if (purchaseItemDataDto.getUserMaxPurchaseAmount() != -1) {
                if (userTotalAmount >= purchaseItemDataDto.getUserMaxPurchaseAmount()) {
                    player.sendMessage("[방랑상인상점]: ◇ §c 총 구매 수량 초과입니다. 구매 가능: "
                            + (purchaseItemDataDto.getUserMaxPurchaseAmount() - userTotalAmount) + "개");
                    player.openInventory(new WanderingMarketGUI(player).getInventory());
                    return;
                }
            }

            double balance = BinggreEconomy.getInst().getEconomy().getBalance(player);

            // 골드 잔액 확인
            if (balance >= purchaseItemPrice) {
                // 구매 성공
                player.sendMessage("§f[방랑상인상점]: ◇ 아이템 구매에 성공하였습니다.");
                player.getInventory().addItem(purchaseItemStack);

                // 골드 차감
                BinggreEconomy.getInst().getEconomy().withdrawPlayer(player, purchaseItemPrice);

                // 구매 로그 기록
                ItemPurchaseLogDto newLog = ItemPurchaseLogDto.builder()
                        .user_id(player.getName())
                        .uuid(player.getUniqueId().toString())
                        .purchaseItemID(purchaseItemDataDto.getItemID())
                        .amount(purchaseAmount)
                        .purchasePrice(purchaseItemPrice)
                        .purchaseTime(DateUtil.getCurrentTime())
                        .build();
                ItemPurchaseLogRepository.getInstance().insertPurchaseLog(newLog);
                ItemPurchaseMemoryLogRepository.getInstance().insertPurchaseLog(newLog);
            } else {
                player.sendMessage("[방랑상인상점]: ◇ §c 잔액이 부족합니다.");
            }
            player.openInventory(new WanderingMarketGUI(player).getInventory());
        }
    }
}
