package com.kislay.rulette.dao;

import com.kislay.rulette.metadata.RuleSystemMetaData;

public interface MetaDataDao {
    RuleSystemMetaData getRuleSystemMetaData(String ruleSystemName) throws Exception;
}
