package service;

import model.Brigade;
import java.util.List;

public class FirstAvailableStrategy implements BrigadeSelectionStrategy {
    @Override
    public Brigade selectBrigade(List<Brigade> freeBrigades, String targetAddress) {
        // Найпростіша логіка: беремо першу вільну
        if (freeBrigades != null && !freeBrigades.isEmpty()) {
            return freeBrigades.get(0);
        }
        return null;
    }
}