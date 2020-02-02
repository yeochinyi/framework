package org.koin.sample

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.test.AutoCloseKoinTest
import org.koin.test.inject

// https://start.insert-koin.io/#/quickstart/junit-test
class HelloAppTest : AutoCloseKoinTest() {

    val model by inject<HelloMessageData>()
    val service by inject<HelloService>()

    @Before
    fun before() {
        startKoin {
            modules(helloModule)
        }
    }

    @Test
    fun tesKoinComponents() {
        val helloApp = HelloApplication()
        helloApp.sayHello()

        assertEquals(service, helloApp.helloService)
        assertEquals("Hey, ${model.message}", service.hello())
    }
}