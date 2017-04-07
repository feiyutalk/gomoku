package edu.hitsz.remoting.command.body;

import java.io.Serializable;

/**
 * @author allen
 */
public interface RemotingCommandBody extends Serializable {

    void checkFields() throws Exception;
}
