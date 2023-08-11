package cn.addenda.bc.bc.jc.json;

import cn.addenda.bc.bc.jc.util.DateUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author addenda
 * @since 2021/9/13
 */
public class LocalDateStrDeSerializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode jsonNode = jp.getCodec().readTree(jp);
        final String s = jsonNode.asText();
        return DateUtils.parseLd(s, DateUtils.YMD_FORMATTER);
    }
}
