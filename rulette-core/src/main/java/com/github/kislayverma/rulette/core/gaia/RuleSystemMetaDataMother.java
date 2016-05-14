package com.github.kislayverma.rulette.core.gaia;

import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputMetaData;
import java.util.List;

public class RuleSystemMetaDataMother {
    private static final String ruleSystemName = "gaia_rule_system";
    private static final String tableName = "gaia_table";
    private static final String uniqueIdColumnName = "rule_id";
    private static final String uniqueOutputColumnName = "rule_output_id";

    public static RuleSystemMetaData getDefaultMetaData() throws Exception {
        List<RuleInputMetaData> ruleInputs = RuleInputMetaDataMother.getDefaultValueMetaData(5);

        return new RuleSystemMetaData(
            ruleSystemName, tableName, uniqueIdColumnName, uniqueOutputColumnName, ruleInputs);
    }
}
