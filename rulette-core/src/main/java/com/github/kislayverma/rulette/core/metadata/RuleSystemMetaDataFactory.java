package com.github.kislayverma.rulette.core.metadata;

import com.github.kislayverma.rulette.core.dao.MetaDataDao;
import com.github.kislayverma.rulette.core.dao.impl.MetaDataDaoMySqlImpl;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RuleSystemMetaDataFactory {

    private static RuleSystemMetaDataFactory instance;
    private static MetaDataDao metaDataDao;
    private static final Map<String, RuleSystemMetaData> metaDataMap = new ConcurrentHashMap<>();

    private RuleSystemMetaDataFactory() {
        if (metaDataDao == null) {
            try {
                metaDataDao = new MetaDataDaoMySqlImpl();
            } catch (Exception ex) {
                metaDataDao = null;
                Logger.getLogger(RuleSystemMetaDataFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static RuleSystemMetaDataFactory getInstance() {
        if (instance == null) {
            instance = new RuleSystemMetaDataFactory();
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
