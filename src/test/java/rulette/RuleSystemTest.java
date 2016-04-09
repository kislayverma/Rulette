package test.java.rulette;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.powermock.core.classloader.annotations.PrepareForTest;
import rulette.RuleSystem;
import rulette.dao.DataSource;
import rulette.dao.impl.BaseDaoMySqlImpl;
import rulette.gaia.RuleSystemMetaDataMother;
import org.powermock.modules.junit4.PowerMockRunner;
import rulette.metadata.RuleSystemMetaDataFactory;
import org.mockito.MockitoAnnotations;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.support.membermodification.MemberModifier;
import rulette.dao.impl.MetaDataDaoMySqlImpl;
import rulette.dao.impl.RuleSystemDaoMySqlImpl;
import rulette.gaia.RuleMother;
import rulette.metadata.RuleSystemMetaData;

/**
 *
 * @author kislay.verma
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
    RuleSystem.class,
    RuleSystemMetaDataFactory.class,
    DataSource.class,
    MetaDataDaoMySqlImpl.class,
    RuleSystemDaoMySqlImpl.class,
    BaseDaoMySqlImpl.class})
public class RuleSystemTest {

    @InjectMocks
    private RuleSystem sut = new RuleSystem();

    public RuleSystemTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws NoSuchMethodException {
        MockitoAnnotations.initMocks(this);
        MemberModifier.suppress(BaseDaoMySqlImpl.class.getConstructor());
        PowerMock.mockStatic(DataSource.class);
    }

    @After
    public void tearDown() {
    }

    //@Test(expected = Exception.class)
    public void testCreateInvalidName() throws Exception {
        RuleSystemMetaData rsMetaData = RuleSystemMetaDataMother.getDefaultMetaData();

        MetaDataDaoMySqlImpl mockDao = PowerMock.createMock(MetaDataDaoMySqlImpl.class);
        PowerMock.expectNew(MetaDataDaoMySqlImpl.class).andReturn(mockDao);
        EasyMock.expect(mockDao.getRuleSystemMetaData("wrongName")).andThrow(new Exception());
        PowerMock.replayAll();

        RuleSystemDaoMySqlImpl mockRsDao = PowerMock.createMock(RuleSystemDaoMySqlImpl.class);
        PowerMock.expectNew(RuleSystemDaoMySqlImpl.class).andReturn(mockRsDao);
        EasyMock.expect(mockRsDao.getAllRules(rsMetaData.getRuleSystemName()))
                .andReturn(RuleMother.getDefaultRules(10, rsMetaData));
        PowerMock.replayAll();
        try {
            sut = new RuleSystem("wrongName", null);
        } catch (Exception e) {
            Assert.assertNull(sut);
        }

//        PowerMock.verifyAll();
    }

    @Test
    public void testFullInitialization() throws Exception {
        RuleSystemMetaData rsMetaData = RuleSystemMetaDataMother.getDefaultMetaData();

        MetaDataDaoMySqlImpl mockDao = PowerMock.createMock(MetaDataDaoMySqlImpl.class);
        PowerMock.expectNew(MetaDataDaoMySqlImpl.class).andReturn(mockDao);
        EasyMock.expect(mockDao.getRuleSystemMetaData(rsMetaData.getRuleSystemName())).andReturn(rsMetaData);
        PowerMock.replayAll();

        RuleSystemDaoMySqlImpl mockRsDao = PowerMock.createMock(RuleSystemDaoMySqlImpl.class);
        PowerMock.expectNew(RuleSystemDaoMySqlImpl.class).andReturn(mockRsDao);
        EasyMock.expect(mockRsDao.getAllRules(rsMetaData.getRuleSystemName()))
                .andReturn(RuleMother.getDefaultRules(10, rsMetaData));
        PowerMock.replayAll();

        sut = new RuleSystem(rsMetaData.getRuleSystemName(), null);
        Assert.assertNotNull(sut);

        PowerMock.verifyAll();
    }
}
