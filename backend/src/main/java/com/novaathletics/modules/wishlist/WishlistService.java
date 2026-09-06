package com.novaathletics.modules.wishlist;
import com.novaathletics.common.error.*;
import com.novaathletics.modules.wishlist.entity.*;
import com.novaathletics.modules.wishlist.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service
public class WishlistService {
  private final WishlistRepository wlRepo; private final WishlistItemRepository itemRepo;
  public WishlistService(WishlistRepository wr, WishlistItemRepository ir){this.wlRepo=wr; this.itemRepo=ir;}
  public Wishlist getOrCreate(Long cid){
    return wlRepo.findByCustomerId(cid).orElseGet(()->{ Wishlist w=new Wishlist(); w.setCustomerId(cid); return wlRepo.save(w); });
  }
  public List<WishlistItem> list(Long cid){ return itemRepo.findByWishlistId(getOrCreate(cid).getId()); }
  @Transactional
  public WishlistItem add(Long cid, Long productId, Long variantId){
    Wishlist wl=getOrCreate(cid);
    if(itemRepo.findByWishlistIdAndProductId(wl.getId(), productId).isPresent()) throw new BusinessException("WISHLIST_ITEM_EXISTS","Already in wishlist",org.springframework.http.HttpStatus.CONFLICT);
    WishlistItem wi=new WishlistItem(); wi.setWishlistId(wl.getId()); wi.setProductId(productId); wi.setVariantId(variantId);
    return itemRepo.save(wi);
  }
  @Transactional
  public void remove(Long cid, Long itemId){
    Wishlist wl=getOrCreate(cid);
    WishlistItem wi=itemRepo.findById(itemId).orElseThrow(()->new NotFoundException("WISHLIST_ITEM_NOT_FOUND","Not found"));
    if(!wi.getWishlistId().equals(wl.getId())) throw new BusinessException("FORBIDDEN","Forbidden",org.springframework.http.HttpStatus.FORBIDDEN);
    itemRepo.delete(wi);
  }
}
