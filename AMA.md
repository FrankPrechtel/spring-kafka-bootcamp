#### Retry
* Es könnte eine gute Strategie sein, einen Short-Time-Retry im Kafka-Consumer von einigen Minuten (Empfehlung Hans Schüll: max. 5 min) zu konfigurieren, um kurze Ausfälle der Mongo-DB (z.B. bei Restart nach Versionsupdate) zu überbrücken. Die Use-Cases in ANOnext lassen das zu.
* Weniger ratsam ist es, den Short-Time-Retry für länger als wenige Minuten laufen zu lassen, da Kunden und Prozesse blockiert werden.

#### Queueing
* Empfehlung: Full-Stop Strategie (z.B. Bulk-Head) - Bei Ausfall der Infrastruktur wird das System angehalten und durch einen DevOp entstört und wieder angestoßen
* Bei längeren Ausfällen (Retries sind abgelaufen) bietet es sich an, mehrere Kafka Delayed-Topics (für mehrere Eskalationsstufen) zu verwenden: Nachrichten werden auf dem Delayed-Topic "geparkt" (zu bewerten: Delayed-Retry vs. Full-Stop)
* Zu beachten ist auch noch, ob eine Reihenfolge der Events beachtet werden muss. Empfehlung: Consumer am besten idempotent und stabil ggü. Reihenfolge implementieren.
* Letzte Station DLT (dort liegen lassen bzw. manuell wieder anstoßen vs. Producer könnte nochmal senden)

#### Fragen
* Gibt es eine Möglichkeit im Fehlerfall das Konsumieren eines Topics für die komplette Consumergroup zu pausieren?
* Gibt es eine elegantere Lösung auf die Header in einem Consumer zuzugreifen ohne die Verwendung von @StreamListener? Momentan sieht es bei uns so aus:
```java
@Bean
Consumer<Message<PWSDomainEvent<EmployeeDto>>> consumeEmployeeUpdatedEvent() {

return message -> {
            LOGGER.info("Delivery Attempt: " + message.getHeaders().get("deliveryAttempt"));
            PWSDomainEvent<EmployeeDto> payload = objectMapper.convertValue(message.getPayload(), new TypeReference<>() {});
            updateEmployment(payload);
        };
}
```
* Wie können wir beim Verwenden des SeekToCurrentErrorHandler nach dem letzten Attempt direkt auf die DLQ schreiben?
* Auf was genau müssen wir beim max.poll.interval.ms achten?
* Partitions for Dummys
* Was passiert wenn unsere Anwendung während unserer Retrys abstürzt?
* Andere Möglichkeit Sleuth Integration? #245 (prio niedrig)
* Ab welchem Zeitpunkt ist ein FullStop wirklich sinnvoll? Und wie sieht ein sinnvoller wieder Anlauf aus?
