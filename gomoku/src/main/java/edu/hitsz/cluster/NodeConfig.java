package edu.hitsz.cluster;


import edu.hitsz.commons.constants.Constants;

import java.io.Serializable;
import java.util.UUID;

/**
 * 节点配置信息
 * @author
 */
public class NodeConfig implements Serializable {

    private static final long serialVersionUID = -8283382582968938472L;
    /* 是否可用 */
    private boolean available = true;
    /* 通信服务ip */
    private String ip;
    /* 通信服务端口*/
    private int port;
    /* 节点唯一标识 */
    private String identity;
    /* 节点类型 */
    private NodeType nodeType;
    /* 注册中心地址 */
    private String registryAddress;
    /* 请求超时时间*/
    private int invokeTimeoutMillis;
    /* 系统的workspace路径*/
    private String workspacePath;
    /* 系统集群名*/
    private String clusterName;

    public static class Builder implements Serializable{
        private boolean available = true;
        private String ip = "localhost";
        private int port = 8080;
        private String identity = UUID.randomUUID().toString();
        private NodeType nodeType = NodeType.SERVER;
        private String registryAddress;
        private int invokeTimeoutMillis = 20000;
        private String workspacePath = Constants.USER_HOME + "/";
        private String clusterName = "Frobot";

        public Builder available(boolean available){
            this.available = available;
            return this;
        }

        public Builder ip(String ip){
            this.ip = ip;
            return this;
        }

        public Builder port(int port){
            this.port = port;
            return this;
        }

        public Builder identity(String identity){
            this.identity = identity;
            return this;
        }

        public Builder nodeType(NodeType nodeType){
            this.nodeType = nodeType;
            return this;
        }

        public Builder registerAddress(String registerAddress){
            this.registryAddress = registerAddress;
            return this;
        }

        public Builder invokeTimeoutMillis(int invokeTimeoutMillis){
            this.invokeTimeoutMillis = invokeTimeoutMillis;
            return this;
        }

        public Builder workspacePath(String workspacePath){
            this.workspacePath = workspacePath;
            return this;
        }

        public Builder clusterName(String clusterName){
            this.clusterName = clusterName;
            return this;
        }

        public NodeConfig build(){
            return new NodeConfig(this);
        }
    }

    private NodeConfig(Builder builder){
        this.available = builder.available;
        this.ip = builder.ip;
        this.port = builder.port;
        this.identity = builder.identity;
        this.nodeType = builder.nodeType;
        this.registryAddress = builder.registryAddress;
        this.invokeTimeoutMillis = builder.invokeTimeoutMillis;
        this.workspacePath = builder.workspacePath;
        this.clusterName = builder.clusterName;
    }

    /************************* 	Getter & Setter	*************************/
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public int getInvokeTimeoutMillis() {
        return invokeTimeoutMillis;
    }

    public void setInvokeTimeoutMillis(int invokeTimeoutMillis) {
        this.invokeTimeoutMillis = invokeTimeoutMillis;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public void setWorkspacePath(String workspacePath) {
        this.workspacePath = workspacePath;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    @Override
    public String toString() {
        return "NodeConfig [available=" + available + ", ip=" + ip + ", identity=" + identity + ", nodeType=" + nodeType
                + ", registryAddress=" + registryAddress + ", invokeTimeoutMillis=" + invokeTimeoutMillis + ", port="
                + port + ", projectPath=" + workspacePath + ", clusterName=" + clusterName + "]";
    }

}
