package edu.hitsz.remoting.codec.serialize;

import edu.hitsz.commons.io.UnsafeByteArrayInputStream;
import edu.hitsz.commons.io.UnsafeByteArrayOutputStream;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author allen
 */
public class JavaSerializable implements RemotingSerializable {

    //该字段是为了序列化和反序列化用一样的方式
    @Override
    public int getId() {
        return 1;
    }

    @Override
    public byte[] serialize(Object obj) throws Exception {
        UnsafeByteArrayOutputStream bos = new UnsafeByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);

        try {
            oos.writeObject(obj);
            oos.flush();
            return bos.toByteArray();
        } finally {
            oos.close();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception {
        UnsafeByteArrayInputStream bin = new UnsafeByteArrayInputStream(data);
        ObjectInputStream ois = new ObjectInputStream(bin);

        try {
            Object obj = ois.readObject();
            return (T) obj;
        } finally {
            ois.close();
        }
    }
}