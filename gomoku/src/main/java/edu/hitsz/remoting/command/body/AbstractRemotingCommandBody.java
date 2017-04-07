package edu.hitsz.remoting.command.body;

import edu.hitsz.commons.utils.SystemClock;

import java.util.Map;

public abstract class AbstractRemotingCommandBody implements RemotingCommandBody {

    private static final long serialVersionUID = -8184979792935677160L;

    private String nodeType;

    private String identity;

    private Long timestamp = SystemClock.now();

    private Map<String, Object> extParams;

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getExtParams() {
        return extParams;
    }

    public void setExtParams(Map<String, Object> extParams) {
        this.extParams = extParams;
    }

}