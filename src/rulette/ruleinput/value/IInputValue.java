package rulette.ruleinput.value;

public interface IInputValue<T> {

    T getValue();
    int compareTo(String obj) throws Exception;
    boolean isEmpty();
}
