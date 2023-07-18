package server.handler;

import entities.Epic;
import entities.Task;
import javafx.util.Pair;
import manager.TaskManager;

import java.net.HttpURLConnection;
import java.util.List;

public class EpicHandler extends TaskHandler {
    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    /*
    Здравствуйте!
Подскажите пожалуйста что надо поправить
в EpicHandler.java

```
    @Override
    protected Pair<Integer, String> updateEntity(Task task) {
Pair забыл добавить/закомитать в папку lib
надо исправить
```
Pair определен в javafx.util.Pair
https://www.techiedelight.com/ru/implement-pair-class-java/
class Pair<U, V>
{
	public final U first;   	// the first field of a Pair
	public final V second;  	// the second field of a Pair

	// Constructs a new Pair with specified values
	private Pair(U first, V second)
	{
		this.first = first;
		this.second = second;
	}

	@Override
	// Checks specified object is "equal to" the current object or not
	public boolean equals(Object o)
	{
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		Pair<?, ?> pair = (Pair<?, ?>) o;

		// call `equals()` method of the underlying objects
		if (!first.equals(pair.first)) {
			return false;
		}
		return second.equals(pair.second);
	}

	@Override
	// Computes hash code for an object to support hash tables
	public int hashCode()
	{
		// use hash codes of the underlying objects
		return 31 * first.hashCode() + second.hashCode();
	}

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

	// Factory method for creating a Typed Pair immutable instance
	public static <U, V> Pair <U, V> of(U a, V b)
	{
		// calls private constructor
		return new Pair<>(a, b);
	}
}

     */
    @Override
    protected Pair<Integer, String> updateEntity(Task task) {
        Epic updateEpic = (Epic) task;
        manager.updateEpic(updateEpic);
        return new Pair<>(HttpURLConnection.HTTP_OK, "");
    }

    @Override
    protected Pair<Integer, String> createEntity(Task task) {
        Epic newEpic = (Epic) task;
        final Integer id = manager.createEpic(newEpic);
        if (id == null) {
            return new Pair<>(HttpURLConnection.HTTP_INTERNAL_ERROR, "");
        }
        newEpic.setId(id);
        final String json = gson.toJson(newEpic.getId());
        return new Pair<>(HttpURLConnection.HTTP_CREATED, json);
    }

    @Override
    protected void deleteEntities() {
        manager.deleteEpics();
    }

    @Override
    protected void deleteEntities(int id) {
        manager.deleteEpic(id);
    }

    @Override
    protected String getJsonEntities() {
        final List<Epic> epics = manager.getEpics();
        if (epics == null) {
            return null;
        }
        return gson.toJson(epics);
    }

    @Override
    protected String getJsonEntities(int id) {
        final Epic epic = manager.getEpic(id);
        if (epic == null) {
            return null;
        }
        return gson.toJson(epic);
    }
}
