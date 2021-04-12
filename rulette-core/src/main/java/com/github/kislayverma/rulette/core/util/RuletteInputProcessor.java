package com.github.kislayverma.rulette.core.util;

import com.github.kislayverma.rulette.core.annotations.RuletteInput;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; 

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 11110 on 08/07/16.
 */
public class RuletteInputProcessor {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Map<String, String> generateInputMap(Object request) throws IllegalAccessException {

        Map<String, String> inputMap = new HashMap<>();
        Field[] fieldsInObject = request.getClass().getDeclaredFields();
        for(Field field : fieldsInObject){
            RuletteInput ruletteInput = field.getAnnotation(RuletteInput.class);
            if(ruletteInput == null){
                continue;
            }
            field.setAccessible(true);
            Object fieldValue = field.get(request);
            String columnName = ruletteInput.name();
            String fieldValueString;
            if(fieldValue == null){
                fieldValueString = "";
            }else {
                if(fieldValue instanceof Date){
                    fieldValueString = LocalDateTime.parse((CharSequence)fieldValue, formatter).format(formatter);
                }else {
                    fieldValueString = fieldValue.toString();
                }
            }
            inputMap.put(columnName, fieldValueString);
        }
        return inputMap;
    }


}
