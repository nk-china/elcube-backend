package cn.nkpro.tfms.platform.bpm;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NkBPMUtils {

    public static List<String> getProperty(FlowElement element, String name){
        return getPropertys(element)
                .entrySet()
                .stream()
                .filter(e-> StringUtils.equals(e.getKey(),name))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    public static Map<String, List<String>> getPropertys(FlowElement element){

        Map<String, List<String>> result = new HashMap<>();

        element
                .getExtensionElements()
                .entrySet()
                .stream()
                .filter(ee -> "properties".equals(ee.getKey()))
                .findFirst()
                .flatMap(stringListEntry ->
                        stringListEntry.getValue()
                        // properties
                        .stream()
                        .findFirst()
                        .flatMap(extensionElement ->
                                extensionElement
                                // properties[0]
                                .getChildElements()

                                // properties[0].childElements
                                .entrySet()
                                .stream()
                                .filter(ee -> "property".equals(ee.getKey()))
                                .findFirst())).ifPresent(stringListEntry1 ->
                                        stringListEntry1.getValue()
                                        .forEach((extensionElement1 -> {
                                            String key = extensionElement1.getAttributes().get("name").get(0).getValue();
                                            String value = extensionElement1.getAttributes().get("value").get(0).getValue();
                                            result.computeIfAbsent(key, (k) -> new ArrayList<>()).add(value);
                                        })));

        return result;
    }
}
