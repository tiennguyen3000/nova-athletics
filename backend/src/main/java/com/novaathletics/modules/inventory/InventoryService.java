package com.novaathletics.modules.inventory;
import com.novaathletics.common.error.BusinessException;
import com.novaathletics.modules.inventory.entity.*;
import com.novaathletics.modules.inventory.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;
@Service
public class InventoryService {
  private final WarehouseInventoryRepository invRepo; private final InventoryTransactionRepository txRepo; private final InventoryReservationRepository resRepo; private final WarehouseRepository whRepo;
  public InventoryService(WarehouseInventoryRepository ir, InventoryTransactionRepository tr, InventoryReservationRepository rr, WarehouseRepository wr){this.invRepo=ir; this.txRepo=tr; this.resRepo=rr; this.whRepo=wr;}

  @Transactional
  public void reserve(Long variantId,int qty,Long orderId,Long cartId){
    // choose warehouse with most available
    var all=invRepo.findByVariantForUpdate(variantId);
    if(all.isEmpty()) throw new BusinessException("OUT_OF_STOCK","No inventory",org.springframework.http.HttpStatus.CONFLICT);
    all.sort((a,b)->Integer.compare(b.getAvailable(), a.getAvailable()));
    int remaining=qty;
    for(WarehouseInventory inv: all){
      if(remaining<=0) break;
      int avail=inv.getAvailable();
      if(avail<=0) continue;
      int take=Math.min(avail, remaining);
      inv.setQuantityReserved(inv.getQuantityReserved()+take);
      invRepo.save(inv);
      InventoryReservation r=new InventoryReservation(); r.setVariantId(variantId); r.setWarehouseId(inv.getWarehouseId()); r.setOrderId(orderId); r.setCartId(cartId); r.setQuantity(take); r.setStatus("ACTIVE"); r.setExpiresAt(Instant.now().plusSeconds(900));
      resRepo.save(r);
      InventoryTransaction tx=new InventoryTransaction(); tx.setVariantId(variantId); tx.setWarehouseId(inv.getWarehouseId()); tx.setType("RESERVATION"); tx.setQuantity(0); tx.setReferenceType("ORDER"); tx.setReferenceId(orderId); tx.setNote("reserve "+take);
      txRepo.save(tx);
      remaining-=take;
    }
    if(remaining>0){
      // rollback will happen via exception; but we need to revert already reserved - transaction will rollback
      throw new BusinessException("OUT_OF_STOCK","Insufficient stock for variant "+variantId,org.springframework.http.HttpStatus.CONFLICT);
    }
  }

  @Transactional
  public void confirmReservation(Long orderId){
    for(InventoryReservation r: resRepo.findByOrderId(orderId)){
      if(!"ACTIVE".equals(r.getStatus())) continue;
      var inv=invRepo.findForUpdate(r.getVariantId(), r.getWarehouseId()).orElseThrow();
      inv.setQuantityOnHand(inv.getQuantityOnHand()-r.getQuantity());
      inv.setQuantityReserved(inv.getQuantityReserved()-r.getQuantity());
      invRepo.save(inv);
      r.setStatus("CONFIRMED"); resRepo.save(r);
      InventoryTransaction tx=new InventoryTransaction(); tx.setVariantId(r.getVariantId()); tx.setWarehouseId(r.getWarehouseId()); tx.setType("SALE"); tx.setQuantity(-r.getQuantity()); tx.setReferenceType("ORDER"); tx.setReferenceId(orderId);
      txRepo.save(tx);
    }
  }

  @Transactional
  public void releaseReservation(Long orderId){
    for(InventoryReservation r: resRepo.findByOrderId(orderId)){
      if(!"ACTIVE".equals(r.getStatus())) continue;
      var inv=invRepo.findForUpdate(r.getVariantId(), r.getWarehouseId()).orElseThrow();
      inv.setQuantityReserved(inv.getQuantityReserved()-r.getQuantity());
      invRepo.save(inv);
      r.setStatus("RELEASED"); resRepo.save(r);
      InventoryTransaction tx=new InventoryTransaction(); tx.setVariantId(r.getVariantId()); tx.setWarehouseId(r.getWarehouseId()); tx.setType("RELEASE"); tx.setQuantity(0); tx.setReferenceType("ORDER"); tx.setReferenceId(orderId);
      txRepo.save(tx);
    }
  }

  @Transactional
  public void adjust(Long variantId,Long warehouseId,int delta,String type,String note,Long userId){
    var inv=invRepo.findForUpdate(variantId, warehouseId).orElseGet(()->{ WarehouseInventory ni=new WarehouseInventory(); ni.setVariantId(variantId); ni.setWarehouseId(warehouseId); ni.setQuantityOnHand(0); ni.setQuantityReserved(0); return ni; });
    if("PURCHASE".equals(type)||"ADJUSTMENT".equals(type)||"RETURN".equals(type)){
      inv.setQuantityOnHand(inv.getQuantityOnHand()+delta);
    } else if("DAMAGE".equals(type)){
      inv.setQuantityOnHand(inv.getQuantityOnHand()-Math.abs(delta));
    }
    if(inv.getQuantityOnHand()<0) throw new BusinessException("VALIDATION_ERROR","Negative stock",org.springframework.http.HttpStatus.BAD_REQUEST);
    invRepo.save(inv);
    InventoryTransaction tx=new InventoryTransaction(); tx.setVariantId(variantId); tx.setWarehouseId(warehouseId); tx.setType(type); tx.setQuantity(delta); tx.setReferenceType("MANUAL"); tx.setNote(note); tx.setCreatedBy(userId);
    txRepo.save(tx);
  }

  @org.springframework.scheduling.annotation.Scheduled(fixedDelay=60000)
  @Transactional
  public void releaseExpired(){
    for(InventoryReservation r: resRepo.findByStatusAndExpiresAtBefore("ACTIVE", Instant.now())){
      try{
        var inv=invRepo.findForUpdate(r.getVariantId(), r.getWarehouseId()).orElse(null);
        if(inv!=null){ inv.setQuantityReserved(inv.getQuantityReserved()-r.getQuantity()); invRepo.save(inv); }
        r.setStatus("EXPIRED"); resRepo.save(r);
        InventoryTransaction tx=new InventoryTransaction(); tx.setVariantId(r.getVariantId()); tx.setWarehouseId(r.getWarehouseId()); tx.setType("RELEASE"); tx.setQuantity(0); tx.setReferenceType("EXPIRE"); tx.setReferenceId(r.getId());
        txRepo.save(tx);
      }catch(Exception ignored){}
    }
  }
}
