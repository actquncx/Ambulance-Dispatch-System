package service;

import model.Brigade;
import java.util.List;

public interface BrigadeSelectionStrategy {
    Brigade selectBrigade(List<Brigade> freeBrigades, String targetAddress);
}