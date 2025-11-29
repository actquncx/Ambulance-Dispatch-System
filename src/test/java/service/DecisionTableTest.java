package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import service.AmbulanceService;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DecisionTableTest {

    // Створюємо екземпляр сервісу для тестів (тут можна передати null у репозиторії, бо ми тестуємо чисту логіку)
    private final AmbulanceService service = new AmbulanceService(null, null, null, null);


    // ТАБЛИЦЯ 1: Валідація створення виклику
    @DisplayName("Таблиця 1: Валідація даних виклику")
    @ParameterizedTest(name = "{index} => name={0}, phone={1}, age={2}, addr={3} -> VALID? {4}")
    @CsvSource({
            // Name      | Phone        | Age | Address    | Expected (Valid?)
            "Ivan,       0991234567,    30,   Street 1,    true",   // T1: Все вірно
            ",           0991234567,    30,   Street 1,    false",  // T2: Немає імені
            "Ivan,       123,           30,   Street 1,    false",  // T3: Короткий телефон
            "Ivan,       0991234567,    -5,   Street 1,    false",  // T4: Невірний вік (від'ємний)
            "Ivan,       0991234567,    150,  Street 1,    false",  // T5: Невірний вік (>120)
            "Ivan,       0991234567,    30,   ,            false"   // T6: Немає адреси
    })
    void testCallValidation(String name, String phone, int age, String address, boolean expected) {
        boolean actual = service.validateCallData(name, phone, age, address);
        assertEquals(expected, actual, "Failed on case: " + name);
    }

    // ТАБЛИЦЯ 2: Визначення доступності бригади
    @DisplayName("Таблиця 2: Доступність бригади")
    @ParameterizedTest(name = "{index} => status={0}, shiftEnd={1}, current={2} -> AVAILABLE? {3}")
    @CsvSource({
            "free,    20,        10,       true",   // T1: Вільна, зміна триває
            "busy,    20,        10,       false",  // T2: Зайнята
            "lunch,   20,        10,       false",  // T3: Обід
            "free,    20,        21,       false",  // T4: Зміна закінчилась
            "free,    20,        20,       false"   // T5: Кінець зміни прямо зараз
    })
    void testBrigadeAvailability(String status, int shiftEnd, int current, boolean expected) {
        boolean actual = service.isBrigadeAvailable(status, shiftEnd, current);
        assertEquals(expected, actual);
    }

    // ТАБЛИЦЯ 3: Пріоритизація виклику
    @DisplayName("Таблиця 3: Пріоритет виклику")
    @ParameterizedTest(name = "{index} => diag={0}, age={1} -> PRIORITY {2}")
    @CsvSource({
            "heart attack,    50,   CRITICAL",  // T1: Серцевий напад
            "stroke,          70,   CRITICAL",  // T2: Інсульт
            "car accident,    25,   HIGH",      // T3: ДТП
            "fever,           2,    HIGH",      // T4: Дитина (<3 років)
            "flu,             85,   MEDIUM",    // T5: Літня людина (>80) з грипом
            "flu,             25,   LOW",       // T6: Звичайна застуда
            ",                30,   LOW"        // T7: Немає діагнозу
    })
    void testPriorityCalculation(String diagnosis, int age, String expected) {
        String actual = service.calculatePriority(diagnosis, age);
        assertEquals(expected, actual);
    }
}