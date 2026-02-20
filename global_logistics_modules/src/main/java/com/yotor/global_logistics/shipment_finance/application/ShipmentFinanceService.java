package com.yotor.global_logistics.shipment_finance.application;

import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import com.yotor.global_logistics.shipment_finance.application.dto.ShipmentPaymentRequest;
import com.yotor.global_logistics.shipment_finance.domain.ShipmentFinance;
import com.yotor.global_logistics.shipment_finance.domain.ShipmentPayment;
import com.yotor.global_logistics.shipment_finance.persistence.ShipmentFinanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentFinanceService {

    private final ShipmentFinanceRepository financeRepo;

    public void CreateShipmentPayment(UUID shipmentFinanceId, ShipmentPaymentRequest req){

        ShipmentFinance finance = financeRepo.findByPublicId(shipmentFinanceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        ShipmentPayment payment = ShipmentPayment.create(
                req.paidAmount(),
                req.referenceNo(),
                req.slipUrl(),
                req.paidAt()
        );

        finance.registerPayment(payment);
        financeRepo.save(finance);
    }

    @Transactional
    public void verifyShipmentPayment(UUID paymentPublicId, UUID adminId) {

        ShipmentFinance finance =
                financeRepo.findByPaymentPublicId(paymentPublicId)
                        .orElseThrow(()-> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        finance.verifyPayment(paymentPublicId, adminId);

        financeRepo.save(finance);
    }
}
