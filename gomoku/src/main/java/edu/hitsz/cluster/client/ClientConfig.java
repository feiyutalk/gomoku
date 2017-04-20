package edu.hitsz.cluster.client;

/**
 * Created by Neuclil on 17-4-15.
 */
public class ClientConfig {
    private int id;
    private String name;
    private String gender;
    private int age;
    private String from;
    private int image;
    private String ip;
    private int port;
    private String serverIp;
    private int serverPort;

    private ClientConfig(Builder builder){
        this.name = builder.name;
        this.gender = builder.gender;
        this.age = builder.age;
        this.from = builder.from;
        this.image = builder.image;
        this.ip = builder.ip;
        this.port = builder.port;
        this.serverIp = builder.serverIp;
        this.serverPort = builder.serverPort;
    }

    public static class Builder{
        private String name = "Ronnie";
        private String gender = "male";
        private int age = 18;
        private String from = "China";
        private int image = 1;
        private String ip = "localhost";
        private int port = 8000;
        private String serverIp = "localhost";
        private int serverPort = 8080;

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder gender(String gender){
            this.gender = gender;
            return this;
        }

        public Builder age(int age){
            this.age = age;
            return this;
        }

        public Builder from(String from){
            this.from = from;
            return this;
        }

        public Builder image(int image){
            this.image = image;
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

        public Builder serverIp(String serverIp){
            this.serverIp = serverIp;
            return this;
        }

        public Builder serverPort(int serverPort){
            this.serverPort = serverPort;
            return this;
        }

        public ClientConfig build(){
            return new ClientConfig(this);
        }
    }

    /************************* 	Getter & Setter	*************************/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

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

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public String toString() {
        return "ClientConfig{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", serverIp='" + serverIp + '\'' +
                ", serverPort=" + serverPort +
                '}';
    }
}
