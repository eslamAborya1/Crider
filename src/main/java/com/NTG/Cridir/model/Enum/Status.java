    package com.NTG.Cridir.model.Enum;

    public enum Status {
        PENDING,       // لسه محدش قبل
        ACCEPTED,      // Provider قبل الطلب
        WAITING_CUSTOMER, // Provider قبل → في انتظار رد الكستمر
        CONFIRMED,     // الكستمر وافق
        REJECTED,      // الكستمر رفض
        CANCELLED,     // الكستمر لغى
        COMPLETED
    }
