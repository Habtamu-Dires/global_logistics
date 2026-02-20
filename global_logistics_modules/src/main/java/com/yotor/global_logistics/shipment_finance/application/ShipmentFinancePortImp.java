package com.yotor.global_logistics.shipment_finance.application;

import com.yotor.global_logistics.shipment_finance.api.ShipmentFinancePort;
import com.yotor.global_logistics.shipment_finance.domain.ShipmentFinance;
import com.yotor.global_logistics.shipment_finance.persistence.ShipmentFinanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ShipmentFinancePortImp implements ShipmentFinancePort {

    private final ShipmentFinanceRepository financeRepo;

    @Override
    public void createFinanceForShipment(UUID shipmentId, BigDecimal agreedAmount) {
        ShipmentFinance finance = ShipmentFinance.create(shipmentId, agreedAmount);
        financeRepo.save(finance);
    }
}
