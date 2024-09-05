package com.axana.app;

import java.lang.reflect.Method;

import ca.uhn.hl7v2.model.AbstractSegment;

public class HL7ReflectionUtil {

    public static String getFieldName(AbstractSegment segment, String segmentName, int fieldID) {
        try {
            String methodName = "get" + segmentName + "-" + fieldID;
            Method method = segment.getClass().getMethod(methodName);
            Object fieldObject = method.invoke(segment);
            return fieldObject != null ? fieldObject.getClass().getSimpleName() : "Unknown";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving field name";
        }
    }
}
