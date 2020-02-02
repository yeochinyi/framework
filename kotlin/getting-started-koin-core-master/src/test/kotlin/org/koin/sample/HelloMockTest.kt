package org.koin.sample

import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.Mockito

class HelloMockTest : AutoCloseKoinTest() {

    val service: HelloService by inject()

    @Before
    fun before() {
        startKoin {
            modules(helloModule)
        }
        declareMock<HelloService>()
    }

    @Test
    fun tesKoinComponents() {
        HelloApplication().sayHello()

        Mockito.verify(service).hello()
    }
}