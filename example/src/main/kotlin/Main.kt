package punkhomov.grabbi.example

import kotlinx.coroutines.runBlocking
import punkhomov.grabbi.core.flatMap
import punkhomov.grabbi.core.isSuccess
import punkhomov.grabbi.core.onSuccess
import punkhomov.grabbi.example.apiweb.models.CardNumber
import java.time.LocalDate

fun main() {
    val sotk = SotkApiClient()

    Result.success(5)

    runBlocking {
        val signInResult = sotk.signInWebService("cgaxech", "123456", true)

        if (signInResult.isSuccess) {
            val number = CardNumber.from("100080512")
            val startDate = LocalDate.of(2024, 1, 1)
            val endDate = LocalDate.of(2024, 2, 1)

            sotk.addCard(number).flatMap {
                sotk.getCardInfo(number)
            }.onSuccess {
                println(it)
            }.flatMap {
                sotk.getCardTransactions(number, startDate, endDate)
            }.onSuccess {
                it.forEach { transaction -> println(transaction) }
            }

            sotk.signOutWebService()
        }
    }

//    runBlocking {
//        val user = sotk.signInWebService("cgaxech", "123456", true)
//        user.onSuccess { uu ->
//            println("Signed in as ${uu.username}")
//
//            val myCards = sotk.getMyCards()
//            myCards.onSuccess { cards ->
//                cards.forEach { card ->
//                    println(card)
//                }
//
//                cards.forEach { card ->
//                    val cardInfo = sotk.getCardInfo(card)
//                    cardInfo.onSuccess {
//                        println(it)
//                    }
//                }
//            }
//
//            sotk.signOutWebService()
//        }
//        user.onFailure {
//            println("Failed to sign in cause $it")
//        }
//    }
}


//Самара
//Тольятти
//Сызрань
//Новокуйбышевск
//Жигулевск

