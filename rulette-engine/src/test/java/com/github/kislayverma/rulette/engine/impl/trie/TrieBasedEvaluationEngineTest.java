package com.github.kislayverma.rulette.engine.impl.trie;

import com.github.kislayverma.rulette.core.exception.RuleConflictException;
import com.github.kislayverma.rulette.core.gaia.RuleMother;
import com.github.kislayverma.rulette.core.gaia.RuleSystemMetaDataMother;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.rule.Rule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Unit Test class for TrieBasedEvaluationEngine.
 * @author arpit.jain
 */
public class TrieBasedEvaluationEngineTest {

    private static RuleSystemMetaData metaData;
    private static TrieBasedEvaluationEngine sut;

    @BeforeAll
    static void beforeAll() {
        try {
            metaData = RuleSystemMetaDataMother.getDefaultMetaData();
        } catch (Exception ex) {
            fail("Exception while getting default rule system meta data", ex);
        }
    }

    @Nested
    @DisplayName("addRule")
    class AddRule {

        @BeforeEach
        void setUp() {
            try {
                // Refresh rule engine before each test
                sut = new TrieBasedEvaluationEngine(metaData);
            } catch (Exception ex) {
                fail("Exception while instantiating TrieBasedEvaluationEngine", ex);
            }
        }

        @Test
        @DisplayName("throw RuleConflictException when conflicting rules added")
        void conflictingRuleAddition() {
            try {
                List<Rule> sampleRules = RuleMother.getDefaultRules(2, metaData);
                sut.addRule(sampleRules.get(0));
                sut.addRule(sampleRules.get(1));
                Throwable exception = assertThrows(RuleConflictException.class, () -> sut.addRule(sampleRules.get(0)));
                assertEquals(exception.getMessage(),
                        "The following existing rules conflict with the given input : " + sampleRules.get(0));

            } catch (Exception ex) {
                fail("Exception while testing conflicting rules addition", ex);
            }
        }
    }
}