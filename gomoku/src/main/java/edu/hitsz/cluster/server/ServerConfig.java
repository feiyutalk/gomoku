package edu.hitsz.cluster.server;

/**
 * Created by Neuclil on 17-4-15.
 */
public class ServerConfig {
    private String ip;
    private int port;
    private int matches;

    private ServerConfig(Builder builder) {
        this.ip = builder.ip;
        this.port = builder.port;
        this.matches = builder.matches;
    }

    public static class Builder {
        private String ip = "localhost";
        private int port = 8080;
        private int matches = 256;

        public Builder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }

        public Builder matches(int matches){
            this.matches = matches;
            return this;
        }

        public ServerConfig build() {
            return new ServerConfig(this);
        }
    }

    /************************* 	Getter & Setter	*************************/
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMatches() {
        return matches;
    }

    public void setMatches(int matches) {
        this.matches = matches;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
