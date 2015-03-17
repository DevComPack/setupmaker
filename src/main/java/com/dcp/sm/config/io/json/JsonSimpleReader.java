package com.dcp.sm.config.io.json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JsonSimpleReader
{
    private JSONParser parser;
    private FileReader json_file;
    private JSONObject obj;
    
    public JsonSimpleReader(String file_name) throws IOException, ParseException
    {
        parser = new JSONParser();
        json_file = new FileReader(file_name);
        obj = (JSONObject) parser.parse(json_file);
    }
    
    public JsonSimpleReader(String file_name, String root_obj) throws IOException, ParseException
    {
        parser = new JSONParser();
        json_file = new FileReader(file_name);
        obj = (JSONObject) parser.parse(json_file);
        obj = (JSONObject) obj.get(root_obj);
    }
    
    public int readInt(String name) {
        return (Integer) obj.get(name);
    }
    
    public String readString(String name) {
        return (String) obj.get(name);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> readStringList(String name) {
        JSONArray msg = (JSONArray) obj.get(name);
        List<String> list = new ArrayList<String>();
        Iterator<String> iterator = msg.iterator();
        while(iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }
    @SuppressWarnings("unchecked")
    public String[] readStringArray(String name) {
        JSONArray msg = (JSONArray) obj.get(name);
        String[] arr = new String[msg.size()];
        Iterator<String> iterator = msg.iterator();
        for(int i=0;iterator.hasNext();i++) {
            arr[i] = iterator.next();
        }
        return arr;
    }
    
    public void close() throws IOException {
        json_file.close();
    }
    

    @SuppressWarnings("unchecked")
    public static void main(String[] args)
    {
        JSONParser parser = new JSONParser();
        
        try {
     
            Object obj = parser.parse(new FileReader("src/dcp/config/io/json/test.txt"));
     
            JSONObject jsonObject = (JSONObject) obj;
     
            String name = (String) jsonObject.get("name");
            System.out.println(name);
     
            long age = (Long) jsonObject.get("age");
            System.out.println(age);
     
            // loop array
            JSONArray msg = (JSONArray) jsonObject.get("messages");
            Iterator<String> iterator = msg.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
     
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
