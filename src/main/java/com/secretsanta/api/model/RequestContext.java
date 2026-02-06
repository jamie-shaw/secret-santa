package com.secretsanta.api.model;

public class RequestContext {

    private static final ThreadLocal<String> schema = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();
    private static final ThreadLocal<String> recipient = new ThreadLocal<>();
    
    public static String getSchema() {
        return schema.get();
    }
    
    public static void setSchema(String schemaValue) {
        schema.set(schemaValue);
    }
    
    public static String getUsername() {
        return username.get();
    }
    
    public static void setUsername(String usernameValue) {
        username.set(usernameValue);
    }
    
    public static String getRecipient() {
        return recipient.get();
    }
    
    public static void setRecipient(String recipientValue) {
        recipient.set(recipientValue);
    }
    
    public static void clear() {
        schema.remove();
        username.remove();
        recipient.remove();
    }   
}
