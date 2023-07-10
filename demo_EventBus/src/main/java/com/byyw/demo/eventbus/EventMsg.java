package com.byyw.demo.eventbus;

import lombok.Data;

@Data
public class EventMsg implements EventMsgFa{
    private String id;
    private String content;
    private String code;
}
