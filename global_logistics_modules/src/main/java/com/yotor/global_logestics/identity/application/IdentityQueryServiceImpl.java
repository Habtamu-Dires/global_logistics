package com.yotor.global_logestics.identity.application;

import com.yotor.global_logestics.identity.api.IdentityQueryService;
import com.yotor.global_logestics.identity.api.dto.UserSummary;
import com.yotor.global_logestics.identity.domain.vehicle.Vehicle;
import com.yotor.global_logestics.identity.persistence.UserIdentityRepository;
import com.yotor.global_logestics.identity.persistence.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdentityQueryServiceImpl implements IdentityQueryService {

    private final UserIdentityRepository userRepo;
    private final VehicleRepository vehicleRepo;


    @Override
    public Optional<UserSummary> findUserByExternalId(UUID userExternalId) {
        return userRepo.findByExternalId(userExternalId)
                .map(UserSummary::from);
    }

    @Override
    public boolean isUserApproved(UUID userExternalId) {
        return userRepo.findByExternalId(userExternalId)
                .map(u -> u.getStatus().name().equals("APPROVED"))
                .orElse(false);
    }

    @Override
    public boolean isVehicleApproved(UUID vehicleExternalId) {
        return vehicleRepo.findByExternalId(vehicleExternalId)
                .map(Vehicle::isApproved)
                .orElse(false);
    }

}
