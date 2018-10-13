package com.nepal.adversify.data;

import com.generic.appbase.domain.dto.ClientInfo;

import java.io.Serializable;

public class ConnectedClient implements Serializable {

    public ClientInfo clientInfo = new ClientInfo();
    public String distance;

}
