package org.koin.sample

import org.junit.Test
import org.koin.dsl.koinApplication
import org.koin.test.check.checkModules

class CheckModulesTest {

    @Test
    fun checkAllModules() {
        koinApplication {
            printLogger()
            modules(helloAppModule)
        }.checkModules()
    }
}