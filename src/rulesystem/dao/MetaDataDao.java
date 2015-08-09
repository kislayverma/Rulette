package rulesystem.dao;

import rulesystem.metadata.RuleSystemMetaData;


public interface MetaDataDao {
    RuleSystemMetaData getRuleSystemMetaData(String ruleSystemName) throws Exception;
}
