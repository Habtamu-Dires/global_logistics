package com.yotor.global_logistics.shipment_finance.api;

import java.math.BigDecimal;
import java.util.UUID;

public interface ShipmentFinancePort {
    void createFinanceForShipment(UUID shipmentId, BigDecimal agreedAmount);
}
