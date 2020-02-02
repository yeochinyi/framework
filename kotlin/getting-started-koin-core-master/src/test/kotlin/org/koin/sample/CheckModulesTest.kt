package org.koin.sample

import org.junit.Test
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.test.AutoCloseKoinTest
import org.koin.test.check.checkModules

/**
 * Dry run configuration
 */
class CheckModulesTest : AutoCloseKoinTest() {

    @Test
    fun dryRunTest() {
        koinApplication {
            printLogger(Level.DEBUG)
            modules(helloModule)
        }.checkModules()
    }
}