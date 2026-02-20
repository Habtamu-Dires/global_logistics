package com.yotor.global_logistics.driver_negotiation.application;

import com.yotor.global_logistics.driver_negotiation.domain.dto.NegotiationStatus;
import com.yotor.global_logistics.driver_negotiation.api.DriverNegotiationQueryService;
import com.yotor.global_logistics.driver_negotiation.application.dto.DriverNegotiationResponse;
import com.yotor.global_logistics.driver_negotiation.domain.DriverNegotiation;
import com.yotor.global_logistics.driver_negotiation.persistence.DriverNegotiationRepository;
import com.yotor.global_logistics.exception.BusinessException;
import com.yotor.global_logistics.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverNegotiationQueryServiceImp implements DriverNegotiationQueryService {


    private final DriverNegotiationRepository driverNegotiationRepo;

    @Override
    public DriverNegotiationResponse markSelected(UUID negotiationId) {
        DriverNegotiation negotiation = driverNegotiationRepo
                .findByNegotiationId(negotiationId)
                .orElseThrow(() -> new BusinessException(ErrorCode.OFFER_NOT_FOUND));

        negotiation.markSelected();
        driverNegotiationRepo.save(negotiation);

        return DriverNegotiationResponse.from(negotiation);
    }

    @Override
    public int countByShipmentAndStatus(UUID shipmentId, NegotiationStatus status) {
        return driverNegotiationRepo.countByShipmentAndStatus(shipmentId, status);
    }


    @Override
    public void markDriversNotSelected(UUID shipmentId){
        List<DriverNegotiation> driverAccepts =
                driverNegotiationRepo.findByShipmentAndStatus(
                        shipmentId,
                        NegotiationStatus.DRIVER_ACCEPTS
                );

        for (DriverNegotiation n : driverAccepts) {
            n.markNotSelected();
        }

        driverNegotiationRepo.saveAll(driverAccepts);
    }

    @Override
    public void markOthersExpired(UUID shipmentId){
        List<DriverNegotiation> expired =
                driverNegotiationRepo.findExpiredNegotiations(
                        shipmentId,
                        List.of(NegotiationStatus.OFFER_SENT,
                                NegotiationStatus.ADMIN_COUNTERED,
                                NegotiationStatus.DRIVER_COUNTERED
                        )
                );

        for (DriverNegotiation n : expired) {
            n.markExpired();
        }

        driverNegotiationRepo.saveAll(expired);
    }
}
