package com.hanyans.gachacounter.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.Level;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;


/**
 * Utility class for serializing and deserializing JSON contents.
 */
public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
            .registerModule(new SimpleModule("Log Level Module")
                    .addSerializer(Level.class, new LogLevelSerializer())
                    .addDeserializer(Level.class, new LogLevelDeserializer()));


    /**
     * Deserializes the contents of the file in the given path to an object
     * instance of the specified class.
     *
     * <p>The contents of the file must encoded using UTF-8.
     *
     * @param <T> the type of the object instance to deserialize to.
     * @param path - the path of the file to derserialize.
     * @param valueType - the class of the object instance to deserialize to.
     * @return the object instance of the deserialized contents.
     * @throws FileNotFoundException if the file cannot be found.
     * @throws DatabindException if the structure of the JSON content does not
     *      match the expected.
     * @throws IOException if any other I/O errors occurs.
     */
    public static <T> T deserialize(Path path, Class<T> valueType)
                throws FileNotFoundException, DatabindException, IOException {
        Objects.requireNonNull(path, "Path is null");
        try (BufferedReader reader = FileUtil.getFileReader(path)) {
            return objectMapper.readValue(reader, valueType);
        }
    }


    /**
     * Deserializes the contents of the given {@code Reader} to an object
     * instance of the specified class.
     *
     * <p>The contents of the reader must encoded using UTF-8. The method does
     * <b>NOT</b> close the given reader.
     *
     * @param <T> the type of the object instance to deserialize to.
     * @param reader - the reader of a content to deserialize.
     * @param valueType - the class of the object instance to deserialize to.
     * @return the object instance of the deserialized contents.
     * @throws DatabindException if the structure of the JSON content does not
     *      match the expected.
     * @throws IOException if any other I/O errors occurs.
     */
    public static <T> T deserialize(Reader reader, Class<T> valueType)
                throws DatabindException, IOException {
        Objects.requireNonNull(reader);
        return objectMapper.readValue(reader, valueType);
    }


    /**
     * Deserializes the given String of contents to an object instance of the
     * specified class.
     *
     * @param <T> the type of the object instance to deserialize to.
     * @param content - the content to deserialize.
     * @param valueType - the class of the object instance to deserialize to.
     * @return the object instance of the deserialized contents.
     * @throws DatabindException if the structure of the JSON content does not
     *      match the expected.
     * @throws IOException if any other I/O errors occurs.
     */
    public static <T> T deserialize(String content, Class<T> valueType)
                throws DatabindException, IOException {
        Objects.requireNonNull(content);
        return objectMapper.readValue(content, valueType);
    }


    /**
     * Serializes the given object instance to a file in the specified path in
     * JSON format.
     *
     * @param path - the path to the file to write to.
     * @param instance - the object instance to serialize.
     * @throws IOException if an I/O error occurs during the write process.
     */
    public static void serializeToFile(Path path, Object instance) throws IOException {
        Objects.requireNonNull(path, "Path is null");
        try (BufferedWriter writer = FileUtil.getFileWriter(path)) {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(writer, instance);
        }
    }





    private static class LogLevelSerializer extends JsonSerializer<Level> {
        @Override
        public void serialize(Level value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(String.valueOf(value));
        }
    }


    private static class LogLevelDeserializer extends JsonDeserializer<Level> {
        @Override
        public Level deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
            String levelString = p.getValueAsString();
            try {
                return Level.valueOf(levelString);
            } catch (NullPointerException nullEx) {
                throw JsonMappingException.from(
                    p,
                    String.format("Log level is null",
                            levelString, Level.values()),
                    nullEx);
            } catch (IllegalArgumentException illArgEx) {
                throw JsonMappingException.from(
                        p,
                        String.format("Unknown log level <%s>, only %s are allowed",
                                levelString, allowedLevels()),
                        illArgEx);
            }
        }


        private List<Level> allowedLevels() {
            ArrayList<Level> levels = new ArrayList<>();
            for (Level level : Level.values()) {
                levels.add(level);
            }
            return levels;
        }
    }
}
