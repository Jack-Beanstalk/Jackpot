package bee.design.jackpot

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

public class plantApi{
    private val APIKEY = "20201006PFT1IAXGAK0LGTQXIY9EG"
    private val plantUrl = "http://api.nongsaro.go.kr/service/garden/gardenList"
    private val plantDetailUrl = "http://api.nongsaro.go.kr/service/garden/gardenDtl"
    private val sType = "sCntntsSj"
    private var text: String? = null

    suspend fun searchPlant(newText: String?): ArrayList<PlantInfo> {

        var reqParam = URLEncoder.encode("apiKey", "UTF-8") + "=" + URLEncoder.encode(APIKEY, "UTF-8")
        reqParam += "&" + URLEncoder.encode("sText", "UTF-8") + "=" + URLEncoder.encode(newText, "UTF-8")
        reqParam += "&" + URLEncoder.encode("sType", "UTF-8") + "=" + URLEncoder.encode(sType, "UTF-8")
        var returnList = ArrayList<PlantInfo>()
        var plant:PlantInfo? = null

        try{
            val mURL = URL("$plantUrl?$reqParam")
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            parser.setInput(mURL.openStream(),null)
            var eventType = parser.eventType
            println("plantTest2")

            while(eventType != XmlPullParser.END_DOCUMENT){
                val tagname = parser.name
                when(eventType){
                    XmlPullParser.START_TAG -> if (tagname.equals("item",ignoreCase = true)){
                        plant = PlantInfo()
                    }
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> if(tagname.equals("item",ignoreCase = true)){
                        plant?.let{
                            print(it)
                            returnList.add(it)
                        }
                    }
                    else if(tagname.equals("cntntsSj")) {
                        plant!!.plantName = text
                    }
                    else if(tagname.equals("cntntsNo")){
                        plant!!.plantCode = text
                    }
                    else if(tagname.equals("rtnFileCours")){
                        var rtnFileCourList = text?.split('|')
                        plant!!.rtnFileCours = rtnFileCourList?.get(0)
                    }
                    else if(tagname.equals("rtnStreFileNm")){
                        var rtnStreFileNmList = text?.split('|')
                        plant!!.rtnStreFileNm = rtnStreFileNmList?.get(0)
                    }
                    else->{

                    }
                }
                eventType = parser.next()
            }
        }
        catch (e: XmlPullParserException) {
            e.printStackTrace()
        }
/*
        println()
        println("text ="+newText)
        for(plant in returnList){
            println("plantName ="+plant.plantName)
        }
        */

        return returnList
    }

    /*
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
    */




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
