package cn.addenda.bc.bc.jc.json;

import cn.addenda.bc.bc.jc.util.DateUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author addenda
 * @since 2021/9/13
 */
public class LocalDateTimeStrSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeString(DateUtils.format(localDateTime, DateUtils.FULL_FORMATTER));
    }
}
