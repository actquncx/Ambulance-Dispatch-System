package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EquivalencePartitioningTest {

    // Сервіс для тестування (мок-репозиторії тут не потрібні, бо тестуємо чисту валідацію)
    private final AmbulanceService service = new AmbulanceService(null, null, null, null);

    // --- ТЕСТ 1: Класи еквівалентності для валідації даних ---

    @ParameterizedTest(name = "{index} => {0}: name={1}, phone={2}, age={3} -> Valid? {5}")
    @MethodSource("provideValidationCases")
    @DisplayName("Валідація даних: Класи еквівалентності")
    void testCallValidationEquivalence(String description, String name, String phone, int age, String address, boolean expected) {
        boolean actual = service.validateCallData(name, phone, age, address);
        assertEquals(expected, actual, "Failed: " + description);
    }

    // Джерело даних для тесту (тут ми реалізуємо таблицю з пункту 2)
    private Stream<Arguments> provideValidationCases() {
        return Stream.of(
                // 1. ВАЛІДНИЙ КЛАС (Всі дані коректні)
                Arguments.of("TC1: Valid Data", "Ivan", "0991234567", 30, "Deribasivska 1", true),

                // 2. НЕВАЛІДНІ КЛАСИ (По одному представнику на кожну помилку)

                // Ім'я (Null / Empty)
                Arguments.of("TC2: Invalid Name (Empty)", "", "0991234567", 30, "Center", false),
                Arguments.of("TC3: Invalid Name (Null)", null, "0991234567", 30, "Center", false),

                // Телефон (Format / Length)
                Arguments.of("TC4: Invalid Phone (Too short)", "Ivan", "103", 30, "Center", false),
                Arguments.of("TC5: Invalid Phone (Letters)", "Ivan", "099abcde12", 30, "Center", false),

                // Вік (Range 0-120)
                Arguments.of("TC6: Invalid Age (Negative)", "Ivan", "0991234567", -10, "Center", false),
                Arguments.of("TC7: Invalid Age (Too old)", "Ivan", "0991234567", 150, "Center", false),

                // Адреса (Null / Empty)
                Arguments.of("TC8: Invalid Address (Null)", "Ivan", "0991234567", 30, null, false)
        );
    }
    // --- ТЕСТ 2: Граничні значення для віку ---
    @ParameterizedTest(name = "Boundary Age: {0} -> Valid? {1}")
    @MethodSource("provideBoundaryAgeCases")
    @DisplayName("Граничні значення віку")
    void testAgeBoundaries(int age, boolean expected) {
        // Інші дані фіксовані як валідні
        boolean actual = service.validateCallData("Test", "0991234567", age, "Street");
        assertEquals(expected, actual);
    }

    private Stream<Arguments> provideBoundaryAgeCases() {
        return Stream.of(
                Arguments.of(-1, false), // Трохи нижче межі
                Arguments.of(0, true),   // Нижня межа (Valid)
                Arguments.of(1, true),   // Трохи вище межі
                Arguments.of(120, true), // Верхня межа (Valid)
                Arguments.of(121, false) // Трохи вище верхньої межі
        );
    }
}