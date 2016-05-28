package com.github.kislayverma.rulette.core;

import com.github.kislayverma.rulette.core.RuleSystem;
import com.github.kislayverma.rulette.core.dao.DataSource;
import com.github.kislayverma.rulette.core.dao.MetaDataDao;
import com.github.kislayverma.rulette.core.dao.impl.mysql.BaseDaoImpl;
import com.github.kislayverma.rulette.core.dao.impl.mysql.MetaDataDaoImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.mockito.MockitoAnnotations;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.support.membermodification.MemberModifier;

/**
 *
 * @author kislay.verma
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({
    //RuleSystemMetaDataFactory.class,
    //RuleSystemMetaDataFactory.class,
    //RuleSystemMetaDataFactory.class,
    //RuleSystemMetaDataFactory.class,
    DataSource.class,
    MetaDataDaoImpl.class,
    BaseDaoImpl.class})
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
        //BaseDaoMySqlImpl bdmi = PowerMockito.mock(BaseDaoImpl.class);
        // Suppress the constructor of the Base dao so that db conections arent set up
        MemberModifier.suppress(BaseDaoImpl.class.getConstructor());
        MemberModifier.suppress(MetaDataDaoImpl.class.getConstructor());
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
