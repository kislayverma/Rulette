package com.github.kislayverma.rulette.core.gaia;

import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RuleInputMother {
    private static final Random randGen = new Random();

    public static List<RuleInput> getDefaultRangeRuleInputs(int n) throws Exception {
        if (n <= 0) {
            throw new Exception("0 or less dummy rule input objects requested");
        }

        List<RuleInput> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = randGen.nextInt();
            dummyObjs.add(RuleInput.createRuleInput(id, "input-name-" + id, id, RuleInputType.RANGE, String.class.getName(), "inputValue-" + id));
        }

        return dummyObjs;
    }

    public static List<RuleInput> getDefaultValueRuleInputs(int n) throws Exception {
        if (n <= 0) {
            throw new Exception("0 or less dummy rule input objects requested");
        }

        List<RuleInput> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = randGen.nextInt();
            dummyObjs.add(RuleInput.createRuleInput(id, "input-name-" + id, id, RuleInputType.RANGE, String.class.getName(), "inputValue-" + id));
        }

        return dummyObjs;
    }
}
