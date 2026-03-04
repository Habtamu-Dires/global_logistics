package com.yotor.global_logistics.shipment;

import org.springframework.modulith.ApplicationModule;

@ApplicationModule(
        allowedDependencies = "identity::port"
)
public class ShipmentModule {
}
