package sorting

import java.util.Scanner

var longs = mutableListOf<Long>()
var words = mutableListOf<String>()
val lines = mutableListOf<String>()

enum class InpType { WORD, LINE, LONG}

// default values
var curInpType = InpType.WORD
var isSortNatural = true

const val ARG_SORT_TYPE = "-sortingType"
const val ARG_DATA_TYPE = "-dataType"

fun main(args: Array<String>) {

    readLines()
    if (convertToWords()) {
        println("Empty input!")
        return
    }

    if (args.isNotEmpty())  {

        if (args.contains(ARG_SORT_TYPE)) {
            val i = args.indexOf(ARG_SORT_TYPE)
            if (i < args.lastIndex)
                if (args[i+1].equals("byCount"))
                    isSortNatural = false
        }

        if (args.contains(ARG_DATA_TYPE)) {
            val i = args.indexOf(ARG_DATA_TYPE)
            if (i < args.lastIndex) {
                if (args[i + 1].equals("long")) {
                    curInpType = InpType.LONG
                    convertToLongs()
                }
                else if (args[i + 1].equals("line")) {
                    curInpType = InpType.LINE
                }
            }
        }
    }

    if (isSortNatural)
        when (curInpType) {
            InpType.WORD ->
                printNatural(sortList(words), "words", " ")
            InpType.LINE ->
                printNatural(sortList(lines), "lines", "\n")
            InpType.LONG ->
                printNatural(sortList(longs), "numbers", " ")
        }
    else
        when (curInpType) {
            InpType.WORD -> printByCount(sortByCount(words), "words", words.size)
            InpType.LINE -> printByCount(sortByCount(lines), "lines", lines.size)
            InpType.LONG -> printByCount(sortByCount(longs), "numbers", longs.size)
        }
}

data class ElemNumber<T>(var elem: T, var int: Int)

fun <T> printByCount(list: MutableList<ElemNumber<T>>, s: String, numElem: Int) {

    println("Total $s: $numElem.")

    for (elem in list)
        println("${elem.elem}: ${elem.int} time(s), ${elem.int * 100 / numElem}%")
}

fun <T> sortByCount(list: MutableList<T>): MutableList<ElemNumber<T>> {
    val map = list.groupingBy { it }.eachCount()
    val res: MutableList<ElemNumber<T>> = mutableListOf()
    for (elem in map) res.add(ElemNumber(elem.key, elem.value))

    return sortList(res)
}

fun readLines(): Boolean {
    val scanner = Scanner(System.`in`)

    // reading lines
    while (scanner.hasNext()) {
        lines.add(scanner.nextLine())
    }
    return lines.isEmpty()
}

fun convertToWords(): Boolean {

    for (line in lines){
        val inpWords = line.trim().split(Regex("\\s+"))
        for (word in inpWords) {
            words.add(word)
        }
    }

    return words.isEmpty()
}

private fun convertToLongs(): Boolean {
    for (word in words) {
        val num = try {
            word.toLong()
        } catch (e: NumberFormatException) {
            println("Wrong input!")
            return true
        }
        longs.add(num)
    }
    return longs.isEmpty()
}

fun <T> printNatural(list: MutableList<T>, elemName: String, delim: String) {

    println("Total $elemName: ${list.size}.")
    print("Sorted data:")
    for (l in list) print("$delim$l")
    println("")
}

fun <T> sortList(list: MutableList<T>): MutableList<T> {
    return when (list.size) {
        1 -> list
        2 -> {
            if (isMore(list[0], list[1])){
                val l = list[0]
                list[0] = list[1]
                list[1] = l
            }
            list
        }
        else -> {
            val list1 = sortList(list.subList(0, list.size / 2))
            val list2 = sortList(list.subList(list.size / 2, list.size))
            mergeLists(list1, list2)
        }
    }
}

fun <T> isMore(arg1: T, arg2: T): Boolean {
    if (arg1 is Long && arg2 is Long) return arg1 > arg2
    if (arg1 is String && arg2 is String)
        return arg1.compareTo(arg2) > 0
    if (arg1 is ElemNumber<*> && arg2 is ElemNumber<*>) {
        if (arg1.int > arg2.int) return true
        else if (arg1.int == arg2.int) return isMore(arg1.elem, arg2.elem)
    }
    return false
}

fun <T> mergeLists(list1: MutableList<T>, list2: MutableList<T>): MutableList<T> {
    var i1 = 0
    var i2 = 0
    val res = mutableListOf<T>()
    while (i1 < list1.size || i2 < list2.size) {
        if (i1 == list1.size) res.add(list2[i2++])
        else if (i2 == list2.size) res.add(list1[i1++])
        else if (isMore(list1[i1], list2[i2])) res.add(list2[i2++])
        else res.add(list1[i1++])
    }
    return res
}

