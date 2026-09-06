package com.novaathletics.modules.order.entity;
import jakarta.persistence.*; import java.time.Instant;
@Entity @Table(name="shipments") public class Shipment {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="order_id", nullable=false) private Long orderId;
  @Column(name="warehouse_id") private Long warehouseId;
  private String carrier; @Column(name="tracking_number") private String trackingNumber;
  private String status="PENDING";
  @Column(name="shipped_at") private Instant shippedAt;
  @Column(name="delivered_at") private Instant deliveredAt;
  @Column(name="created_at") private Instant createdAt=Instant.now();
  @Column(name="updated_at") private Instant updatedAt=Instant.now();
  public Long getId(){return id;} public void setId(Long v){id=v;}
  public Long getOrderId(){return orderId;} public void setOrderId(Long v){orderId=v;}
  public Long getWarehouseId(){return warehouseId;} public void setWarehouseId(Long v){warehouseId=v;}
  public String getCarrier(){return carrier;} public void setCarrier(String v){carrier=v;}
  public String getTrackingNumber(){return trackingNumber;} public void setTrackingNumber(String v){trackingNumber=v;}
  public String getStatus(){return status;} public void setStatus(String v){status=v;}
  public Instant getShippedAt(){return shippedAt;} public void setShippedAt(Instant v){shippedAt=v;}
  public Instant getDeliveredAt(){return deliveredAt;} public void setDeliveredAt(Instant v){deliveredAt=v;}
}
