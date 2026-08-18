package com.example.safespace.core.data.notification

import com.example.safespace.core.domain.model.notification.NotificationPayload
import com.example.safespace.core.domain.model.notification.ParseResult
import com.example.safespace.core.domain.model.notification.ParsedTransaction
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class FreedomNotificationParserTest {

    private val parser = FreedomNotificationParser()

    private fun createPayload(
        packageName: String = "ffinbank.MyFreedom",
        title: String? = null,
        text: String? = null,
        bigText: String? = null,
        postTime: Long = 1L
    ) = NotificationPayload(
        packageName = packageName,
        title = title,
        text = text,
        bigText = bigText,
        postTime = postTime
    )

    private fun ParseResult.expectSuccess(): ParsedTransaction {
        assertTrue("Expected Success but was $this", this is ParseResult.Success)
        return (this as ParseResult.Success).tx
    }

    private fun ParseResult.expectNotATransaction() {
        assertEquals(ParseResult.NotATransaction, this)
    }

    private fun ParseResult.expectUnrecognized(reasonContains: String) {
        assertTrue("Expected Unrecognized but was $this", this is ParseResult.Unrecognized)
        val actual = (this as ParseResult.Unrecognized).reason
        assertTrue(
            "Expected reason to contain '$reasonContains' but was '$actual'",
            actual.contains(reasonContains, ignoreCase = true)
        )
    }

    @Test
    fun `P1 - обычная покупка парсится в ExpenseItem`() {
        val payload = createPayload(
            title = "Покупка Starbucks",
            bigText = "-1 500,00 ₸\n " +
                    "Карта: *1234\n " +
                    "Дата: 17.08.2026 14:59",
        )

        val result = parser.parse(payload).expectSuccess()

        assertEquals(1500.0, result.amount)
        assertEquals("Starbucks", result.merchant)
        assertEquals("KZT", result.currency)
    }

    @Test
    fun `P2 - NBSP как разделитель тысяч парсится корректно`() {
        val payload = createPayload(
            title = "Покупка Wildberries",
            bigText = "-12\u00A0345,00 ₸\n " +
                    "Карта: *1234\n " +
                    "Дата: 17.08.2026 14:59",
        )
        val result = parser.parse(payload).expectSuccess()

        assertEquals(12345.0, result.amount)
        assertEquals("Wildberries", result.merchant)
        assertEquals("KZT", result.currency)
    }

    @Test
    fun `P3 - Сумма парсится валидно без тиын`() {
        val payload = createPayload(
            title = "Покупка METRO",
            bigText = "-500 ₸\n " +
                    "Карта: *1234\n " +
                    "Дата: 17.08.2026 14:59",
        )
        val result = parser.parse(payload).expectSuccess()

        assertEquals(500.0, result.amount)
        assertEquals("METRO", result.merchant)
    }

    @Test
    fun `P4 - фоллбэк на text когда bigText пустой`() {
        val payload = createPayload(
            title = "Покупка SMALL",
            text = "-5240 ₸\n " +
                    "Карта: *1234\n " +
                    "Дата: 18.08.2026 14:59",
            bigText = null,
        )

        val result = parser.parse(payload).expectSuccess()

        assertEquals(5240.0, result.amount)
        assertEquals("SMALL", result.merchant)
    }

    @Test
    fun `P5 - название мерчанта в несколько слов не ломает парс`() {
        val payload = createPayload(
            title = "Покупка Coffee Boom Almaty",
            bigText = "-11230 ₸\n " +
                    "Карта: *1234\n " +
                    "Дата: 18.08.2026 17:23",
        )
        val result = parser.parse(payload).expectSuccess()
        assertEquals(11230.0, result.amount)
        assertEquals("Coffee Boom Almaty", result.merchant)
        assertEquals("KZT", result.currency)
    }

    @Test
    fun `N1 - кэшбек не попадет в расходы`() {
        val payload = createPayload(
            title = "Кэшбек за покупку",
            bigText = "-11230 ₸\n " +
                    "Freedom SuperCard\n" +
                    "Дата: 18.08.2026 17:23",
        )

        parser.parse(payload).expectNotATransaction()
    }

    @Test
    fun `N2 - перевод на карту другого банка не заходит в расходы`() {
        val payload = createPayload(
            title = "Перевод на карту другого банка",
            bigText = "-5000 ₸\n " +
                    "Карта: *1234\n " +
                    "Дата: 18.08.2026 11:33",
        )
        parser.parse(payload).expectNotATransaction()
    }

    @Test
    fun `N3 - промо-уведомления не пройдут в расчет расходов`() {
        val payload = createPayload(
            title = "Отель со скидкой 30% предлагаем только сегодня"
        )
        parser.parse(payload).expectNotATransaction()
    }

    @Test
    fun `N4 - при отсутствии названия мерчанта не падает`() {
        val payload = createPayload(
            title = "Покупка   ",
            bigText = "-1124 ₸\n " +
                    "Карта: *1234\n " +
                    "Дата: 18.08.2026 13:21",
        )

        parser.parse(payload).expectUnrecognized("empty merchant")
    }

    @Test
    fun `N5 - без заголовка уведомления парсер не упадет`() {
        val payload = createPayload()
        parser.parse(payload).expectUnrecognized("no title")
    }

    @Test
    fun `N5b - без описания операции в уведомлении парсер не упадет`() {
        val payload = createPayload(
            title = "Покупка Fist"
        )
        parser.parse(payload).expectUnrecognized("no notification body")
    }

    @Test
    fun `N6 - сумма расхода начинается не с минуса`() {
        val payload = createPayload(
            title = "Покупка Meloman",
            bigText = "+124 ₸\n " +
                    "Карта: *1234\n " +
                    "Дата: 18.08.2026 13:31",
        )
        parser.parse(payload).expectNotATransaction()
    }

    @Test
    fun `N7 - вместо суммы пришло слово`() {
        val payload = createPayload(
            title = "Покупка GreenMart",
            bigText = "-threehundred ₸\n " +
                    "Карта: *1234\n " +
                    "Дата: 18.08.2026 11:11",
        )
        parser.parse(payload).expectUnrecognized("cannot parse amount")
    }

    @Test
    fun `H1 - freedom-пакет распознается как свой`() {
        val payload = createPayload(
            packageName = "ffinbank.MyFreedom"
        )
        val result = parser.canHandle(payload.packageName)
        assertTrue(result)
    }

    @Test
    fun `H2 - пакет чужого банка отклоняется`() {
        val payload = createPayload(
            packageName = "kz.kaspi.mobile"
        )
        val result = parser.canHandle(payload.packageName)
        assertFalse(result)
    }

    @Test
    fun `D1 - транзитивный перевод между счетами не считается расходом`() {
        val payload = createPayload(
            title = "Перевод между своими счетами",
            bigText = "+2,2 $\n " +
                    "Доступно 7,68 $\n" +
                    "Пополнено SuperCard **9608",
        )
        parser.parse(payload).expectNotATransaction()
    }
}
