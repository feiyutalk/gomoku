package edu.hitsz.remoting.codec;

import edu.hitsz.remoting.codec.serialize.RemotingSerializable;
import edu.hitsz.remoting.codec.serialize.RemotingSerializableFactory;
import edu.hitsz.remoting.command.RemotingCommand;
import edu.hitsz.remoting.command.body.RemotingCommandBody;

import java.nio.ByteBuffer;

public class FrobotCodec implements Codec {

    @Override
    public RemotingCommand decode(ByteBuffer byteBuffer) throws Exception {
        int length = byteBuffer.limit();
        int serializableId = byteBuffer.getInt();

        RemotingSerializable serializable = RemotingSerializableFactory.create(serializableId);
        int headerLength = byteBuffer.getInt();
        byte[] headerData = new byte[headerLength];
        byteBuffer.get(headerData);

        RemotingCommand cmd = serializable.deserialize(headerData, RemotingCommand.class);
        int remaining = length - 4 - 4 - headerLength;
        if (remaining > 0) {
            int bodyLength = byteBuffer.getInt();
            int bodyClassLength = remaining - 4 - bodyLength;
            if (bodyLength > 0) {
                byte[] bodyData = new byte[bodyLength];
                byteBuffer.get(bodyData);

                byte[] bodyClassData = new byte[bodyClassLength];
                byteBuffer.get(bodyClassData);

                RemotingCommandBody remotingCommandBody = (RemotingCommandBody) serializable.deserialize(bodyData, Class.forName(new String(bodyClassData)));
                cmd.setBody(remotingCommandBody);
            }
        }
        return cmd;
    }

    @Override
    public ByteBuffer encode(RemotingCommand remotingCommand) throws Exception {
        RemotingSerializable serializable = RemotingSerializableFactory.create(remotingCommand.getSid());
        //length 
        int length = 0;
        //serializableId
        length += 4;
        //header length 
        length += 4;
        //header data length
        byte[] headerData = serializable.serialize(remotingCommand);
        length += headerData.length;

        byte[] bodyData = null;
        byte[] bodyClass = null;

        RemotingCommandBody body = remotingCommand.getBody();

        if (body != null) {
            //body length
            length += 4;
            //body data
            bodyData = serializable.serialize(body);
            length += bodyData.length;
            //class data
            bodyClass = body.getClass().getName().getBytes();
            length += bodyClass.length;
        }

        ByteBuffer result = ByteBuffer.allocate(4 + length);
        //length 
        result.putInt(length);
        //serializableId
        result.putInt(serializable.getId());
        //header length
        result.putInt(headerData.length);
        //header data
        result.put(headerData);
        if (bodyData != null) {
            //body length
            result.putInt(bodyData.length);
            //body data
            result.put(bodyData);
            //body class
            result.put(bodyClass);
        }
        result.flip();
        return result;
    }

}
