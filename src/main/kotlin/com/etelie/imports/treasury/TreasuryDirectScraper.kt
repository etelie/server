package com.etelie.imports.treasury

import com.etelie.network.WebContentNotFoundException
import it.skrape.core.document
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.Method
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.html5.p
import it.skrape.selects.html5.strong
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TreasuryDirectScraper {

    private val coroutineContext = Dispatchers.IO + CoroutineName(this::class.simpleName!!)

    suspend fun scrape(): TreasuryDirectScraperResult = withContext(coroutineContext) {
        skrape(AsyncFetcher) {
            request {
                method = Method.GET
                url = "https://treasurydirect.gov/savings-bonds/"
                userAgent = "Mozilla/5.0"
            }
            response {
                val mainRates: List<String?> = document.strong {
                    findAll {
                        filter {
                            it.ownText.contains(Regex("""Current Rate: \d+\.\d+%"""))
                        }.map {
                            Regex("""Current Rate: (.+)%""")
                                .find(it.ownText)
                                ?.groups
                                ?.get(1)
                                ?.value
                        }

                    }
                }
                val iBondFixedRate: String? = document.p {
                    findFirst("#main > section.colored.gray > div > div:nth-child(2) > div:nth-child(2) > div > div > div > p:nth-child(4)") {
                        let {
                            Regex("""This includes a fixed rate of (.+)%""")
                                .find(it.ownText)
                                ?.groups
                                ?.get(1)
                                ?.value
                        }
                    }
                }

                if (mainRates.size != 2 || mainRates.any { it == null } || iBondFixedRate == null) {
                    throw WebContentNotFoundException("${this::class.simpleName} failed to find interest rates")
                }

                TreasuryDirectScraperResult(
                    mainRates[0]!!,
                    mainRates[1]!!,
                    iBondFixedRate,
                )
            }
        }
    }

}

// https://treasurydirect.gov/savings-bonds/ee-bonds/
// #main > div > div > div.col-md-9 > section:nth-child(1) > div > div.grid-row-container.row > div:nth-child(2) > div > div > div.rate-box.card > div > div > div > div:nth-child(3)

// https://treasurydirect.gov/savings-bonds/i-bonds/
// #main > div > div > div.col-md-9 > section:nth-child(1) > div > div.grid-row-container.row > div:nth-child(2) > div > div > div > div > div > div > div:nth-child(3)
// #main > div > div > div.col-md-9 > section:nth-child(1) > div > div.grid-row-container.row > div:nth-child(2) > div > div > div > div > div > div > p:nth-child(4)
