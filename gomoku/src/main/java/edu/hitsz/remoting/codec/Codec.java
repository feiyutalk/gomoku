package edu.hitsz.remoting.codec;

import edu.hitsz.remoting.command.RemotingCommand;

import java.nio.ByteBuffer;

/**
 * @author allen
 *         编码解码类的公共接口，必须实现编码 和 解码的方法
 *         注意： 解码后 返回的 必须是 RemotingCommand
 *         编码时 传入的参数 必须是 RemotingCommand
 */
public interface Codec {
    RemotingCommand decode(final ByteBuffer byteBuffer) throws Exception;

    ByteBuffer encode(final RemotingCommand remotingCommand) throws Exception;
}
