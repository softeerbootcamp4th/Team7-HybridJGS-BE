package JGS.CasperEvent.global.util;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GsonUtil {
    private static final String PATTERN_DATE = "yyyy-MM-dd";
    private static final String PATTERN_TIME = "HH:mm:ss";
    private static final String PATTERN_DATETIME = String.format("%s %s", PATTERN_DATE, PATTERN_TIME);

    static class LocalDataTimeAdapter extends TypeAdapter<LocalDateTime> {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(PATTERN_DATETIME);

        @Override
        public void write(JsonWriter out, LocalDateTime value) throws IOException {
            if (value != null) out.value(value.format(format));
        }

        @Override
        public LocalDateTime read(JsonReader in) throws IOException {
            return LocalDateTime.parse(in.nextString(), format);
        }
    }

    static class LocalDateAdapter extends TypeAdapter<LocalDate> {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(PATTERN_DATE);

        @Override
        public void write(JsonWriter out, LocalDate value) throws IOException {
            out.value(value.format(format));
        }

        @Override
        public LocalDate read(JsonReader in) throws IOException {
            return LocalDate.parse(in.nextString(), format);
        }
    }

    static class LocalTimeAdapter extends TypeAdapter<LocalTime> {
        DateTimeFormatter format = DateTimeFormatter.ofPattern(PATTERN_TIME);

        @Override
        public void write(JsonWriter out, LocalTime value) throws IOException {
            out.value(value.format(format));
        }

        @Override
        public LocalTime read(JsonReader in) throws IOException {
            return LocalTime.parse(in.nextString(), format);
        }
    }

    static class EnumSerializer implements JsonSerializer<Enum<?>>{
        @Override
        public JsonElement serialize(Enum<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.name());
        }
    }

    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat(PATTERN_DATETIME)
            .registerTypeAdapter(LocalDateTime.class, new LocalDataTimeAdapter().nullSafe())
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter().nullSafe())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter().nullSafe())
            .create();

    public static Gson getGson(){
        return gson;
    }
}
