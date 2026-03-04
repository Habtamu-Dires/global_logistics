package com.yotor.global_logistics.shipment.domain.enums;

public enum ShipmentStatus {

    /* ---------------------------
    *  New shipment
    *  ----------------------------*/
    CREATED,                     // Consignor created initial proposal

    /* -----------------------------
     * Proposal & Negotiation
     * ----------------------------- */

    ADMIN_REQUESTED_CHANGE,      // Admin sent counter-offer
    CONSIGNOR_COUNTERED,         // Consignor sent counter-offer

    ADMIN_REJECTED_OFFER,        // Admin rejected latest offer
    CONSIGNOR_REJECTED_OFFER,    // Consignor rejected latest offer

    /* -----------------------------
     * Agreement
     * ----------------------------- */

    CONSIGNOR_ACCEPTED,          // Consignor accepted latest offer
    ADMIN_APPROVED,              // Admin finalized the deal

    /* --------------------------------
    * ASSIGNED
    * ---------------------------------- */

     DRIVER_ASSIGNED,

    /* -----------------------------
     * In Transit
     * ----------------------------- */
     IN_TRANSIT,                   // Driver assigned, shipment started

    /* -----------------------------
     * Completed
     * ----------------------------- */
    COMPLETED,                   // Shipment successfully completed\

    /* -----------------------------
     * Termination / Failure
     * ----------------------------- */

    CANCELLED_BY_CONSIGNOR,      // Consignor cancelled shipment
    CANCELLED_BY_ADMIN,          // Admin cancelled shipment
    CANCELLED_SYSTEM;            // System-level cancellation (timeout, force majeure)

    public boolean isTerminal() {
        return this == COMPLETED
                || this == CANCELLED_BY_CONSIGNOR
                || this == CANCELLED_BY_ADMIN
                || this == CANCELLED_SYSTEM;
    }

    public boolean isRejected(){
        return this == ADMIN_REJECTED_OFFER
                || this == CONSIGNOR_REJECTED_OFFER;
    }

}


