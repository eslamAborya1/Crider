package com.NTG.Cridir.util;

import java.util.HashMap;
import java.util.Map;

public class PricingUtils {

    private static final Map<String, Double> BASE_PRICES = new HashMap<>();

    static {
        BASE_PRICES.put("TOW", 400.0);
        BASE_PRICES.put("BATTERY", 200.0);
        BASE_PRICES.put("FUEL_DELIVERY", 150.0);
        BASE_PRICES.put("TIRE_CHANGE", 180.0);
    }

    public static double getBasePrice(String issueType) {
        if (issueType == null) {
            return 0.0;
        }
       
        String normalized = issueType.trim().toUpperCase();
        return BASE_PRICES.getOrDefault(normalized, 0.0);
    }
}
