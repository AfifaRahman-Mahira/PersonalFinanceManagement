package personalfinanceproject;

import javafx.beans.property.*;

public class Budget {
    private final StringProperty category;
    private final DoubleProperty limit;

    public Budget(String category, double limit) {
        this.category = new SimpleStringProperty(category);
        this.limit = new SimpleDoubleProperty(limit);
    }

    public StringProperty categoryProperty() {
        return category;
    }

    public DoubleProperty limitProperty() {
        return limit;
    }
}
