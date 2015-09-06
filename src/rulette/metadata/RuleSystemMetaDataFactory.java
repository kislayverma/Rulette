package rulette.metadata;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import rulette.dao.MetaDataDao;
import rulette.dao.impl.MetaDataDaoMySqlImpl;

public class RuleSystemMetaDataFactory {

    private static final RuleSystemMetaDataFactory instance = new RuleSystemMetaDataFactory();
    private MetaDataDao metaDataDao;
    private static Map<String, RuleSystemMetaData> metaDataMap;

    private RuleSystemMetaDataFactory() {
        try {
            metaDataDao = new MetaDataDaoMySqlImpl();
        } catch (Exception ex) {
            metaDataDao = null;
            Logger.getLogger(RuleSystemMetaDataFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static RuleSystemMetaDataFactory getInstance() {
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
