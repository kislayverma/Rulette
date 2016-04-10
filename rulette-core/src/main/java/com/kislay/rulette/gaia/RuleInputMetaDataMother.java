package com.kislay.rulette.gaia;

import com.kislay.rulette.ruleinput.RuleInputMetaData;
import com.kislay.rulette.ruleinput.RuleType;
import com.kislay.rulette.ruleinput.value.InputDataType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RuleInputMetaDataMother {
    private static final Random randGen = new Random();

    public static List<RuleInputMetaData> getDefaultRangeMetaData(int n) throws Exception {
        if (n <= 0) {
            throw new Exception("0 or less dummy rule input objects requested");
        }

        List<RuleInputMetaData> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = randGen.nextInt();
            dummyObjs.add(
                new RuleInputMetaData(id, "rule-input-" + id, id, RuleType.RANGE, InputDataType.STRING));
        }

        return dummyObjs;
    }

    public static List<RuleInputMetaData> getDefaultValueMetaData(int n) throws Exception {
        if (n <= 0) {
            throw new Exception("0 or less dummy rule input objects requested");
        }

        List<RuleInputMetaData> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = randGen.nextInt();
            dummyObjs.add(
                new RuleInputMetaData(id, "rule-input-" + id, id, RuleType.VALUE, InputDataType.STRING));
        }

        return dummyObjs;
    }
}
