package edu.hitsz.remoting.codec.serialize;

public class RemotingSerializableFactory {

    public static RemotingSerializable create(int serializableId) {
        switch (serializableId) {
            default:
            case 1:
                return new JavaSerializable();
        }
    }
}
