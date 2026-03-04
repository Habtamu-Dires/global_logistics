package com.yotor.global_logistics.admin;

import org.springframework.modulith.ApplicationModule;

@ApplicationModule(
        allowedDependencies = "identity::port, shipment::port, assignment::port"
)
public class AdminModule {
}
