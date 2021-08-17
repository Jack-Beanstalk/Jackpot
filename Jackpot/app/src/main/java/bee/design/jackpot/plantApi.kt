package bee.design.jackpot

import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

public class plantApi {
    private val APIKEY = "20201006PFT1IAXGAK0LGTQXIY9EG"
    private val plantUrl = "http://api.nongsaro.go.kr/service/garden/gardenList"
    private val plantDetailUrl = "http://api.nongsaro.go.kr/service/garden/gardenDtl"
    private val sType = "sCntntsSj"

    public fun searchPlant(newText: String): List<PlantInfo> {

        var reqParam = URLEncoder.encode("apiKey", "UTF-8") + "=" + URLEncoder.encode(APIKEY, "UTF-8")
        reqParam += "&" + URLEncoder.encode("sText", "UTF-8") + "=" + URLEncoder.encode(newText, "UTF-8")
        reqParam += "&" + URLEncoder.encode("sType", "UTF-8") + "=" + URLEncoder.encode(sType, "UTF-8")
        var returnList: List<PlantInfo> = mutableListOf()

        val mURL = URL("$plantUrl?$reqParam")

        with(mURL.openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "GET"

            println("URL : $url")
            println("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                println("Response : $response")

            }
        }

        return returnList
    }

    public fun getPlantDetail(plant: PlantInfo): PlantInfo {

        var reqParam = URLEncoder.encode("apiKey", "UTF-8") + "=" + URLEncoder.encode(APIKEY, "UTF-8")
        reqParam += "&" + URLEncoder.encode("cntntsNo", "UTF-8") + "=" + URLEncoder.encode(plant.plantCode, "UTF-8")

        val mURL = URL("$plantDetailUrl?$reqParam")

        with(mURL.openConnection() as HttpURLConnection) {
            // optional default is GET
            requestMethod = "GET"

            println("URL : $url")
            println("Response Code : $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                println("Response : $response")

            }
        }

        return plant
    }

}
