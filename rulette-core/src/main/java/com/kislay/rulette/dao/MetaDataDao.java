package rulette.dao;

import rulette.metadata.RuleSystemMetaData;

public interface MetaDataDao {
    RuleSystemMetaData getRuleSystemMetaData(String ruleSystemName) throws Exception;
}
