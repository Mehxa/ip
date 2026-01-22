public class Task {
    String name;
    Boolean marked;
    String taskStr;

    Task(String name){
        this.name = name;
        this.marked = false;
        this.taskStr = "[%s] %s";
    }

    public void mark(){
        this.marked = true;
    }

    public void unmark(){
        this.marked = false;
    }

    public String getStatusString() {
        return (marked ? "X" : " ");
    }

    public String toString() {
        return String.format(taskStr,this.getStatusString(),this.name);
    }
}
