package com.secretsanta.api;

public class SystemContextHolder {

    private static String schema = "shaw";
    private static int year = 2018;
    
    public static void setSchema(String newSchema) {
        schema = newSchema;
    }
    
    public static String getSchema() {
        return schema;
    }
    
    public static void setCurrentYear(int newYear) {
        year = newYear;
    }
    
    public static int getCurrentYear() {
        return year;
    }
}
