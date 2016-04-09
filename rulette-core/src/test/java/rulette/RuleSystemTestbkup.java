package test.java.rulette;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import rulette.RuleSystem;
import rulette.dao.DataSource;
import rulette.dao.MetaDataDao;
import rulette.dao.impl.BaseDaoMySqlImpl;
import rulette.gaia.RuleSystemMetaDataMother;
import org.powermock.modules.junit4.PowerMockRunner;
import rulette.metadata.RuleSystemMetaDataFactory;
import org.mockito.MockitoAnnotations;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.support.membermodification.MemberModifier;
import rulette.dao.impl.MetaDataDaoMySqlImpl;

/**
 *
 * @author kislay.verma
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
    //RuleSystemMetaDataFactory.class,
    DataSource.class,
    MetaDataDaoMySqlImpl.class,
    BaseDaoMySqlImpl.class})
public class RuleSystemTestbkup {

    @InjectMocks
    private RuleSystem sut = new RuleSystem();

    @Mock
    private MetaDataDao metaDataDao;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws NoSuchMethodException {
        MockitoAnnotations.initMocks(this);
        //BaseDaoMySqlImpl bdmi = PowerMockito.mock(BaseDaoMySqlImpl.class);
        // Suppress the constructor of the Base dao so that db conections arent set up
        MemberModifier.suppress(BaseDaoMySqlImpl.class.getConstructor());
        MemberModifier.suppress(MetaDataDaoMySqlImpl.class.getConstructor());
        PowerMock.mockStatic(DataSource.class);
    }

    @After
    public void tearDown() {
    }

    //@Test(expected = Exception.class)
    public void testCreateInvalidName() throws Exception {
        String wrongName = "wrongName";

        sut = new RuleSystem(wrongName, null);
    }

    @Test
    public void testFullInitialization() throws Exception {
//        String ruleSystemName = "testSystem";
//
//        PowerMock.mockStatic(RuleSystemMetaDataFactory.class);
//        rsMetaDataFactory = PowerMockito.mock(RuleSystemMetaDataFactory.class);
//
//        Mockito.when(metaDataDao.getRuleSystemMetaData(ruleSystemName))
//               .thenReturn(RuleSystemMetaDataMother.getDefaultMetaData());
//
//        sut = new RuleSystem(ruleSystemName, null);
//
//        Assert.assertNotNull(sut);
    }
}
