package com.yotor.global_logistics.driver_negotiation;

import org.springframework.modulith.ApplicationModule;

@ApplicationModule(
        allowedDependencies = "identity::api, shipment::api"
)
public class DriverNegotiationModule {
}
