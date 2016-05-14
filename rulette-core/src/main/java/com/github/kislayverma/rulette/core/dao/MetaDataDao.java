package com.github.kislayverma.rulette.core.dao;

import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;

public interface MetaDataDao {
    RuleSystemMetaData getRuleSystemMetaData(String ruleSystemName) throws Exception;
}
