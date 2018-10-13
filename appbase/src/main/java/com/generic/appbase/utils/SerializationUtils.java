package com.generic.appbase.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationUtils {

    public static InputStream serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(obj);

        oos.flush();
        oos.close();

        return new ByteArrayInputStream(baos.toByteArray());
    }

    public static Object deserialize(InputStream data) throws IOException, ClassNotFoundException {
        ObjectInputStream is = new ObjectInputStream(data);
        return is.readObject();
    }

}
