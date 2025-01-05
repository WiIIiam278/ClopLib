package net.william278.cloplib.operation;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class OperationTypeSerializer extends TypeAdapter<OperationType> {

    @Override
    public void write(JsonWriter out, OperationType value) throws IOException {
        if (value == null || value.getKey() == null) {
            out.nullValue();
            return;
        }
        out.value(value.getKey().asString());
    }

    @Override
    public OperationType read(JsonReader in) throws IOException {
        final String next = in.nextString();
        if (next == null) {
            return null;
        }
        return OperationType.getOrCreate(next);
    }

}
