package com.generic.appbase.domain.dto;

import java.io.Serializable;

public class ClientInfo implements Serializable {

    public String name;
    public byte[] avatar;
    public Location location = new Location();

}
