package io.github.takusan23.mobilestatuswidget.tool

/**
 * 周波数からバンドを出す
 *
 * 相対表は Wiki パクった（おい）
 *
 * https://ja.wikipedia.org/wiki/LTEバンド#周波数帯域、チャネルの帯域幅
 *
 * */
object BandDictionary {

    /**
     * Bandと周波数の相対表。LTE（4G）版
     * */
    private val bandLTEList = listOf(
        BandDictionaryData(1, 2110f, 2170f),
        BandDictionaryData(2, 1930f, 1990f),
        BandDictionaryData(3, 1805f, 1880f),
        BandDictionaryData(4, 2110f, 2155f),
        BandDictionaryData(5, 869f, 894f),
        BandDictionaryData(7, 2620f, 2690f),
        BandDictionaryData(8, 925f, 960f),
        BandDictionaryData(10, 2110f, 2170f),
        BandDictionaryData(11, 1475.9f, 1495.9f),
        BandDictionaryData(12, 729f, 746f),
        BandDictionaryData(13, 746f, 756f),
        BandDictionaryData(14, 758f, 768f),
        BandDictionaryData(17, 734f, 746f),
        BandDictionaryData(18, 860f, 875f),
        BandDictionaryData(19, 875f, 890f),
        BandDictionaryData(20, 791f, 821f),
        BandDictionaryData(21, 1495.9f, 1510.9f),
        BandDictionaryData(22, 3510f, 3590f),
        BandDictionaryData(24, 1525f, 1559f),
        BandDictionaryData(25, 1930f, 1995f),
        BandDictionaryData(26, 859f, 894f),
        BandDictionaryData(27, 852f, 869f),
        BandDictionaryData(28, 758f, 803f),
        BandDictionaryData(29, 717f, 728f),
        BandDictionaryData(30, 2350f, 2360f),
        BandDictionaryData(31, 462.5f, 467.5f),
        BandDictionaryData(32, 1452f, 1496f),
        BandDictionaryData(33, 1900f, 1920f),
        BandDictionaryData(34, 2010f, 2025f),
        BandDictionaryData(35, 1850f, 1910f),
        BandDictionaryData(36, 1930f, 1990f),
        BandDictionaryData(37, 1910f, 1930f),
        BandDictionaryData(38, 2570f, 2620f),
        BandDictionaryData(39, 1880f, 1920f),
        BandDictionaryData(40, 2300f, 2400f),
        BandDictionaryData(41, 2496f, 2690f),
        BandDictionaryData(42, 3400f, 3600f),
        BandDictionaryData(43, 3600f, 3800f),
        BandDictionaryData(44, 703f, 803f),
        BandDictionaryData(45, 1447f, 1467f),
        BandDictionaryData(46, 5150f, 5925f),
        BandDictionaryData(47, 5855f, 5925f),
        BandDictionaryData(48, 3550f, 3700f),
        BandDictionaryData(49, 3550f, 3700f),
        BandDictionaryData(50, 1432f, 1517f),
        BandDictionaryData(51, 1427f, 1432f),
        BandDictionaryData(52, 3300f, 3400f),
        BandDictionaryData(65, 1920f, 2010f),
        BandDictionaryData(66, 1710f, 1780f),
    )

    /**
     * Bandと周波数の相対表。NR（5G）版
     * */
    val bandNRList = listOf(
        BandDictionaryData(1, 2110f, 2170f),
        BandDictionaryData(2, 1930f, 1990f),
        BandDictionaryData(3, 1805f, 1880f),
        BandDictionaryData(5, 869f, 894f),
        BandDictionaryData(7, 2620f, 2690f),
        BandDictionaryData(8, 925f, 960f),
        BandDictionaryData(12, 729f, 746f),
        BandDictionaryData(14, 758f, 768f),
        BandDictionaryData(18, 860f, 875f),
        BandDictionaryData(20, 791f, 821f),
        BandDictionaryData(25, 1930f, 1995f),
        BandDictionaryData(28, 758f, 803f),
        BandDictionaryData(29, 717f, 728f),
        BandDictionaryData(30, 2350f, 2360f),
        BandDictionaryData(34, 2010f, 2025f),
        BandDictionaryData(38, 2570f, 2620f),
        BandDictionaryData(39, 1880f, 1920f),
        BandDictionaryData(40, 2300f, 2400f),
        BandDictionaryData(41, 2496f, 2690f),
        BandDictionaryData(48, 3550f, 3700f),
        BandDictionaryData(50, 1432f, 1517f),
        BandDictionaryData(51, 1427f, 1432f),
        BandDictionaryData(65, 2110f, 2200f),
        BandDictionaryData(66, 2110f, 2200f),
        BandDictionaryData(70, 1995f, 2020f),
        BandDictionaryData(71, 617f, 652f),
        BandDictionaryData(74, 1475f, 1518f),
        BandDictionaryData(75, 1432f, 1517f),
        BandDictionaryData(76, 1427f, 1432f),
        BandDictionaryData(77, 3300f, 4200f),
        BandDictionaryData(78, 3300f, 3800f),
        BandDictionaryData(79, 4400f, 5000f),
    )

    /**
     * 周波数からバンドを出す。LTE版
     *
     * 1850 なら band 3
     * */
    fun toLTEBand(earfcn: Int): Int {
        return bandLTEList.find { bandDictionaryData ->
            // 範囲内にあれば
            earfcn.toFloat() in (bandDictionaryData.dlMin..bandDictionaryData.dlMax)
        }?.bandNum ?: 0
    }

    /**
     * 周波数からバンドを出す。5G版
     *
     * 実機を持っていないので知らない
     * */
    fun toNRBand(nrarfcn: Int): Int {
        return bandNRList.find { bandDictionaryData ->
            // 範囲内にあれば
            nrarfcn.toFloat() in (bandDictionaryData.dlMin..bandDictionaryData.dlMax)
        }?.bandNum ?: 0
    }

}

data class BandDictionaryData(
    val bandNum: Int,
    val dlMin: Float,
    val dlMax: Float,
)