package com.example.safespace.core.data.notification

import com.example.safespace.core.domain.model.notification.NotificationPayload
import junit.framework.TestCase.assertEquals
import org.junit.Test

class FreedomNotificationParserTest {

    private val parser = FreedomNotificationParser()

    @Test
    fun `P1 - обычная покупка парсится в ExpenseItem`() {
        val payload = NotificationPayload(
            packageName = "ffinbank.MyFreedom",
            title = "Покупка Starbucks",
            text = "",
            bigText = "-1 500,00 ₸\n " +
                    "Карта: *1234\n " +
                    "Дата: 17.08.2026 14:59",
            postTime = 1
        )
        val result = parser.parse(payload)

        assertEquals(1500.0, result?.amount)
        assertEquals("Starbucks", result?.description)
    }

//    @Test
//    fun `P2 - NBSP как разделитель тысяч парсится корректно`() {}
//
//    @Test
//    fun `P3 - Сумма парсится валидно без тиын`() {}

}
