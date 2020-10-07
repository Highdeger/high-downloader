package highdeger.highdownloader;

class ModelLooksItem {
    private String label;
    private String value;

    ModelLooksItem(String label, String value) {
        this.label = label;
        this.value = value;
    }

    @Override
    public String toString() {
        return label;
    }

    String getLabel() {
        return label;
    }

    String getValue() {
        return value;
    }
}
