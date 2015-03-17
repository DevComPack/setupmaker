package com.dcp.sm.logic.model;

public class RegistryKey {

  private String key; 
  
  private String name;
  
  private String defaultValue;
  
  private String variable;
  
  public RegistryKey(String key, String name, String defaultValue, String variable) {
    this.key = key;
    this.name = name;
    this.defaultValue = defaultValue;
    this.variable = variable;
  }

  public String getKey() {
    return key;
  }
  
  public String getName() {
    return name;
  }

  public String getDefaultValue() {
    return defaultValue;
  }
  
  public String getVariable() {
    return variable;
  }
}