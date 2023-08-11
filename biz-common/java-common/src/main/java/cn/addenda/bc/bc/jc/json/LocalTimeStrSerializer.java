package cn.addenda.bc.bc.jc.json;

import cn.addenda.bc.bc.jc.util.DateUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalTime;

/**
 * @author addenda
 * @since 2021/9/13
 */
public class LocalTimeStrSerializer extends JsonSerializer<LocalTime> {

    @Override
    public void serialize(LocalTime localTime, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(DateUtils.format(localTime, DateUtils.HMSS_FORMATTER));
    }

}
