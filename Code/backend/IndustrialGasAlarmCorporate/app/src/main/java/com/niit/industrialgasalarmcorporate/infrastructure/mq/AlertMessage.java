package com.niit.industrialgasalarmcorporate.infrastructure.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertMessage implements Serializable {

    private String deviceUuid;
    private String ruleUuid;
    private String alertType;
    private String severity;
    private BigDecimal concentration;
    private BigDecimal threshold;
    private String message;
    private boolean autoCreateWorkOrder;
    private String deviceName;
}
