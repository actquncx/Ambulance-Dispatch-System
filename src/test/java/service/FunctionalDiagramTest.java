package service;

import model.Brigade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.BrigadeRepository;
import repository.CallRepository;
import repository.PatientRepository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunctionalDiagramTest {

    @Mock private PatientRepository patientRepo;
    @Mock private CallRepository callRepo;
    @Mock private BrigadeRepository brigadeRepo;
    @Mock private BrigadeSelectionStrategy strategy;

    @InjectMocks
    private AmbulanceService service;

    @BeforeEach
    void setUp() throws SQLException {
        lenient().when(patientRepo.save(any())).thenReturn(1);
        lenient().when(callRepo.save(any())).thenReturn(100);
    }

    // --- ТЕСТ 1: Успішний розподіл (Всі умови True) ---
    // Причини: 1=True, 2=True, 3=True
    // Наслідки: 21, 22, 23, 24
    @Test
    @DisplayName("Test 1: Valid Data + Brigade Exists + Strategy OK -> Assigned")
    void testFullSuccess() throws SQLException {
        // Arrange (Умови)
        List<Brigade> brigades = List.of(new Brigade(1, "free", "Center"));
        when(brigadeRepo.findFreeBrigades()).thenReturn(brigades); // Причина 2 = True
        when(strategy.selectBrigade(anyList(), anyString())).thenReturn(brigades.get(0)); // Причина 3 = True

        // Act
        String result = service.registerNewCall("Ivan", "Ivanov", 12345, 30, "Street", "Flu");

        // Assert (Наслідки)
        verify(callRepo).save(any()); // Ефект 21 (Збережено виклик)
        verify(brigadeRepo).updateStatus(1, "in_transit"); // Ефект 22 (Статус бригади)
        verify(callRepo).updateBrigade(100, 1); // Ефект 23 (Прив'язка)
        assertTrue(result.contains("Призначено бригаду")); // Ефект 24
    }

    // --- ТЕСТ 2: Немає вільних бригад ---
    // Причини: 1=True, 2=False
    // Наслідки: 21, 25 (Збережено, але черга)
    @Test
    @DisplayName("Test 2: Valid Data + NO Free Brigades -> Queue")
    void testNoFreeBrigades() throws SQLException {
        // Arrange
        when(brigadeRepo.findFreeBrigades()).thenReturn(Collections.emptyList()); // Причина 2 = False

        // Act
        String result = service.registerNewCall("Ivan", "Ivanov", 12345, 30, "Street", "Flu");

        // Assert
        verify(callRepo).save(any()); // Ефект 21 (Збережено)
        verify(brigadeRepo, never()).updateStatus(anyInt(), anyString()); // НЕМАЄ Ефекту 22
        assertTrue(result.contains("Вільних бригад немає")); // Ефект 25
    }

    // --- ТЕСТ 3: Стратегія не знайшла (наприклад, далеко) ---
    // Причини: 1=True, 2=True, 3=False (повернула null)
    // Наслідки: 21, 25
    @Test
    @DisplayName("Test 3: Valid Data + Brigade Exists + Strategy NULL -> Queue")
    void testStrategyReturnsNull() throws SQLException {
        // Arrange
        List<Brigade> brigades = List.of(new Brigade(1, "free", "Center"));
        when(brigadeRepo.findFreeBrigades()).thenReturn(brigades); // Причина 2 = True
        when(strategy.selectBrigade(anyList(), anyString())).thenReturn(null); // Причина 3 = False

        // Act
        String result = service.registerNewCall("Ivan", "Ivanov", 12345, 30, "Street", "Flu");

        // Assert
        verify(callRepo).save(any()); // Ефект 21
        verify(brigadeRepo, never()).updateStatus(anyInt(), anyString()); // Немає 22
        assertTrue(result.contains("Вільних бригад немає")); // Ефект 25
    }

    // --- ТЕСТ 4: Помилка БД (Симуляція невалідного стану) ---
    // Причини: 1=False (тут симулюємо Exception)
    // Наслідки: 26 (Помилка)
    @Test
    @DisplayName("Test 4: Database Error -> Error Message")
    void testDatabaseError() throws SQLException {
        // Arrange
        when(patientRepo.save(any())).thenThrow(new SQLException("DB connection failed"));

        // Act
        String result = service.registerNewCall("Ivan", "Ivanov", 12345, 30, "Street", "Flu");

        // Assert
        assertTrue(result.contains("Помилка системи")); // Ефект 26
        verify(callRepo, never()).save(any()); // Нічого не збережено
    }
}