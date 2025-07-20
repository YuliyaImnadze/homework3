package org.example;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static void main(String[] args) {
        MyThreadPool pool = new MyThreadPool(4);

        Map<Integer, String> storage = new ConcurrentHashMap<>();

        List<String> orders = List.of(
                "Заказ 001: Часы",
                "Заказ 002: Телевизор",
                "Заказ 003: Кофемашина",
                "Заказ 004: Фен",
                "Заказ 005: Лампа",
                "Заказ 006: Планшет",
                "Заказ 007: Ноутбук",
                "Заказ 008: Клавиатура"
        );

        for (int i = 0; i < orders.size(); i++) {
            int orderId = i + 1;
            String orderDetails = orders.get(i);

            pool.execute(() -> {
                long start = System.currentTimeMillis();
                System.out.println("Начало заказа %d потоком %s в %d".formatted(
                        orderId,
                        Thread.currentThread().getName(),
                        start));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                long end = System.currentTimeMillis();
                System.out.println("Завершение заказа №%d %s в %d, длительность: %d мс".formatted(
                        orderId,
                        Thread.currentThread().getName(),
                        end,
                        (end - start)));

                storage.put(orderId, "выполнялся потоком %s: %s".formatted(
                        Thread.currentThread().getName(), orderDetails));
            });

        }

        pool.shutdown();
        pool.awaitTermination();

        System.out.println("Все заказы обработаны:");
        storage.forEach((id, result) -> System.out.println(id + ": " + result));
    }
}