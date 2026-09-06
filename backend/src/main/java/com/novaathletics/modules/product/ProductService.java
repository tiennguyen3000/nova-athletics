package com.novaathletics.modules.product;
import com.novaathletics.common.error.NotFoundException;
import com.novaathletics.modules.product.dto.*;
import com.novaathletics.modules.product.entity.*;
import com.novaathletics.modules.product.repository.*;
import com.novaathletics.modules.inventory.repository.WarehouseInventoryRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class ProductService {
  private final ProductRepository productRepo; private final ProductVariantRepository variantRepo;
  private final ProductImageRepository imageRepo; private final WarehouseInventoryRepository invRepo;
  private final CategoryRepository catRepo; private final CollectionRepository colRepo;
  public ProductService(ProductRepository pr, ProductVariantRepository vr, ProductImageRepository ir, WarehouseInventoryRepository wir, CategoryRepository cr, CollectionRepository cor){
    this.productRepo=pr; this.variantRepo=vr; this.imageRepo=ir; this.invRepo=wir; this.catRepo=cr; this.colRepo=cor;
  }
  public Page<ProductListResponse> list(String q,String category,String collection,String gender,String sport,String color,String size, Pageable pageable){
    String cg=gender; String sp=sport;
    Page<Product> page=productRepo.search(q,cg,sp,pageable);
    return page.map(this::toList);
  }
  public ProductDetailResponse detail(String slug){
    Product p=productRepo.findBySlug(slug).orElseThrow(()->new NotFoundException("PRODUCT_NOT_FOUND","Product not found"));
    if(p.getDeletedAt()!=null) throw new NotFoundException("PRODUCT_NOT_FOUND","Product not found");
    return toDetail(p);
  }
  public Product getById(Long id){ return productRepo.findById(id).orElseThrow(()->new NotFoundException("PRODUCT_NOT_FOUND","Product not found")); }
  private ProductListResponse toList(Product p){
    var variants=variantRepo.findByProductId(p.getId());
    var images=imageRepo.findByProductIdOrderBySortOrderAsc(p.getId());
    String primary=images.stream().filter(i->Boolean.TRUE.equals(i.getIsPrimary())).map(ProductImage::getUrl).findFirst().orElse(images.isEmpty()?null:images.get(0).getUrl());
    List<String> sizes=variants.stream().map(ProductVariant::getSize).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    List<ProductListResponse.ColorDto> colors=variants.stream().filter(v->v.getColor()!=null).map(v->new ProductListResponse.ColorDto(v.getColor(),v.getColorHex())).distinct().collect(Collectors.toList());
    boolean inStock=variants.stream().anyMatch(v-> invRepo.findByVariantId(v.getId()).stream().anyMatch(inv-> inv.getAvailable()>0));
    return new ProductListResponse(p.getId(),p.getName(),p.getSlug(),p.getSubtitle(),p.getBrand(),p.getGender(),p.getSport(),p.getBasePrice(),p.getSalePrice(),p.getCurrency(),p.getIsFeatured(),primary,List.of(),List.of(),sizes,colors,inStock);
  }
  private ProductDetailResponse toDetail(Product p){
    var variants=variantRepo.findByProductId(p.getId());
    var images=imageRepo.findByProductIdOrderBySortOrderAsc(p.getId());
    List<ProductDetailResponse.ImageDto> imgs=images.stream().map(i->new ProductDetailResponse.ImageDto(i.getUrl(),i.getAltText(),i.getSortOrder(),i.getIsPrimary())).collect(Collectors.toList());
    List<ProductDetailResponse.VariantDto> vars=variants.stream().map(v->{
      int avail=invRepo.findByVariantId(v.getId()).stream().mapToInt(inv-> inv.getAvailable()).sum();
      java.math.BigDecimal price=v.getPriceOverride()!=null?v.getPriceOverride():(p.getSalePrice()!=null?p.getSalePrice():p.getBasePrice());
      return new ProductDetailResponse.VariantDto(v.getId(),v.getSku(),v.getSize(),v.getColor(),v.getColorHex(),price,avail,v.getIsActive());
    }).collect(Collectors.toList());
    return new ProductDetailResponse(p.getId(),p.getName(),p.getSlug(),p.getSubtitle(),p.getDescription(),p.getBrand(),p.getGender(),p.getSport(),p.getBasePrice(),p.getSalePrice(),p.getCurrency(),p.getIsFeatured(),imgs,vars,List.of(),List.of(),List.of());
  }
}
