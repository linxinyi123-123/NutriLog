package com.example.nutrilog.utils

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination

object PinyinUtils {
    
    private val format = HanyuPinyinOutputFormat().apply {
        caseType = HanyuPinyinCaseType.LOWERCASE
        toneType = HanyuPinyinToneType.WITHOUT_TONE
        vCharType = HanyuPinyinVCharType.WITH_V
    }
    
    /**
     * 将中文转换为拼音（首字母大写）
     */
    fun toPinyin(chinese: String): String {
        return try {
            val sb = StringBuilder()
            for (char in chinese) {
                when {
                    Character.toString(char).matches("[\\u4E00-\\u9FA5]".toRegex()) -> {
                        // 中文字符
                        val pinyinArray = PinyinHelper.toHanyuPinyinStringArray(char, format)
                        if (pinyinArray != null && pinyinArray.isNotEmpty()) {
                            sb.append(pinyinArray[0].capitalize())
                        } else {
                            sb.append(char)
                        }
                    }
                    char.isLetterOrDigit() -> {
                        // 字母或数字
                        sb.append(char)
                    }
                    else -> {
                        // 其他字符（如空格、标点）
                        sb.append(' ')
                    }
                }
            }
            sb.toString().trim().replace("\\s+".toRegex(), " ")
        } catch (e: BadHanyuPinyinOutputFormatCombination) {
            e.printStackTrace()
            chinese
        }
    }
    
    /**
     * 将中文转换为拼音首字母缩写
     */
    fun toPinyinAbbr(chinese: String): String {
        return try {
            val sb = StringBuilder()
            for (char in chinese) {
                when {
                    Character.toString(char).matches("[\\u4E00-\\u9FA5]".toRegex()) -> {
                        // 中文字符
                        val pinyinArray = PinyinHelper.toHanyuPinyinStringArray(char, format)
                        if (pinyinArray != null && pinyinArray.isNotEmpty()) {
                            sb.append(pinyinArray[0][0].uppercaseChar())
                        }
                    }
                    char.isLetter() -> {
                        // 字母
                        sb.append(char.uppercaseChar())
                    }
                }
            }
            sb.toString()
        } catch (e: BadHanyuPinyinOutputFormatCombination) {
            e.printStackTrace()
            chinese
        }
    }
    
    /**
     * 获取拼音搜索关键词（包含全拼和首字母）
     */
    fun getPinyinSearchKeywords(chinese: String): String {
        val fullPinyin = toPinyin(chinese)
        val abbrPinyin = toPinyinAbbr(chinese)
        return "$fullPinyin $abbrPinyin"
    }
    
    /**
     * 检查字符串是否包含中文字符
     */
    fun containsChinese(text: String): Boolean {
        return text.any { Character.toString(it).matches("[\\u4E00-\\u9FA5]".toRegex()) }
    }
}