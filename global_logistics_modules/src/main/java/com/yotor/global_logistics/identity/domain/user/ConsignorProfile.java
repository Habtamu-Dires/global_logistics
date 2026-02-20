package com.yotor.global_logistics.identity.domain.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("consignor_profile")
public class ConsignorProfile {

    @Id
    private Long id;

    private Long userId;

    private String businessName;
    private String tradeLicence;

    protected ConsignorProfile() {}

    public static ConsignorProfile create(
            Long userId,
            String businessName,
            String tradeLicence
    ){
        ConsignorProfile c = new ConsignorProfile();
        c.userId = userId;
        c.businessName = businessName;
        c.tradeLicence = tradeLicence;

        return c;
    }
}
