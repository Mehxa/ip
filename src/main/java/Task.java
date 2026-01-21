public class Task {
    String name;
    Boolean marked;

    Task(String name){
        this.name = name;
        this.marked = false;
    }

    public void mark(){
        this.marked = true;
    }

    public void unmark(){
        this.marked = false;
    }
}
