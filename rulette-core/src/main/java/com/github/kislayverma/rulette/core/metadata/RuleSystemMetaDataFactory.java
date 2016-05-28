package com.github.kislayverma.rulette.core.metadata;

import com.github.kislayverma.rulette.core.dao.MetaDataDao;
import com.github.kislayverma.rulette.core.dao.impl.MetaDataDaoMySqlImpl;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleSystemMetaDataFactory {

    private static RuleSystemMetaDataFactory instance;
    private final MetaDataDao metaDataDao;
    private static final Map<String, RuleSystemMetaData> metaDataMap = new ConcurrentHashMap<>();

    private RuleSystemMetaDataFactory(MetaDataDao metaDataDao) {
         this.metaDataDao = metaDataDao;
    }

    public static RuleSystemMetaDataFactory getInstance() {
        if (instance == null) {
            instance = new RuleSystemMetaDataFactory(new MetaDataDaoMySqlImpl());
        }

        return instance;
    }

    public RuleSystemMetaData getMetaData(String ruleSystemName) throws Exception {
        RuleSystemMetaData rsMetaData = metaDataMap.get(ruleSystemName);
        if (rsMetaData == null) {
            rsMetaData = metaDataDao.getRuleSystemMetaData(ruleSystemName);
            metaDataMap.put(ruleSystemName, rsMetaData);
        }

        return rsMetaData;
    }
}
