package se.llbit.chunky.ui;

import javafx.scene.control.Tooltip;
import se.llbit.fxutil.CustomizedListCellFactory;
import se.llbit.util.Registerable;

public class RegisterableCellAdapter implements CustomizedListCellFactory.Adapter<Registerable> {
    public static final RegisterableCellAdapter INSTANCE = new RegisterableCellAdapter();

    private RegisterableCellAdapter() {
    }

    @Override
    public String getLabel(Registerable item) {
        return item.getName();
    }

    @Override
    public Tooltip getTooltip(Registerable item) {
        String description = item.getDescription();
        if (description != null && !description.isEmpty()) {
            return new Tooltip(item.getDescription());
        }
        return null;
    }
}
