package com.github.kislayverma.rulette.core.gaia;

import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RuleInputMetaDataMother {
    private static final Random RANDOM_NUM_GENERATOR = new Random();
    private static final String DUMMY_RULE_INPUT_NAME = "input-name-";
    private static final String DUMMY_LOWER_BOUND_FIELD_NAME = "lower-bound-field-name-";
    private static final String DUMMY_UPPER_BOUND_FIELD_NAME = "upper-bound-field-name-";

    public static List<RuleInputMetaData> getDefaultRangeMetaData(int n) throws Exception {
        if (n <= 0) {
            throw new Exception("0 or less dummy rule input objects requested");
        }

        List<RuleInputMetaData> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = RANDOM_NUM_GENERATOR.nextInt();
            dummyObjs.add(new RuleInputMetaData(DUMMY_RULE_INPUT_NAME + id, id, RuleInputType.RANGE,
                String.class.getName(), DUMMY_LOWER_BOUND_FIELD_NAME + i, DUMMY_UPPER_BOUND_FIELD_NAME + i));
        }

        return dummyObjs;
    }

    public static List<RuleInputMetaData> getDefaultValueMetaData(int n) throws Exception {
        if (n <= 0) {
            throw new Exception("0 or less dummy rule input objects requested");
        }

        List<RuleInputMetaData> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = RANDOM_NUM_GENERATOR.nextInt();
            dummyObjs.add(new RuleInputMetaData(DUMMY_RULE_INPUT_NAME + id, id, RuleInputType.VALUE, 
                String.class.getName(), "test" + i, null));
        }

        return dummyObjs;
    }
}
