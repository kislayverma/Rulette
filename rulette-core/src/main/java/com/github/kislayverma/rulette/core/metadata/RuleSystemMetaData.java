package com.github.kislayverma.rulette.core.metadata;

import com.github.kislayverma.rulette.core.ruleinput.RuleInputConfiguration;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputConfigurator;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputValueFactory;
import com.github.kislayverma.rulette.core.ruleinput.value.DefaultDataType;
import com.github.kislayverma.rulette.core.ruleinput.value.defaults.DefaultBuilderRegistry;
import java.util.List;

/**
 * This class represents the rule systems entity model.
 */
public class RuleSystemMetaData {
    private final DefaultBuilderRegistry BUILDER_REGISTRY = new DefaultBuilderRegistry();

    private final String ruleSystemName;
    private final String tableName;
    private final List<RuleInputMetaData> inputColumnList;
    private final String uniqueIdColumnName;
    private final String uniqueOutputColumnName;

    public RuleSystemMetaData(
            String ruleSystemName,
            String tableName,
            String uniqueIdColName,
            String uniqueOutputColName,
            List<RuleInputMetaData> inputs) {
        this.ruleSystemName = ruleSystemName;
        this.tableName = tableName;
        this.uniqueIdColumnName = uniqueIdColName;
        this.uniqueOutputColumnName = uniqueOutputColName;
        this.inputColumnList = inputs;
    }

    /**
     * This method loads default configuration for all rule inputs if no custom override 
     * is given (in which case it overrides the defaults).
     * Input and output columns always get default configuration.
     * 
     * @param configuration Custom configuration for rule inputs
     */
    public void applyCustomConfiguration(RuleInputConfigurator configuration) {
        RuleInputValueFactory.getInstance().registerRuleInputBuilder(
            this.uniqueIdColumnName, BUILDER_REGISTRY.getDefaultBuilder(DefaultDataType.STRING.name()));
        RuleInputValueFactory.getInstance().registerRuleInputBuilder(
            this.uniqueOutputColumnName, BUILDER_REGISTRY.getDefaultBuilder(DefaultDataType.STRING.name()));

        if (configuration == null) {
            for (RuleInputMetaData rimd : inputColumnList) {
                RuleInputValueFactory.getInstance().registerRuleInputBuilder(
                    rimd.getName(), BUILDER_REGISTRY.getDefaultBuilder(rimd.getDataType()));
            }
        } else {
            for (RuleInputMetaData rimd : inputColumnList) {
                RuleInputConfiguration inputConfig = configuration.getConfig(rimd.getName());
                if (inputConfig != null) {
                    RuleInputValueFactory.getInstance().registerRuleInputBuilder(
                        rimd.getName(), inputConfig.getInputValueBuilder());
                } else {
                    RuleInputValueFactory.getInstance().registerRuleInputBuilder(
                        rimd.getName(), BUILDER_REGISTRY.getDefaultBuilder(rimd.getDataType()));
                }
            }
        }
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
