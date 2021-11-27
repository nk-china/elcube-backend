package cn.nkpro.easis.task.model;

import lombok.Data;

import java.util.Map;

@Data
public class DmnDecisionRunModel {

        private String decisionKey;
        private String xml;
        private Map<String,Object> variables;
}