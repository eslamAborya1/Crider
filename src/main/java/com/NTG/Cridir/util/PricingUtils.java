package com.NTG.Cridir.util;

import java.util.HashMap;
import java.util.Map;

public class PricingUtils {

    private static final Map<String, Double> BASE_PRICES = new HashMap<>();

    static {
        BASE_PRICES.put("TOWING", 400.0);
        BASE_PRICES.put("BATTERY", 200.0);
        BASE_PRICES.put("FUEL_DELIVERY", 150.0);
        BASE_PRICES.put("TIRE_CHANGE", 180.0);
        // add more service types as needed
    }

    public static double getBasePrice(String issueType) {
        return BASE_PRICES.getOrDefault(issueType.toUpperCase(), 0.0); // fallback = 100
    }
}
