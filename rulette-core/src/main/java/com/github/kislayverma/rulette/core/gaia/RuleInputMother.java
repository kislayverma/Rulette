package com.github.kislayverma.rulette.core.gaia;

import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RuleInputMother {
    private static final Random randGen = new Random();

    public static List<RuleInput> getRandomRangeRuleInputs(int n) throws Exception {
        List<RuleInput> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = randGen.nextInt();
            dummyObjs.add(RuleInput.createRuleInput(
                "input-name-" + id, id, RuleInputType.RANGE, String.class.getName(), "inputValue-" + id));
        }

        return dummyObjs;
    }
    public static List<RuleInput> getEmptyRangeRuleInputs(int n) throws Exception {
        List<RuleInput> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = randGen.nextInt();
            dummyObjs.add(RuleInput.createRuleInput(
                "input-name-" + id, id, RuleInputType.RANGE, String.class.getName(), null));
        }

        return dummyObjs;
    }

    public static List<RuleInput> getRandomValueRuleInputs(int n) throws Exception {
        List<RuleInput> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = randGen.nextInt();
            dummyObjs.add(RuleInput.createRuleInput(
                "input-name-" + id, id, RuleInputType.VALUE, String.class.getName(), "inputValue-" + id));
        }

        return dummyObjs;
    }

    public static List<RuleInput> getEmptyValueRuleInputs(int n) throws Exception {
        List<RuleInput> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = randGen.nextInt();
            dummyObjs.add(RuleInput.createRuleInput(
                "input-name-" + id, id, RuleInputType.VALUE, String.class.getName(), null));
        }

        return dummyObjs;
    }
}
