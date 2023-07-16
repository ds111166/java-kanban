package manager.utilities;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import entities.Task;

import java.io.IOException;

public class TaskAdapter extends TypeAdapter<Task> {

    @Override
    public void write(JsonWriter out, Task value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {

        }
    }

    @Override
    public Task read(JsonReader in) throws IOException {
        return null;
    }
}
