package com.digdes.school;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaSchoolStarter {
    //Дефолтный конструктор

    private final List<Map<String, Object>> execute = new ArrayList<>();

    public List<Map<String, Object>> execute(String request) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String[] word = request.split(" ");
        String requestForWhere = request.substring(request.toLowerCase().indexOf("where") + 6);

        if (word[0].equalsIgnoreCase("delete")) {
            if (word.length == 1) {
                execute.clear();
            } else if (word[1].equalsIgnoreCase("where")) {
                execute.removeIf(stringObjectMap -> checkCondition(stringObjectMap, requestForWhere));
            }
        }
        if (word[0].equalsIgnoreCase("SELECT")) {
            if (word.length == 1) {
                execute.forEach(System.out::println);
            } else if (word[1].equalsIgnoreCase("where")) {
                for (Map<String, Object> stringObjectMap : execute) {
                    if (checkCondition(stringObjectMap, requestForWhere)) {
                        System.out.println(stringObjectMap);
                    }
                }
            }
        }
        if (word[0].strip().equalsIgnoreCase("update") && word[1].equalsIgnoreCase("values")) {
            for (int i = 0; i < 2; i++) {
                word[i] = "";
            }
            String data = String.join(" ", word).strip();
            String update = data.substring(0, data.toLowerCase().indexOf("where"));
            String[] keyValuesPairs = update.strip().split(",");
            for (Map<String, Object> stringObjectMap : execute) {
                if (checkCondition(stringObjectMap, requestForWhere)) {
                    for (String keyValuesPair : keyValuesPairs) {
                        String[] keyValue = keyValuesPair.split("=");
                        String key = keyValue[0].strip();
                        String value = keyValue[1].strip();
                        stringObjectMap.put(key, parseValue(value));
                    }
                }
            }
        }

        if (word[0].equalsIgnoreCase("INSERT") && word[1].equalsIgnoreCase("values")) {
            for (int i = 0; i < 2; i++) {
                word[i] = "";
            }
            String data = String.join(" ", word);
            String[] keyValuesPairs = data.strip().split(",");
            for (String keyValuePair : keyValuesPairs) {
                String[] keyValue = keyValuePair.split("=");
                String key = keyValue[0].strip();
                String value = keyValue[1].strip();
                map.put(key, parseValue(value));
            }
            execute.add(map);
        }
        return execute;
    }

    private boolean checkCondition(Map<String, Object> stringObjectMap, String conditionStr) {
        String operator = "";
        Pattern pattern = Pattern.compile("(>|<|=|!=|like|ilike)");
        Pattern pattern1 = Pattern.compile("(<=|>=)");
        Matcher matcher = pattern.matcher(conditionStr);
        Matcher matcher1 = pattern1.matcher(conditionStr);
        if (matcher.find()) {
            operator = matcher.group();
        }
        if (matcher1.find()) {
            operator = matcher1.group();
        }
        String[] conditions = conditionStr.split(operator);
        if (conditions.length < 2) return false;
        String fieldName = conditions[0].trim();
        Object value = parseValue(conditions[1].trim());
        Object fieldValue = stringObjectMap.getOrDefault(fieldName, "");

        switch (operator) {
            case ">":
                return compareValues(fieldValue, value) > 0;
            case "<":
                return compareValues(fieldValue, value) < 0;
            case ">=":
                return compareValues(fieldValue, value) >= 0;
            case "<=":
                return compareValues(fieldValue, value) <= 0;
            case "=":
                return compareValues(fieldValue, value) == 0;
            case "!=":
                return compareValues(fieldValue, value) != 0;
            case "like":
                return fieldValue.toString().matches(value.toString().replace("%", ".*"));
            case "ilike":
                return fieldValue.toString().toLowerCase().matches(value.toString().toLowerCase().replace("%", ".*"));
            default:
                return false;
        }
    }

    private int compareValues(Object fieldValue, Object value) {
        if (fieldValue instanceof Comparable<?> && value instanceof Comparable<?>) {
            return ((Comparable) fieldValue).compareTo(value);
        } else {
            return fieldValue.toString().compareTo(value.toString());
        }
    }

    private Object parseValue(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e2) {
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    return Boolean.parseBoolean(value);
                } else {
                    return value.strip();
                }
            }
        }
    }
}

