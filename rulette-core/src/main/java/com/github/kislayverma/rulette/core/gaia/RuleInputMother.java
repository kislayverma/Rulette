package com.github.kislayverma.rulette.core.gaia;

import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.ruleinput.type.RangeInput;
import com.github.kislayverma.rulette.core.ruleinput.type.ValueInput;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class generates dummy data for unit testing.
 * @author kislay.verma
 */
public class RuleInputMother {
    private static final Random RANDOM_NUM_GENERATOR = new Random();
    private static final String DUMMY_RULE_INPUT_NAME = "input-name-";
    private static final String DUMMY_LOWER_BOUND = "range-lower-bound-";
    private static final String DUMMY_UPPER_BOUND = "range-upper-bound-";

    public static List<RuleInput> getRandomRangeRuleInputs(int n) throws Exception {
        List<RuleInput> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = RANDOM_NUM_GENERATOR.nextInt();
            dummyObjs.add(new RangeInput(
                DUMMY_RULE_INPUT_NAME + id, id, String.class.getName(), DUMMY_LOWER_BOUND + id, DUMMY_UPPER_BOUND + id));
        }

        return dummyObjs;
    }
    public static List<RuleInput> getEmptyRangeRuleInputs(int n) throws Exception {
        List<RuleInput> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = RANDOM_NUM_GENERATOR.nextInt();
            dummyObjs.add(new RangeInput(
                DUMMY_RULE_INPUT_NAME + id, id, String.class.getName(), DUMMY_LOWER_BOUND + id, DUMMY_UPPER_BOUND + id));
        }

        return dummyObjs;
    }

    public static List<RuleInput> getRandomValueRuleInputs(int n) throws Exception {
        List<RuleInput> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = RANDOM_NUM_GENERATOR.nextInt();
            dummyObjs.add(new ValueInput(
                DUMMY_RULE_INPUT_NAME + id, id, String.class.getName(), "inputValue-" + id));
        }

        return dummyObjs;
    }

    public static List<RuleInput> getEmptyValueRuleInputs(int n) throws Exception {
        List<RuleInput> dummyObjs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            int id = RANDOM_NUM_GENERATOR.nextInt();
            dummyObjs.add(new ValueInput(
                DUMMY_RULE_INPUT_NAME + id, id, String.class.getName(), null));
        }

        return dummyObjs;
    }
}
