package com.yotor.global_logistics.assignment;

import org.springframework.modulith.ApplicationModule;

@ApplicationModule(
        allowedDependencies = "identity::api, shipment::api, driverNegotiation::api"
)
public class AssignmentModule {
}
