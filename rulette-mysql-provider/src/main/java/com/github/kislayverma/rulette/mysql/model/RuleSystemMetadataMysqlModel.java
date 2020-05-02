package com.github.kislayverma.rulette.mysql.model;

import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;

import java.util.List;

/**
 * This is a Mysql provider specific extension of rule system meta data. It additionally capture the rule system's unique
 * row id from the MySQL table
 */
public class RuleSystemMetadataMysqlModel extends RuleSystemMetaData {
    private final Long ruleSystemId;

    public RuleSystemMetadataMysqlModel(String ruleSystemName,
                                        String tableName,
                                        String uniqueIdColName,
                                        String uniqueOutputColName,
                                        List<RuleInputMetaData> inputs,
                                        Long ruleSystemId) {
        super(ruleSystemName,
            tableName,
            uniqueIdColName,
            uniqueOutputColName,
            inputs);
        this.ruleSystemId = ruleSystemId;
    }

    public Long getRuleSystemId() {
        return ruleSystemId;
    }
}
