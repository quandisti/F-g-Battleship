package com.battleship.networked;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ConnectionParams {
    private String username;
    private InetAddress host;
    private Integer port;

    public ConnectionParams(String username, String hostname, Integer port) throws UnknownHostException {
        this.username = username;
        this.host = InetAddress.getByName(hostname);
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public InetAddress getHost(){
        return host;
    }

    public Integer getPort() {
        return port;
    }
}
