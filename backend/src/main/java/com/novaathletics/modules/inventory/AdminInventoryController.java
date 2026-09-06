package com.novaathletics.modules.inventory;
import com.novaathletics.common.pagination.ApiResponse;
import com.novaathletics.common.security.SecurityUtils;
import com.novaathletics.modules.inventory.entity.Warehouse;
import com.novaathletics.modules.inventory.repository.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/admin")
public class AdminInventoryController {
  private final WarehouseRepository whRepo; private final WarehouseInventoryRepository invRepo; private final InventoryService invService; private final InventoryTransactionRepository txRepo;
  public AdminInventoryController(WarehouseRepository wr, WarehouseInventoryRepository ir, InventoryService is, InventoryTransactionRepository tr){this.whRepo=wr; this.invRepo=ir; this.invService=is; this.txRepo=tr;}
  @GetMapping("/inventory") @PreAuthorize("hasAuthority('INVENTORY_READ')") public ApiResponse<List<Map<String,Object>>> list(){ return new ApiResponse<>(List.of()); }
  @PostMapping("/inventory/adjustments") @PreAuthorize("hasAuthority('INVENTORY_UPDATE')") public ApiResponse<Map<String,String>> adj(@RequestBody Map<String,Object> body){
    Long vid=Long.valueOf(body.get("variantId").toString()); Long wid=Long.valueOf(body.get("warehouseId").toString());
    int qty=Integer.parseInt(body.get("quantity").toString()); String type=(String)body.getOrDefault("type","ADJUSTMENT"); String note=(String)body.get("note");
    Long uid=SecurityUtils.current().map(p->p.getUserId()).orElse(null);
    invService.adjust(vid,wid,qty,type,note,uid);
    return new ApiResponse<>(Map.of("message","adjusted"));
  }
  @GetMapping("/warehouses") @PreAuthorize("hasAuthority('INVENTORY_READ')") public ApiResponse<List<Warehouse>> whs(){ return new ApiResponse<>(whRepo.findAll()); }
  @PostMapping("/warehouses") @PreAuthorize("hasAuthority('INVENTORY_UPDATE')") public ApiResponse<Warehouse> createWh(@RequestBody Warehouse w){ return new ApiResponse<>(whRepo.save(w)); }
  @GetMapping("/inventory/transactions") @PreAuthorize("hasAuthority('INVENTORY_READ')") public ApiResponse<List<Map<String,Object>>> txs(){ return new ApiResponse<>(List.of()); }
}
