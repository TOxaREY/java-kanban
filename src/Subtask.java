public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description, Integer id) {
        super(name, description, id);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", id=" + super.getId() +
                ", status=" + super.getStatus() +
                '}';
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}
