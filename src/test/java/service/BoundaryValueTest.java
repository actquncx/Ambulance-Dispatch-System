package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BoundaryValueTest {

    private final AmbulanceService service = new AmbulanceService(null, null, null, null);

    // --- ТЕСТ 1: Граничні значення ВІКУ (0 - 120) ---
    @ParameterizedTest(name = "Age Boundary: {0} -> Valid? {1}")
    @MethodSource("provideAgeBoundaries")
    @DisplayName("Аналіз граничних значень: Вік")
    void testAgeBoundaries(int age, boolean expected) {
        boolean actual = service.validateCallData("Test", "0991234567", age, "Street");
        assertEquals(expected, actual, "Failed on age: " + age);
    }

    private Stream<Arguments> provideAgeBoundaries() {
        return Stream.of(
                Arguments.of(-1, false),  // Lower Boundary - 1
                Arguments.of(0, true),    // Lower Boundary
                Arguments.of(1, true),    // Lower Boundary + 1
                Arguments.of(60, true),   // Nominal (Middle)
                Arguments.of(119, true),  // Upper Boundary - 1
                Arguments.of(120, true),  // Upper Boundary
                Arguments.of(121, false)  // Upper Boundary + 1
        );
    }

    // --- ТЕСТ 2: Граничні значення ДОВЖИНИ ТЕЛЕФОНУ (10 - 12) ---
    @ParameterizedTest(name = "Phone Length: {0} -> Valid? {1}")
    @MethodSource("providePhoneLengthBoundaries")
    @DisplayName("Аналіз граничних значень: Довжина телефону")
    void testPhoneBoundaries(String phone, boolean expected) {
        boolean actual = service.validateCallData("Test", phone, 30, "Street");
        assertEquals(expected, actual, "Failed on phone: " + phone);
    }

    private Stream<Arguments> providePhoneLengthBoundaries() {
        return Stream.of(
                Arguments.of("123456789", false),    // Length 9 (Min - 1)
                Arguments.of("0123456789", true),    // Length 10 (Min)
                Arguments.of("01234567890", true),   // Length 11 (Mid)
                Arguments.of("012345678901", true),  // Length 12 (Max)
                Arguments.of("0123456789012", false) // Length 13 (Max + 1)
        );
    }
}