package com.github.kislayverma.rulette.core.metadata;

import com.github.kislayverma.rulette.core.ruleinput.RuleInputMetaData;
import java.util.List;

public class RuleSystemMetaData {

    private final String ruleSystemName;
    private final String tableName;
    private final List<RuleInputMetaData> inputColumnList;
    private final String uniqueIdColumnName;
    private final String uniqueOutputColumnName;

    public RuleSystemMetaData(
            String ruleSystemName, String tableName, String uniqueIdColName, String uniqueOutputColName, List<RuleInputMetaData> inputs) throws Exception {
        this.ruleSystemName = ruleSystemName;
        this.tableName = tableName;
        this.uniqueIdColumnName = uniqueIdColName;
        this.uniqueOutputColumnName = uniqueOutputColName;
        this.inputColumnList = inputs;
    }

    public String getTableName() {
        return tableName;
    }

    public String getUniqueIdColumnName() {
        return uniqueIdColumnName;
    }

    public String getUniqueOutputColumnName() {
        return uniqueOutputColumnName;
    }

    public List<RuleInputMetaData> getInputColumnList() {
        return inputColumnList;
    }

    public String getRuleSystemName() {
        return ruleSystemName;
    }
}
