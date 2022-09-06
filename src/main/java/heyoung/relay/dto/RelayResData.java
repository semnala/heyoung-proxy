package heyoung.relay.dto;

import lombok.Data;

@Data
public class RelayResData<T> {
    private String code;
    private String message;
    private Object data;
}
