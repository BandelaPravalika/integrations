package com.universalsaas.platform.integrations.config;

import com.universalsaas.platform.integrations.entity.IntegrationDefinition;
import com.universalsaas.platform.integrations.repository.IntegrationDefinitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IntegrationDataSeeder implements CommandLineRunner {

    private final IntegrationDefinitionRepository definitionRepository;

    @Override
    public void run(String... args) {
        seedIfMissing("GOOGLE", "Google", "GOOGLE",
                "Connect Google services like Gmail, Calendar, Drive, Meet and Sheets", "#4285F4", "PRODUCTIVITY");
        seedIfMissing("META", "Meta", "META",
                "Capture Facebook and Instagram leads", "#1877F2", "MARKETING");
        seedIfMissing("WHATSAPP", "WhatsApp", "WHATSAPP",
                "Send WhatsApp alerts and messages", "#25D366", "MESSAGING");
        seedIfMissing("ZAPIER", "Zapier", "ZAPIER",
                "Connect with external apps using Zapier", "#FF4A00", "AUTOMATION");
        seedIfMissing("WEBHOOK", "Webhook", "WEBHOOK",
                "Send real-time event data to external systems", "#6B7280", "AUTOMATION");
        seedIfMissing("ZOOM", "Zoom", "ZOOM",
                "Create and manage online meetings", "#2D8CFF", "MEETINGS");
        seedIfMissing("CASHFREE", "Cashfree", "CASHFREE",
                "Accept and verify online payments", "#6933D3", "PAYMENT");
        seedIfMissing("API_KEY", "API Keys", "API_KEY",
                "Allow external systems to access APIs securely", "#111827", "SECURITY");
    }

    private void seedIfMissing(String code, String name, String provider, String description, String color, String category) {
        if (definitionRepository.findByCode(code).isEmpty()) {
            definitionRepository.save(IntegrationDefinition.builder()
                    .code(code)
                    .name(name)
                    .provider(provider)
                    .description(description)
                    .color(color)
                    .category(category)
                    .active(true)
                    .build());
        }
    }
}
