package com.yotor.global_logistics.assignment;

import org.springframework.modulith.ApplicationModule;

@ApplicationModule(
        allowedDependencies = "identity::port, shipment::port, driverNegotiation::port"
)
public class AssignmentModule {
}
