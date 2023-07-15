package manager.utilities;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        try {
            long aLong = in.nextLong();
            Instant instant = Instant.ofEpochMilli(aLong);
            return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        } catch (Exception ex) {
            return null;
        }

    }

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            Instant instant = value.atZone(ZoneId.systemDefault()).toInstant();
            Date date = Date.from(instant);
            out.value(date.getTime());
        }
    }
}
