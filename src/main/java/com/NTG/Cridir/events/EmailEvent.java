package com.NTG.Cridir.events;

public record EmailEvent(String to, String subject, String htmlContent) {}
