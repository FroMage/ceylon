package com.redhat.ceylon.common.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.redhat.ceylon.tools.ToolsSuite;

@RunWith(Suite.class) 
@SuiteClasses({
    CeylonConfigTest.class,
    ConfigWriterTest.class,
    RepositoriesTest.class,
    KeystoresTest.class,
    ProxiesTest.class,
    AuthenticationTest.class
})
public class ConfigSuite {

}
